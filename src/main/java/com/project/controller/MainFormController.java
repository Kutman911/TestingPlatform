package com.project.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainFormController {

    @FXML
    private MenuBar menuBar;

    @FXML
    private Label welcomeLabel;

    private String currentUserRole; // Set this after login
    private String currentUsername; // Set this after login

    public void initializeUser(String username, String role) {
        this.currentUsername = username;
        this.currentUserRole = role;
        welcomeLabel.setText("Welcome, " + username + " (" + role + ")");
        setupMenus();
    }

    private void setupMenus() {
        // Common menus already in FXML (File)

        if ("ADMIN".equalsIgnoreCase(currentUserRole)) {
            Menu adminMenu = new Menu("Admin Tasks");
            MenuItem manageUsers = new MenuItem("Manage Users");
            manageUsers.setOnAction(e -> System.out.println("Load User Management UI")); // Placeholder
            MenuItem manageTestsGlobal = new MenuItem("Manage All Tests");
            adminMenu.getItems().addAll(manageUsers, manageTestsGlobal);
            menuBar.getMenus().add(adminMenu);
        }

        if ("TEACHER".equalsIgnoreCase(currentUserRole) || "ADMIN".equalsIgnoreCase(currentUserRole)) {
            Menu teacherMenu = new Menu("Teacher Tools");
            MenuItem createTest = new MenuItem("Create/Edit Test");
            createTest.setOnAction(e -> System.out.println("Load Test Creation UI")); // Placeholder
            MenuItem viewResults = new MenuItem("View Student Results");
            teacherMenu.getItems().addAll(createTest, viewResults);
            menuBar.getMenus().add(teacherMenu);
        }

        if ("STUDENT".equalsIgnoreCase(currentUserRole)) {
            Menu studentMenu = new Menu("Student Portal");
            MenuItem takeTest = new MenuItem("Available Tests");
            takeTest.setOnAction(e -> System.out.println("Load Available Tests UI")); // Placeholder
            MenuItem myResults = new MenuItem("My Results");
            studentMenu.getItems().addAll(takeTest, myResults);
            menuBar.getMenus().add(studentMenu);
        }

        if ("MANAGER".equalsIgnoreCase(currentUserRole) || "ADMIN".equalsIgnoreCase(currentUserRole)) {
            Menu managerMenu = new Menu("Course Management");
            MenuItem scheduleTest = new MenuItem("Schedule Test");
            MenuItem viewProgress = new MenuItem("View Student Progress");
            managerMenu.getItems().addAll(scheduleTest, viewProgress);
            menuBar.getMenus().add(managerMenu);
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Clear session/user data if any
            Parent loginRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/project/view/LoginForm.fxml")));
            Scene scene = new Scene(loginRoot);
            Stage primaryStage = (Stage) menuBar.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Online Testing Platform - Login");
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            // Show error alert
        }
    }

    @FXML
    private void handleExit(ActionEvent event) {
        Stage stage = (Stage) menuBar.getScene().getWindow();
        stage.close();
    }

    // Method to load different views into the center of the BorderPane
    // public void loadView(String fxmlPath) { ... }
}