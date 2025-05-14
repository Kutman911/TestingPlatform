package com.project.controller.student;// MyResultsController.java
import com.project.dao.StudentTestDao;
import com.project.model.TestResult;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class MyResultsController implements Initializable {
    @FXML private TableView<TestResult> resultsTable;
    @FXML private TableColumn<TestResult, String> resTitleColumn;
    @FXML private TableColumn<TestResult, Integer> resScoreColumn;
    @FXML private TableColumn<TestResult, LocalDateTime> resDateColumn;

    private int studentId;
    private StudentTestDao resultDAO = new StudentTestDao();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Bind columns to TestResult properties (requires getTestTitle(), getScore(), getAttemptDate())
        resTitleColumn.setCellValueFactory(new PropertyValueFactory<>("testTitle"));
        resScoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        resDateColumn.setCellValueFactory(new PropertyValueFactory<>("attemptDate"));

    }


    public void setStudentId(int id) {
        this.studentId = id;
        loadResults();
    }

    private void loadResults() {
        resultsTable.getItems().clear();
        List<TestResult> results = resultDAO.getResultsForStudent(studentId);
        resultsTable.getItems().addAll(results);
    }
}
