package com.project.controller.student;

import com.project.controller.util.UserContextAware;
import com.project.dao.QuestionDao;
import com.project.dao.QuestionDaoImpl;
import com.project.dao.TestDao;
import com.project.dao.TestDaoImpl;
import com.project.model.Question;
import com.project.model.Test;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AvailableTestsController implements UserContextAware {

    @FXML
    private TableView<Test> availableTestsTable;
    @FXML
    private TableColumn<Test, String> testNameColumn;
    @FXML
    private TableColumn<Test, String> courseNameColumn;
    @FXML
    private TableColumn<Test, Integer> durationColumn;

    @FXML
    private Label selectedTestNameLabel;
    @FXML
    private ListView<String> questionsListView;
    @FXML
    private Button btnStartTest;
    private TestDao testDao;
    private User loggedInStudent;
    private QuestionDao questionDao;
    private ObservableList<Test> availableTestList;
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
        testDao = new TestDaoImpl();

        testNameColumn.setCellValueFactory(new PropertyValueFactory<>("testName"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));


        questionDao = new QuestionDaoImpl();

        availableTestsTable.setItems(availableTestList);
        questionsListView.setItems(questionsForSelectedTestList);
        availableTestsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showTestDetails(newValue)
        );

        loadAvailableTests();

        btnStartTest.setDisable(true);
    }

    private void loadAvailableTests() {
        try {

            List<Test> testsFromDb = testDao.findAllActive();


            availableTestList.setAll(testsFromDb);
            System.out.println("Loaded " + testsFromDb.size() + " active tests from DB.");

        } catch (SQLException e) {
            e.printStackTrace();

            showAlert(Alert.AlertType.ERROR, "Database error", "Failed to load the list of available tests : " + e.getMessage());
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showTestDetails(Test test) {
        questionsForSelectedTestList.clear();

        if (test != null) {
            selectedTestNameLabel.setText("Selected Test: " + test.getTestName());
            btnStartTest.setDisable(false);

            try {
                List<Question> questions = questionDao.getQuestionsByTestId(test.getTestId());

                if (questions != null && !questions.isEmpty()) {
                    for (Question q : questions) {
                        String questionText = q.getQuestionText();
                        if (questionText.length() > 100) { // Пример: ограничить 100 символами
                            questionText = questionText.substring(0, 100) + "...";
                        }
                        questionsForSelectedTestList.add(questionText);
                    }
                    System.out.println("Loaded " + questions.size() + " questions details for test ID: " + test.getTestId());
                } else {
                    // Если у теста нет вопросов
                    questionsForSelectedTestList.add("No questions defined for this test.");
                    System.out.println("No questions found for test ID: " + test.getTestId());
                }

            } catch (SQLException e) {
                e.printStackTrace();

                System.err.println("Failed to load question details for test ID " + test.getTestId() + ": " + e.getMessage());
                questionsForSelectedTestList.add("Error loading questions.");
            }


        } else {
            selectedTestNameLabel.setText("Selected Test: None");
            btnStartTest.setDisable(true);
        }
    }

    @FXML
    void handleStartOrViewTest(ActionEvent event) {
        Test selectedTest = availableTestsTable.getSelectionModel().getSelectedItem();
        if (selectedTest != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/view/student/TestTakingView.fxml"));
                Parent testTakingRoot = loader.load();

                TestTakingController controller = loader.getController();
                controller.setCurrentTest(selectedTest);

                if (controller instanceof UserContextAware && loggedInStudent != null) {
                    ((UserContextAware) controller).setUserContext(loggedInStudent);
                } else if (loggedInStudent == null) {
                    System.err.println("Error: loggedInStudent is null when starting test.");
                }

                Scene scene = new Scene(testTakingRoot);
                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Doing test: " + selectedTest.getTestName());
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load test taking view: " + e.getMessage());
            } catch (ClassCastException e) {
                e.printStackTrace();
                System.err.println("Error casting event source to Button in handleStartOrViewTest.");
                showAlert(Alert.AlertType.ERROR, "Internal Error", "Could not get window to start test.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a test to start.");
        }
    }

}