package com.project.controller.admin;

import com.project.dao.UserDAO;

import com.project.dao.UserDaoImpl;
import com.project.model.User; // Абстрактный User
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.ButtonType; // Для диалога подтверждения

public class ManageUsersController {

    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private Button addUserButton;
    @FXML
    private Button editUserButton;
    @FXML
    private Button deleteUserButton;
    @FXML
    private Button refreshButton;

    private UserDAO userDao;
    private ObservableList<User> userList;

    public void initialize() {
        userDao = new UserDaoImpl();
        userList = FXCollections.observableArrayList();

        // Настройка столбцов таблицы для отображения свойств объектов User
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role")); // Убедитесь, что у User есть getRole()

        usersTable.setItems(userList);
        loadUsers(); // Загрузка пользователей при инициализации
    }

    @FXML
    private void loadUsers() {
        try {
            List<User> usersFromDb = userDao.findAll();
            userList.setAll(usersFromDb); // Обновляем ObservableList
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load users: " + e.getMessage());
        }
    }

    @FXML
    void handleRefreshUsers(ActionEvent event) {
        loadUsers();
    }

    @FXML
    void handleAddUser(ActionEvent event) {
        // Здесь мы будем открывать новое окно/диалог для добавления пользователя.
        // Пока что просто выведем сообщение.
        System.out.println("Add User button clicked. Implement dialog for adding user.");
        showAlert(Alert.AlertType.INFORMATION, "Add User", "Functionality to add a new user will be implemented here (e.g., opening a new dialog).");
        // После успешного добавления в диалоге, нужно будет вызвать loadUsers()
    }

    @FXML
    void handleEditUser(ActionEvent event) {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            // Здесь мы будем открывать новое окно/диалог для редактирования пользователя,
            // передавая selectedUser.
            System.out.println("Edit User button clicked for user: " + selectedUser.getUsername());
            showAlert(Alert.AlertType.INFORMATION, "Edit User", "Functionality to edit user '" + selectedUser.getUsername() + "' will be implemented here.");
            // После успешного редактирования, нужно будет вызвать loadUsers()
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a user to edit.");
        }
    }

    @FXML
    void handleDeleteUser(ActionEvent event) {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirm Deletion");
            confirmationDialog.setHeaderText("Delete User: " + selectedUser.getUsername());
            confirmationDialog.setContentText("Are you sure you want to delete this user? This action cannot be undone.");

            Optional<ButtonType> result = confirmationDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    boolean deleted = userDao.delete(selectedUser.getId());
                    if (deleted) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully.");
                        loadUsers(); // Обновить таблицу
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Could not delete the user.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete user: " + e.getMessage());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a user to delete.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Этот метод может понадобиться, если мы захотим передавать данные
    // (например, объект UserSession или MainFormController) в этот контроллер.
    // public void initData(UserSession session) { /* ... */ }
}