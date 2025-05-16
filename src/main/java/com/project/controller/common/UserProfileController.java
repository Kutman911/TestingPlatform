package com.project.controller.common;

import com.project.controller.util.UserContextAware;
import com.project.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserProfileController implements UserContextAware {

    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleLabel;

    private User currentUser;

    @Override
    public void setUserContext(User user) {
        this.currentUser = user;
        populateUserDetails();
    }

    private void populateUserDetails() {
        if (currentUser != null) {
            usernameLabel.setText("Username: " + currentUser.getUsername());
            emailLabel.setText("Email: " + currentUser.getEmail());
            roleLabel.setText("Role: " + currentUser.getRole());
        } else {
            usernameLabel.setText("Username: Not available");
            emailLabel.setText("Email: Not available");
            roleLabel.setText("Role: Not available");
        }
    }

    public void initialize() {
        // Можно установить значения по умолчанию, если currentUser еще не установлен
        populateUserDetails();
    }
}