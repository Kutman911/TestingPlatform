package com.project.controller.student;

import com.project.controller.util.UserContextAware;
import com.project.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AvailableTestsController implements UserContextAware {

    @FXML
    private TableView<TestInfo> availableTestsTable;
    @FXML
    private TableColumn<TestInfo, String> testNameColumn;
    @FXML
    private TableColumn<TestInfo, String> courseNameColumn;
    @FXML
    private TableColumn<TestInfo, Integer> durationColumn;

    @FXML
    private Label selectedTestNameLabel;
    @FXML
    private ListView<String> questionsListView;
    @FXML
    private Button btnStartTest;

    private User loggedInStudent;
    private ObservableList<TestInfo> availableTestList;
    private ObservableList<String> questionsForSelectedTestList;

    @Override
    public void setUserContext(User user) {
        this.loggedInStudent = user;
        if (user != null) {
            System.out.println("Student context set: " + user.getUsername());
        }
    }

    public void initialize() {
        availableTestList = FXCollections.observableArrayList();
        questionsForSelectedTestList = FXCollections.observableArrayList();

        testNameColumn.setCellValueFactory(new PropertyValueFactory<>("testName"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

        availableTestsTable.setItems(availableTestList);
        questionsListView.setItems(questionsForSelectedTestList);

        // Добавляем тестовые данные
        availableTestList.addAll(
                new TestInfo("Java Basics", "Programming", 30),
                new TestInfo("English Grammar", "Language Arts", 45),
                new TestInfo("Math Challenge", "Mathematics", 60)
        );

        // Слушатель для выбора теста в таблице
        availableTestsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showTestDetails(newValue)
        );

        // Изначально кнопка "Start Test" должна быть неактивна
        btnStartTest.setDisable(true);
    }

    private void showTestDetails(TestInfo testInfo) {
        if (testInfo != null) {
            selectedTestNameLabel.setText("Selected Test: " + testInfo.getTestName());
            btnStartTest.setDisable(false);
            // В этом упрощенном варианте мы не будем отображать список вопросов здесь
            questionsForSelectedTestList.clear();
            if (testInfo.getTestName().equals("Java Basics")) {
                questionsForSelectedTestList.addAll("Question 1 (Java)", "Question 2 (Java)", "Question 3 (Java)");
            } else if (testInfo.getTestName().equals("English Grammar")) {
                questionsForSelectedTestList.addAll("Question 1 (English)", "Question 2 (English)", "Question 3 (English)");
            } else if (testInfo.getTestName().equals("Math Challenge")) {
                questionsForSelectedTestList.addAll("Question 1 (Math)", "Question 2 (Math)", "Question 3 (Math)");
            }
        } else {
            selectedTestNameLabel.setText("Selected Test: None");
            btnStartTest.setDisable(true);
            questionsForSelectedTestList.clear();
        }
    }

    @FXML
    void handleStartOrViewTest(ActionEvent event) {
        TestInfo selectedTest = availableTestsTable.getSelectionModel().getSelectedItem();
        if (selectedTest != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/view/student/TestTakingView.fxml"));
                Parent testTakingRoot = loader.load();

                TestTakingController controller = loader.getController();
                controller.setCurrentTest(selectedTest.getTestName());

                Scene scene = new Scene(testTakingRoot);
                Stage stage = (Stage) btnStartTest.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle(selectedTest.getTestName());
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load test taking view: " + e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Вспомогательный класс для хранения информации о тесте
    public static class TestInfo {
        private String testName;
        private String courseName;
        private int duration;

        public TestInfo(String testName, String courseName, int duration) {
            this.testName = testName;
            this.courseName = courseName;
            this.duration = duration;
        }

        public String getTestName() {
            return testName;
        }

        public String getCourseName() {
            return courseName;
        }

        public int getDuration() {
            return duration;
        }
    }
}