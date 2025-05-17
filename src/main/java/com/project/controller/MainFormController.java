package com.project.controller;

import com.project.controller.util.UserContextAware;
import com.project.model.User;
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

import java.util.List;

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

    private User loggedInUser; // Store the full User object

    public void initialize() {
        statusLabel.setText("Status: Initializing...");
    }

    // Changed to accept User object
    public void initializeUser(User user) {
        this.loggedInUser = user;

        if (loggedInUser == null) {
            // Handle error: no user passed or user is null
            showAlert(Alert.AlertType.ERROR, "Initialization Error", "User data not available. Logging out.");
            handleLogout(null); // Pass null or create a dummy ActionEvent
            return;
        }

        welcomeLabel.setText("Welcome, " + loggedInUser.getUsername() + "!");
        roleLabel.setText("Your Role: " + loggedInUser.getRole());
        statusLabel.setText("Status: Logged in as " + loggedInUser.getRole());

        // Clear existing dynamic menus before adding new ones (if any were there)
        menuBar.getMenus().removeIf(menu -> menu.getId() != null && menu.getId().startsWith("roleSpecificMenu_"));

        setupMenusBasedOnRole();
    }

    private void setupMenusBasedOnRole() {
        if (loggedInUser == null) {
            System.err.println("setupMenusBasedOnRole called with null loggedInUser.");
            return;
        }


        List<MenuItem> roleMenuItems = loggedInUser.getRoleSpecificMenuItems();

        if (!roleMenuItems.isEmpty()) {

            String roleNameFormatted = loggedInUser.getRole().substring(0, 1).toUpperCase() +
                    loggedInUser.getRole().substring(1).toLowerCase();
            Menu roleSpecificMenu = new Menu(roleNameFormatted);
            roleSpecificMenu.setId("roleSpecificMenu_" + loggedInUser.getRole());
            for (MenuItem item : roleMenuItems) {
                String viewPath = "";
                String viewTitle = item.getText();

                switch (loggedInUser.getRole().toUpperCase()) {
                    case "STUDENT":
                        if (viewTitle.equals("View Available Tests")) {
                            viewPath = "/com/project/view/student/AvailableTestsView.fxml";
                        } else if (viewTitle.equals("View My Results")) {
                            viewPath = "/com/project/view/student/MyResultsView.fxml";
                            System.out.println("Студент: FXML для 'My Results' еще не создан.");
                        }
                        break;
                    case "TEACHER":
                        if (viewTitle.equals("Create/Manage Tests")) {
                            viewPath = "/com/project/view/teacher/ManageTestsView.fxml";
                        } else if (viewTitle.equals("Manage Questions")) {
                            viewPath = "/com/project/view/teacher/ManageQuestionsView.fxml";
                            System.out.println("Учитель: FXML для 'Manage Questions' еще не создан.");
                        } else if (viewTitle.equals("View Student Submissions")) {
                            viewPath = "/com/project/view/teacher/ViewSubmissionsView.fxml";
                            System.out.println("Учитель: FXML для 'View Student Submissions' еще не создан.");
                        }
                        break;
                    case "ADMIN":
                        if (viewTitle.equals("Manage Users")) {
                            viewPath = "/com/project/view/admin/ManageUsersView.fxml";
                        } else if (viewTitle.equals("System Settings")) {

                            System.out.println("Админ: FXML для 'System Settings' еще не создан.");
                        } else if (viewTitle.equals("View Audit Logs")) {
                            viewPath = "/com/project/view/admin/AuditLogsView.fxml";
                            System.out.println("Админ: FXML для 'View Audit Logs' еще не создан.");
                        }

                        break;
                    case "MANAGER":
                        if (viewTitle.equals("Manage Courses")) {
                            viewPath = "/com/project/view/manager/ManageCoursesView.fxml";
                        } else if (viewTitle.equals("Assign Tests to Courses")) {
                            viewPath = "/com/project/view/manager/AssignTestsView.fxml";
                            System.out.println("Менеджер: FXML для 'Assign Tests to Courses' еще не создан.");
                        } else if (viewTitle.equals("View Course Analytics")) {
                            viewPath = "/com/project/view/manager/ViewCourseAnalytics.fxml";
                            System.out.println("Менеджер: FXML для 'View Course Analytics' еще не создан.");
                        }
                        break;
                    default:
                        System.err.println("Неизвестная роль при настройке меню: " + loggedInUser.getRole());
                        break;
                }

                final String finalViewPath = viewPath;
                if (finalViewPath != null && !finalViewPath.isEmpty()) {

                    item.setOnAction(e -> loadView(finalViewPath, viewTitle));
                } else {
                    final String capturedTitle = viewTitle; // Захватываем viewTitle для лямбды
                    item.setOnAction(e -> {
                        System.out.println("Действие для '" + capturedTitle + "' (Роль: " + loggedInUser.getRole() + ") - FXML путь не настроен.");
                        loadView(null, capturedTitle);
                    });
                }
                roleSpecificMenu.getItems().add(item);
            }
            menuBar.getMenus().add(roleSpecificMenu);
        } else if (loggedInUser != null) {
            System.out.println("For role " + loggedInUser.getRole() + " puncts are not defined in User.");
        }
    }

    @FXML
    private void handleUserProfile(ActionEvent event) {
        if (loggedInUser == null) return;
        showAlert(Alert.AlertType.INFORMATION, "User Profile",
                "Username: " + loggedInUser.getUsername() +
                        "\nEmail: " + loggedInUser.getEmail() +
                        "\nRole: " + loggedInUser.getRole());
        loadView("/com/project/view/common/UserProfileView.fxml", "User Profile");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        loggedInUser = null; // Clear user session
        try {
            Parent loginRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/project/view/LoginForm.fxml")));
            Scene scene = new Scene(loginRoot);
            Stage primaryStage = (Stage) (menuBar != null ? menuBar.getScene().getWindow() : mainPane.getScene().getWindow());
            if (primaryStage != null) {
                primaryStage.setScene(scene);
                primaryStage.setTitle("Online Testing Platform - Login");
                primaryStage.centerOnScreen();
            }
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

    private void loadView(String fxmlPath, String viewTitle) {
        try {
            if (fxmlPath == null || fxmlPath.trim().isEmpty()) {
                System.out.println("View path is empty for: " + viewTitle);
                VBox placeholderView = new VBox(new Label(viewTitle + " - View Path Not Configured"));
                placeholderView.setAlignment(javafx.geometry.Pos.CENTER);
                mainPane.setCenter(placeholderView);
                return;
            }


            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent viewRoot = loader.load();


            Object loadedController = loader.getController();
            if (loadedController instanceof UserContextAware) {
                ((UserContextAware) loadedController).setUserContext(loggedInUser);
            }


            mainPane.setCenter(viewRoot);
            statusLabel.setText("Status: Viewing " + viewTitle);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Loading Error", "Could not load view: " + fxmlPath + "\n" + e.getMessage());
            statusLabel.setText("Status: Error loading " + viewTitle);
            VBox errorView = new VBox(new Label("Error loading: " + viewTitle));
            errorView.setAlignment(javafx.geometry.Pos.CENTER);
            mainPane.setCenter(errorView);
        } catch (NullPointerException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Loading Error", "FXML file not found: " + fxmlPath);
            statusLabel.setText("Status: Error FXML not found for " + viewTitle);
            VBox errorView = new VBox(new Label("FXML not found: " + viewTitle));
            errorView.setAlignment(javafx.geometry.Pos.CENTER);
            mainPane.setCenter(errorView);
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