package com.project.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainFormController {

    @FXML
    private BorderPane mainPane;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Label statusLabel;

    private String currentUsername;
    private String currentUserRole;

    public void initialize() {
        // This method is called after all @FXML annotated members have been injected
        statusLabel.setText("Status: Initializing...");
    }

    public void initializeUser(String username, String role) {
        this.currentUsername = username;
        this.currentUserRole = role;

        welcomeLabel.setText("Welcome, " + username + "!");
        roleLabel.setText("Your Role: " + role);
        statusLabel.setText("Status: Logged in as " + role);

        setupMenusBasedOnRole();
    }

    private void setupMenusBasedOnRole() {
        // Clear any existing role-specific menus if this method were to be called multiple times
        // For now, we assume it's called once.

        // Student Menu
        if ("STUDENT".equalsIgnoreCase(currentUserRole)) {
            Menu studentMenu = new Menu("Student");
            MenuItem viewAvailableTests = new MenuItem("View Available Tests");
            viewAvailableTests.setOnAction(e -> loadView("/com/project/view/student/AvailableTestsView.fxml", "Available Tests"));
            MenuItem viewMyResults = new MenuItem("View My Results");
            viewMyResults.setOnAction(e -> loadView("/com/project/view/student/MyResultsView.fxml", "My Results"));
            studentMenu.getItems().addAll(viewAvailableTests, viewMyResults);
            menuBar.getMenus().add(studentMenu);
        }

        // Teacher Menu
        if ("TEACHER".equalsIgnoreCase(currentUserRole)) {
            Menu teacherMenu = new Menu("Teacher");
            MenuItem createTest = new MenuItem("Create/Manage Tests");
            createTest.setOnAction(e -> loadView("/com/project/view/teacher/ManageTestsView.fxml", "Manage Tests"));
            MenuItem manageQuestions = new MenuItem("Manage Questions");
            manageQuestions.setOnAction(e -> loadView("/com/project/view/teacher/ManageQuestionsView.fxml", "Manage Questions"));
            MenuItem viewSubmissions = new MenuItem("View Student Submissions");
            viewSubmissions.setOnAction(e -> loadView("/com/project/view/teacher/ViewSubmissionsView.fxml", "Submissions"));
            teacherMenu.getItems().addAll(createTest, manageQuestions, viewSubmissions);
            menuBar.getMenus().add(teacherMenu);
        }

        // Admin Menu
        if ("ADMIN".equalsIgnoreCase(currentUserRole)) {
            Menu adminMenu = new Menu("Administrator");
            MenuItem manageUsers = new MenuItem("Manage Users");
            manageUsers.setOnAction(e -> loadView("/com/project/view/admin/ManageUsersView.fxml", "Manage Users"));
            MenuItem systemSettings = new MenuItem("System Settings"); // Example
            systemSettings.setOnAction(e -> System.out.println("Admin: System Settings clicked"));
            MenuItem viewAuditLogs = new MenuItem("View Audit Logs"); // Example
            viewAuditLogs.setOnAction(e -> System.out.println("Admin: View Audit Logs clicked"));

            adminMenu.getItems().addAll(manageUsers, systemSettings, viewAuditLogs);
            menuBar.getMenus().add(adminMenu);
        }

        // Course Manager Menu
        if ("MANAGER".equalsIgnoreCase(currentUserRole)) {
            Menu managerMenu = new Menu("Course Manager");
            MenuItem manageCourses = new MenuItem("Manage Courses");
            manageCourses.setOnAction(e -> loadView("/com/project/view/manager/ManageCoursesView.fxml", "Manage Courses"));
            MenuItem assignTestsToCourses = new MenuItem("Assign Tests to Courses");
            assignTestsToCourses.setOnAction(e -> System.out.println("Manager: Assign Tests clicked"));
            MenuItem viewCourseAnalytics = new MenuItem("View Course Analytics");
            viewCourseAnalytics.setOnAction(e -> System.out.println("Manager: Course Analytics clicked"));
            managerMenu.getItems().addAll(manageCourses, assignTestsToCourses, viewCourseAnalytics);
            menuBar.getMenus().add(managerMenu);
        }
    }

    @FXML
    private void handleUserProfile(ActionEvent event) {
        // For now, just show an alert. Later, this can load a user profile view.
        showAlert(Alert.AlertType.INFORMATION, "User Profile", "Username: " + currentUsername + "\nRole: " + currentUserRole);
        // loadView("/com/project/view/common/UserProfileView.fxml", "User Profile");
    }


    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            currentUsername = null;
            currentUserRole = null;

            Parent loginRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/project/view/LoginForm.fxml")));
            Scene scene = new Scene(loginRoot);
            Stage primaryStage = (Stage) menuBar.getScene().getWindow(); // or mainPane.getScene().getWindow()
            primaryStage.setScene(scene);
            primaryStage.setTitle("Online Testing Platform - Login");
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Logout Error", "Could not load the login screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleExit(ActionEvent event) {
        Stage stage = (Stage) menuBar.getScene().getWindow();
        stage.close();
    }

    // Helper method to load different views into the center of the BorderPane
    private void loadView(String fxmlPath, String viewTitle) {
        try {

            VBox placeholderView = new VBox(new Label(viewTitle + " - View (Not Implemented Yet)"));
            placeholderView.setAlignment(javafx.geometry.Pos.CENTER);
            placeholderView.setStyle("-fx-padding: 20;");
            mainPane.setCenter(placeholderView);
            statusLabel.setText("Status: Viewing " + viewTitle);

            System.out.println("Attempting to load: " + fxmlPath); // For debugging

        } catch (/*IO*/Exception e) { // Catch broader exception for now
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Loading Error", "Could not load view: " + fxmlPath + "\n" + e.getMessage());
            statusLabel.setText("Status: Error loading " + viewTitle);
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