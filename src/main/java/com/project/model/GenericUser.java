package com.project.model;

import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class GenericUser extends User {
    public GenericUser(int id, String username, String email, String role, String passwordHash) {
        super(id, username, email, role, passwordHash);
    }

    @Override
    public String getDashboardTitle() {
        return "Generic User Dashboard";
    }

    @Override
    public List<MenuItem> getRoleSpecificMenuItems() {
        return new ArrayList<>();
    }
}