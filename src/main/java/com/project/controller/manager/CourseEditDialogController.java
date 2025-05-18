package com.project.controller.manager;

import com.project.dao.CourseDao;
import com.project.dao.CourseDaoImpl;
import com.project.model.Course;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class CourseEditDialogController {

    @FXML
    private TextField courseNameField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Button saveButton;

    private Stage dialogStage;
    private Course course; // Редактируемый курс (null для нового)
    private boolean saveClicked = false;
    private CourseDao courseDao;

    @FXML
    private void initialize() {
        courseDao = new CourseDaoImpl();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setCourse(Course course) {
        this.course = course;
        if (course != null) { // Режим редактирования
            courseNameField.setText(course.getCourseName());
            descriptionArea.setText(course.getDescription());
        } else { // Режим добавления
            courseNameField.clear();
            descriptionArea.clear();
        }
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (isInputValid()) {
            String courseName = courseNameField.getText();
            String description = descriptionArea.getText();

            try {
                if (course == null) { // Новый курс
                    // Проверка на уникальность имени курса
                    if (courseDao.findByName(courseName).isPresent()) {
                        showAlert(Alert.AlertType.ERROR, "Validation Error", "Course with this name already exists.");
                        return;
                    }
                    Course newCourse = new Course(courseName, description);
                    courseDao.save(newCourse);
                } else { // Обновление существующего курса
                    // Если имя изменено, проверить на уникальность нового имени
                    if (!course.getCourseName().equals(courseName) && courseDao.findByName(courseName).isPresent()) {
                        showAlert(Alert.AlertType.ERROR, "Validation Error", "Another course with this new name already exists.");
                        return;
                    }
                    course.setCourseName(courseName);
                    course.setDescription(description);
                    courseDao.update(course);
                }
                saveClicked = true;
                dialogStage.close();
            } catch (SQLException e) {
                e.printStackTrace();
                String userMessage = "Could not save course data: " + e.getMessage();
                if ("23505".equals(e.getSQLState())) { // Код ошибки уникальности для PostgreSQL
                    userMessage = "A course with the name '" + courseName + "' already exists.";
                }
                showAlert(Alert.AlertType.ERROR, "Database Error", userMessage);
            }
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (courseNameField.getText() == null || courseNameField.getText().trim().isEmpty()) {
            errorMessage += "Course name cannot be empty!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert(Alert.AlertType.ERROR, "Invalid Fields", errorMessage);
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