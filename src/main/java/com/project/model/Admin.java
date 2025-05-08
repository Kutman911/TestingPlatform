package com.project.model;

import javafx.scene.control.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class Admin extends User {

    public Admin(int id, String username, String email, String passwordHash) {
        super(id, username, email, "ADMIN", passwordHash);
    }

    @Override
    public String getDashboardTitle() {
        return "Administrator Dashboard";
    }

    @Override
    public List<MenuItem> getRoleSpecificMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        MenuItem manageUsers = new MenuItem("Manage Users");
        manageUsers.setOnAction(e -> System.out.println("Admin: Navigating to Manage Users..."));
        // This would eventually call a method to load the ManageUsersView.fxml

        MenuItem systemSettings = new MenuItem("System Settings");
        systemSettings.setOnAction(e -> System.out.println("Admin: Navigating to System Settings..."));


        items.add(manageUsers);
        items.add(systemSettings);
        return items;
    }
}