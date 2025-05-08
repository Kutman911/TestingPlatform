package com.project.controller;

import com.project.db.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class LoginFormController {
    public TextField txtUserName;
    public PasswordField txtPassword;
    public AnchorPane root;

    public void btnSignIn(ActionEvent actionEvent) {
        String username = txtUserName.getText();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Username and Password cannot be empty.");
            return;
        }


        String sql = "SELECT role FROM users WHERE username = ? AND password = ?";


        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password); // This should be a hashed password comparison in production

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String userRole = resultSet.getString("role");

                if (userRole == null || userRole.trim().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Login Failed", "User role not defined. Please contact administrator.");
                    return;
                }


                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/com/project/view/MainForm.fxml")));
                Parent mainFormRoot = loader.load();


                MainFormController mainFormController = loader.getController();
                mainFormController.initializeUser(username, userRole);

                Scene scene = new Scene(mainFormRoot);
                Stage primaryStage = (Stage) root.getScene().getWindow();
                primaryStage.setScene(scene);
                primaryStage.setTitle("Main Dashboard - " + userRole); // Dynamic title
                primaryStage.centerOnScreen();

            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Username or Password Do not Match.");
                txtUserName.clear();
                txtPassword.clear();
                txtUserName.requestFocus();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not connect to the database or query failed: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Loading Error", "Could not load the main form: " + e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Resource Error", "Could not find MainForm.fxml. Check the path.");
        }
    }

    public void btnSignup(ActionEvent actionEvent) throws IOException {
        try {
            Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/project/view/RegistrationForm.fxml"))); // Adjust path
            Scene scene = new Scene(parent);
            Stage primaryStage = (Stage) root.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setTitle("User Registration");
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Loading Error", "Could not load the registration form: " + e.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Resource Error", "Could not find FXML file. Check the path.");
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