package com.project.model;

import java.util.List;
import javafx.scene.control.MenuItem; // For potential UI generation later

public abstract class User {
    protected int id;
    protected String username;
    protected String email;
    protected String passwordHash;
    protected String role;

    public User(int id, String username, String email, String role, String passwordHash) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.passwordHash = passwordHash;
    }


    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getPasswordHash() {
        return passwordHash;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }


    public abstract String getDashboardTitle();
    public abstract List<MenuItem> getRoleSpecificMenuItems();

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}