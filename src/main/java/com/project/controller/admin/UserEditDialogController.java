package com.project.controller.admin;

import com.project.dao.UserDAO;
import com.project.dao.UserDaoImpl;
import com.project.model.*; // Импортируем все модели User, Student и т.д.
import com.project.util.PasswordHashingService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class UserEditDialogController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label passwordLabel;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Button saveButton;

    private Stage dialogStage;
    private User user; // Редактируемый пользователь (null для нового)
    private boolean saveClicked = false;
    private UserDAO userDao;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    @FXML
    private void initialize() {
        userDao = new UserDaoImpl();
        List<String> roles = Arrays.asList("STUDENT", "TEACHER", "ADMIN", "MANAGER");
        roleComboBox.setItems(FXCollections.observableArrayList(roles));
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


    public void setUser(User userToEdit) {
        this.user = userToEdit;

        if (userToEdit != null) { // Режим редактирования
            usernameField.setText(userToEdit.getUsername());
            emailField.setText(userToEdit.getEmail());
            roleComboBox.setValue(userToEdit.getRole().toUpperCase());


            passwordField.setDisable(true);
            passwordField.setPromptText("Not changed in this form");
            passwordLabel.setDisable(true);

        } else {
            roleComboBox.setValue("STUDENT");
            passwordField.setDisable(false);
            passwordLabel.setDisable(false);
        }
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (isInputValid()) {
            String username = usernameField.getText();
            String email = emailField.getText();
            String selectedRole = roleComboBox.getValue();

            try {
                if (user == null) { // Создание нового пользователя
                    if (userDao.findByUsername(username).isPresent()) {
                        showAlert(Alert.AlertType.ERROR, "Validation Error", "Username already exists.");
                        return;
                    }
                    // Дополнительная проверка на уникальность email, если требуется
                    // if (userDao.findByEmail(email).isPresent()) { // Предполагая, что такой метод есть
                    //     showAlert(Alert.AlertType.ERROR, "Validation Error", "Email already exists.");
                    //     return;
                    // }

                    String plainPassword = passwordField.getText();
                    String hashedPassword = PasswordHashingService.hashPassword(plainPassword);

                    User newUser = createUserByRole(0, username, email, selectedRole, hashedPassword);
                    userDao.save(newUser);

                } else { // Обновление существующего пользователя
                    // Проверка, если имя пользователя было изменено и новое имя уже занято
                    if (!user.getUsername().equals(username) && userDao.findByUsername(username).isPresent()) {
                        showAlert(Alert.AlertType.ERROR, "Validation Error", "New username already exists.");
                        return;
                    }


                    user.setUsername(username);
                    user.setEmail(email);

                    User updatedUser = createUserByRole(user.getId(), username, email, selectedRole, user.getPasswordHash()); // Пароль не меняем
                    userDao.update(updatedUser);
                }
                saveClicked = true;
                dialogStage.close();

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Could not save user data: " + e.getMessage());
            }

        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (usernameField.getText() == null || usernameField.getText().trim().isEmpty()) {
            errorMessage += "No valid username!\n";
        }
        if (emailField.getText() == null || emailField.getText().trim().isEmpty() || !EMAIL_PATTERN.matcher(emailField.getText().trim()).matches()) {
            errorMessage += "No valid email!\n";
        }
        if (roleComboBox.getValue() == null || roleComboBox.getValue().isEmpty()) {
            errorMessage += "No role selected!\n";
        }

        if (user == null) { // При создании нового пользователя пароль обязателен
            if (passwordField.getText() == null || passwordField.getText().isEmpty()) {
                errorMessage += "Password cannot be empty for new user!\n";
            } else if (passwordField.getText().length() < 8) {
                errorMessage += "Password must be at least 8 characters long!\n";
            }
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert(Alert.AlertType.ERROR, "Invalid Fields", errorMessage);
            return false;
        }
    }

    private User createUserByRole(int id, String username, String email, String role, String passwordHash) {
        switch (role.toUpperCase()) {
            case "STUDENT":
                return new Student(id, username, email, passwordHash);
            case "TEACHER":
                return new Teacher(id, username, email, passwordHash);
            case "ADMIN":
                return new Admin(id, username, email, passwordHash);
            case "MANAGER":
                return new Manager(id, username, email, passwordHash);
            default: // По умолчанию создаем Student или выбрасываем исключение
                System.err.println("Unknown role in createUserByRole: " + role + ". Defaulting to Student.");
                return new Student(id, username, email, passwordHash); // Или throw new IllegalArgumentException("Invalid role: " + role);
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