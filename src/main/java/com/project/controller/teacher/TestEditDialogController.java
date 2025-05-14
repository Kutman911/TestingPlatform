package com.project.controller.teacher;

import com.project.dao.TestDao;
import com.project.dao.TestDaoImpl;
import com.project.model.Course;
import com.project.model.Test;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;


import java.sql.SQLException;
import java.util.List;

public class TestEditDialogController {

    @FXML
    private TextField testNameField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ComboBox<Course> courseComboBox;
    @FXML
    private Spinner<Integer> durationSpinner;
    @FXML
    private CheckBox activeCheckBox;
    @FXML
    private Button saveButton;

    private Stage dialogStage;
    private Test test;
    private boolean saveClicked = false;
    private TestDao testDao;
    private int loggedInTeacherId;

    private ObservableList<Course> courseObservableList;

    @FXML
    private void initialize() {
        testDao = new TestDaoImpl();
        courseObservableList = FXCollections.observableArrayList();

        // Настройка ComboBox для отображения имен курсов, но хранения объектов Course
        courseComboBox.setConverter(new StringConverter<Course>() {
            @Override
            public String toString(Course course) {
                return course == null ? "No Course (Independent Test)" : course.getCourseName();
            }

            @Override
            public Course fromString(String string) {
                // Этот метод не будет использоваться для выбора, т.к. мы работаем с объектами
                return courseObservableList.stream()
                        .filter(c -> c != null && c.getCourseName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
        courseComboBox.setItems(courseObservableList);

        // Добавляем "пустой" элемент для возможности не выбирать курс
        courseObservableList.add(null); // Представляет "No Course"
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setLoggedInTeacherId(int teacherId) {
        this.loggedInTeacherId = teacherId;
    }

    public void setCourses(List<Course> courses) {
        courseObservableList.addAll(courses); // Добавляем реальные курсы после null
    }

    public void setTest(Test test) {
        this.test = test;
        if (test != null) { // Режим редактирования
            testNameField.setText(test.getTestName());
            descriptionArea.setText(test.getDescription());
            durationSpinner.getValueFactory().setValue(test.getDurationMinutes());
            activeCheckBox.setSelected(test.isActive());

            // Установка выбранного курса в ComboBox
            if (test.getCourseId() != null) {
                courseObservableList.stream()
                        .filter(c -> c != null && c.getCourseId() == test.getCourseId())
                        .findFirst()
                        .ifPresent(courseComboBox::setValue);
            } else {
                courseComboBox.setValue(null); // Выбираем "No Course"
            }
        } else { // Режим добавления
            testNameField.clear();
            descriptionArea.clear();
            // Значения по умолчанию для Spinner и CheckBox уже установлены в FXML или их можно установить здесь
            durationSpinner.getValueFactory().setValue(60); // Значение по умолчанию
            activeCheckBox.setSelected(true);
            courseComboBox.setValue(null); // "No Course" по умолчанию
        }
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (isInputValid()) {
            String testName = testNameField.getText();
            String description = descriptionArea.getText();
            Course selectedCourse = courseComboBox.getValue();
            Integer courseId = (selectedCourse == null) ? null : selectedCourse.getCourseId();
            int duration = durationSpinner.getValue();
            boolean isActive = activeCheckBox.isSelected();

            try {
                if (test == null) { // Новый тест
                    Test newTest = new Test(courseId, loggedInTeacherId, testName, description, duration, isActive);
                    testDao.save(newTest);
                } else { // Обновление существующего теста
                    test.setTestName(testName);
                    test.setDescription(description);
                    test.setCourseId(courseId);
                    test.setDurationMinutes(duration);
                    test.setActive(isActive);
                    // creatorId не меняем при редактировании
                    testDao.update(test);
                }
                saveClicked = true;
                dialogStage.close();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Could not save test data: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";
        if (testNameField.getText() == null || testNameField.getText().trim().isEmpty()) {
            errorMessage += "Test name cannot be empty!\n";
        }
        if (durationSpinner.getValue() == null || durationSpinner.getValue() <= 0) {
            errorMessage += "Duration must be a positive number!\n";
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