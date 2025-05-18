package com.project.controller.manager;

import com.project.dao.CourseDao;
import com.project.dao.CourseDaoImpl;
import com.project.model.Course;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane; // Или другой корневой элемент для диалога
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime; // Если будете отображать даты
import java.util.List;
import java.util.Optional;

public class ManageCoursesController {

    @FXML
    private TableView<Course> coursesTable;
    @FXML
    private TableColumn<Course, Integer> idColumn;
    @FXML
    private TableColumn<Course, String> nameColumn;
    @FXML
    private TableColumn<Course, String> descriptionColumn;

    // @FXML private TableColumn<Course, LocalDateTime> createdAtColumn;
    // @FXML private TableColumn<Course, LocalDateTime> updatedAtColumn;


    private CourseDao courseDao;
    private ObservableList<Course> courseList;

    public void initialize() {
        courseDao = new CourseDaoImpl();
        courseList = FXCollections.observableArrayList();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));


        coursesTable.setItems(courseList);
        loadCourses();
    }

    @FXML
    private void loadCourses() {
        try {
            List<Course> coursesFromDb = courseDao.findAll();
            courseList.setAll(coursesFromDb);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load courses: " + e.getMessage());
        }
    }

    @FXML
    void handleRefreshCourses(ActionEvent event) {
        loadCourses();
    }

    @FXML
    void handleAddCourse(ActionEvent event) {
        showCourseEditDialog(null);
    }

    @FXML
    void handleEditCourse(ActionEvent event) {
        Course selectedCourse = coursesTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            showCourseEditDialog(selectedCourse);
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a course to edit.");
        }
    }

    @FXML
    void handleDeleteCourse(ActionEvent event) {
        Course selectedCourse = coursesTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirm Deletion");
            confirmationDialog.setHeaderText("Delete Course: " + selectedCourse.getCourseName());
            confirmationDialog.setContentText("Are you sure you want to delete this course? Associated tests might be affected."); // Предупреждение о связях

            Optional<ButtonType> result = confirmationDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    boolean deleted = courseDao.delete(selectedCourse.getCourseId());
                    if (deleted) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Course deleted successfully.");
                        loadCourses();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Could not delete the course from the database.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Проверка на наличие связанных записей (например, тестов), если FK настроены с RESTRICT
                    if (e.getSQLState().equals("23503")) { // foreign_key_violation для PostgreSQL
                        showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Cannot delete course. It is referenced by other records (e.g., tests).");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete course: " + e.getMessage());
                    }
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a course to delete.");
        }
    }

    private boolean showCourseEditDialog(Course course) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/project/view/manager/CourseEditDialog.fxml"));
            // Убедитесь, что корневой элемент CourseEditDialog.fxml - GridPane или измените тип ниже
            GridPane dialogPane = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(course == null ? "Add New Course" : "Edit Course");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            // Stage primaryStage = (Stage) coursesTable.getScene().getWindow();
            // dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(dialogPane);
            dialogStage.setScene(scene);

            CourseEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setCourse(course);

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                loadCourses();
            }
            return controller.isSaveClicked();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open course edit dialog: " + e.getMessage());
            return false;
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}