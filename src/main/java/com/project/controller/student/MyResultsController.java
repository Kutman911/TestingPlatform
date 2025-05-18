package com.project.controller.student;

import com.project.dao.StudentTestDao;
import com.project.dao.StudentTestDaoImpl;
import com.project.model.TestResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.sql.SQLException;
import javafx.scene.control.Alert;

public class MyResultsController implements Initializable {

    @FXML
    private TableView<TestResult> resultsTable;
    @FXML
    private TableColumn<TestResult, String> resTitleColumn;
    @FXML
    private TableColumn<TestResult, Integer> resScoreColumn;
    @FXML
    private TableColumn<TestResult, LocalDateTime> resDateColumn;

    private int studentId;
    private StudentTestDao resultDAO;
    private ObservableList<TestResult> resultsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resultDAO = new StudentTestDaoImpl();
        resultsList = FXCollections.observableArrayList();
        resultsTable.setItems(resultsList);

        resTitleColumn.setCellValueFactory(new PropertyValueFactory<>("testTitle"));
        resScoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        resDateColumn.setCellValueFactory(new PropertyValueFactory<>("attemptDate"));
    }

    public void setStudentId(int id) {
        this.studentId = id;
        if (this.studentId > 0) {
            loadResults();
        } else {
            System.err.println("Invalid student ID received in MyResultsController: " + id);
            showAlert(Alert.AlertType.ERROR, "User error", "Index is incorrect.");
        }
    }

    private void loadResults() {
        resultsList.clear();
        try {
            List<TestResult> results = resultDAO.getResultsForStudent(studentId);
            resultsList.addAll(results);
            System.out.println("Loaded " + results.size() + " test results for student ID: " + studentId);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database error", "Failed to load tests results: " + e.getMessage());
        }
        if (resultsList.isEmpty()) {
            System.out.println("No test results found for student ID: " + studentId);
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