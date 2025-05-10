package com.project.controller;

import com.project.model.User; // Abstract User model
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
import java.util.List;
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
        if (loggedInUser == null) return;

        List<MenuItem> roleMenuItems = loggedInUser.getRoleSpecificMenuItems();

        if (!roleMenuItems.isEmpty()) {
            Menu roleSpecificMenu = new Menu(loggedInUser.getRole());
            roleSpecificMenu.setId("roleSpecificMenu_" + loggedInUser.getRole());
            roleSpecificMenu.getItems().addAll(roleMenuItems);


            for (MenuItem item : roleMenuItems) {
                String viewPath = "";
                String viewTitle = item.getText();


                String finalViewPath = viewPath;
                if (!finalViewPath.isEmpty()) {
                    item.setOnAction(e -> loadView(finalViewPath, viewTitle));
                }
            }


            if ("ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {
                Menu adminMenu = new Menu("Administrator");
                adminMenu.setId("roleSpecificMenu_ADMIN");

                List<MenuItem> adminItems = loggedInUser.getRoleSpecificMenuItems();

                for (MenuItem item : adminItems) {
                    if (item.getText().equals("Manage Users")) {
                        item.setOnAction(e -> loadView("/com/project/view/admin/ManageUsersView.fxml", "Manage Users"));
                    } else if (item.getText().equals("System Settings")) {
                        item.setOnAction(e -> {
                            System.out.println("Admin: System Settings clicked (Not Implemented View)");
                            loadView(null, "System Settings");
                        });
                    }
                    adminMenu.getItems().add(item);
                }
                menuBar.getMenus().add(adminMenu); // Добавляем меню администратора
            }
            ///// КОНЕЦ ВСТАВКИ /////

            menuBar.getMenus().add(roleSpecificMenu); // Общее меню для других ролей
        }
    }

    @FXML
    private void handleUserProfile(ActionEvent event) {
        if (loggedInUser == null) return;
        showAlert(Alert.AlertType.INFORMATION, "User Profile",
                "Username: " + loggedInUser.getUsername() +
                        "\nEmail: " + loggedInUser.getEmail() +
                        "\nRole: " + loggedInUser.getRole());
        // loadView("/com/project/view/common/UserProfileView.fxml", "User Profile");
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
    // ... (handleExit, loadView, showAlert methods remain mostly the same)
    // The loadView method will now be more important.

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

            // For actual loading:
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent viewRoot = loader.load();
            mainPane.setCenter(viewRoot);
            statusLabel.setText("Status: Viewing " + viewTitle);

            // Example of initializing controller if it needs the loggedInUser:
            // Object controller = loader.getController();
            // if (controller instanceof NeedsUser) { // Define an interface NeedsUser { void setUser(User user); }
            //    ((NeedsUser) controller).setUser(loggedInUser);
            // }


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