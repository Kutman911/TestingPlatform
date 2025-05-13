package com.project.controller.teacher;

import com.project.dao.SubmissionDao;
import com.project.model.Submission;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ViewSubmissionsController {
    @FXML private TableView<Submission> tableSubmissions;
    @FXML private TableColumn<Submission, String> colStudent;
    @FXML private TableColumn<Submission, String> colTest;
    @FXML private TableColumn<Submission, Double> colScore;
    @FXML private TableColumn<Submission, String> colStatus;

    @FXML
    public void initialize() {
        colStudent.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colTest.setCellValueFactory(new PropertyValueFactory<>("testName"));
        colScore.setCellValueFactory(new PropertyValueFactory<>("score"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadSubmissions();
    }

    private void loadSubmissions() {
        tableSubmissions.setItems(FXCollections.observableArrayList(
                SubmissionDao.getAllSubmissions()
        ));
    }
}
