package com.project.controller.teacher;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;

import com.project.controller.util.UserContextAware;
import com.project.dao.SubmissionDao;
import com.project.dao.SubmissionDaoImpl;
import com.project.model.Submission;
import com.project.model.User;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;


public class ViewSubmissionsController implements Initializable, UserContextAware {

    @FXML
    private TableView<Submission> tableSubmissions;
    @FXML
    private TableColumn<Submission, String> colStudent;
    @FXML
    private TableColumn<Submission, String> colTest;
    @FXML
    private TableColumn<Submission, String> colScore;

    private SubmissionDao submissionDao;
    private ObservableList<Submission> submissionList;

    private User loggedInUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        submissionDao = new SubmissionDaoImpl();
        submissionList = FXCollections.observableArrayList();
        tableSubmissions.setItems(submissionList);

        colStudent.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colTest.setCellValueFactory(new PropertyValueFactory<>("testName"));
        colScore.setCellValueFactory(new PropertyValueFactory<>("scoreDisplay"));
    }

    @Override
    public void setUserContext(User user) {
        this.loggedInUser = user;
        if (user != null) {
            System.out.println("User context set in ViewSubmissionsController: " + user.getUsername() + " (Role: " + user.getRole() + ")");
            loadSubmissions();
        } else {
            System.err.println("User context set in ViewSubmissionsController is null.");
            showAlert(Alert.AlertType.ERROR, "Ошибка пользователя", "Контекст пользователя не установлен.");
            if (submissionList != null) submissionList.clear();
        }
    }

    private void loadSubmissions() {
        try {
            List<Submission> submissionsFromDb = submissionDao.getAllSubmissionsData();
            submissionList.setAll(submissionsFromDb);
            System.out.println("Loaded " + submissionsFromDb.size() + " submissions.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка базы данных", "Не удалось загрузить данные о сабмитах: " + e.getMessage());
            if (submissionList != null) submissionList.clear();
        }
        if (submissionList.isEmpty()) {
            System.out.println("No submissions found in database.");
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