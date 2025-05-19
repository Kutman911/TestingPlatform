package com.project.controller.manager;

import com.project.dao.TestDao;
import com.project.dao.TestDaoImpl;
import com.project.model.Course;
import com.project.model.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.util.List;

public class AssignmentEditDialogController {

    @FXML
    private Label testNameLabel;
    @FXML
    private ComboBox<Course> courseComboBox;
    @FXML
    private CheckBox activeCheckBox;

    private Stage dialogStage;
    private Test test;
    private boolean saveClicked = false;
    private TestDao testDao;
    private ObservableList<Course> courseList;


    @FXML
    private void initialize() {
        testDao = new TestDaoImpl();
        courseList = FXCollections.observableArrayList();
        courseComboBox.setItems(courseList);

        courseComboBox.setConverter(new StringConverter<Course>() {
            @Override
            public String toString(Course course) {
                return course != null ? course.getCourseName() : "";
            }

            @Override
            public Course fromString(String s) {
                return null;
            }
        });
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTest(Test test) {
        this.test = test;
        if (test != null) {
            testNameLabel.setText(test.getTestName());
            activeCheckBox.setSelected(test.isActive());

            Course currentCourse = findCourseById(test.getCourseId());
            if (currentCourse != null) {
                courseComboBox.getSelectionModel().select(currentCourse);
            } else {
                courseComboBox.getSelectionModel().clearSelection();
            }

        } else {
            System.err.println("AssignmentEditDialogController set with null test.");
            testNameLabel.setText("Error: No Test");
            activeCheckBox.setDisable(true);
            courseComboBox.setDisable(true);
        }
    }

    public void setCourses(List<Course> courses) {
        if (courses != null) {
            courseList.setAll(courses);
        } else {
            courseList.clear();
        }
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (isInputValid()) {
            test.setCourseId(courseComboBox.getValue() != null ? courseComboBox.getValue().getCourseId() : null);
            test.setActive(activeCheckBox.isSelected());

            try {
                boolean updated = testDao.update(test);
                if (updated) {
                    saveClicked = true;
                    dialogStage.close();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Save Failed", "Could not update test assignment in database.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save test assignment: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        saveClicked = false;
        dialogStage.close();
    }

    private boolean isInputValid() {
        return true;
    }

    private Course findCourseById(Integer courseId) {
        if (courseId == null || courseList == null) return null;
        for (Course course : courseList) {
            if (course != null && course.getCourseId() == courseId) {
                return course;
            }
        }
        return null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}