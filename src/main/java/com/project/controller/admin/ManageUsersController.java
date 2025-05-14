package com.project.controller.admin;

import com.project.dao.UserDao;
import com.project.dao.UserDaoImpl;
import com.project.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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

    private UserDao userDao;
    private ObservableList<User> userList;


    public void initialize() {
        userDao = new UserDaoImpl();
        userList = FXCollections.observableArrayList();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        usersTable.setItems(userList);
        loadUsers();
    }

    @FXML
    private void loadUsers() {
        try {
            List<User> usersFromDb = userDao.findAll();
            userList.setAll(usersFromDb);
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
        showUserEditDialog(null);
    }

    @FXML
    void handleEditUser(ActionEvent event) {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            showUserEditDialog(selectedUser);
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a user to edit.");
        }
    }

    private boolean showUserEditDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/project/view/admin/UserEditDialog.fxml"));
            GridPane dialogPane = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(user == null ? "Add New User" : "Edit User");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(dialogPane);
            dialogStage.setScene(scene);

            UserEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setUser(user);

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                loadUsers();
            }
            return controller.isSaveClicked();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open user edit dialog: " + e.getMessage());
            return false;
        }
    }

    @FXML
    void handleDeleteUser(ActionEvent event) {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {

            //if (loggedInAdminUser != null && loggedInAdminUser.getId() == selectedUser.getId()) {
               // showAlert(Alert.AlertType.WARNING, "Action Denied", "You cannot delete your own account.");
               // return;
            //}

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
                        showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Could not delete the user from the database.");
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
}