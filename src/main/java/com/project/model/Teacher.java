package com.project.model;

import javafx.scene.control.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class Teacher extends User {

    public Teacher(int id, String username, String email, String passwordHash) {
        super(id, username, email, "TEACHER", passwordHash);
    }

    @Override
    public String getDashboardTitle() {
        return "Teacher Dashboard";
    }

    @Override
    public List<MenuItem> getRoleSpecificMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        MenuItem createTest = new MenuItem("Create/Manage Tests");
        createTest.setOnAction(e -> System.out.println("Teacher: Navigating to Manage Tests..."));

        MenuItem manageQuestions = new MenuItem("Manage Questions");
        manageQuestions.setOnAction(e -> System.out.println("Teacher: Navigating to Manage Questions..."));

        items.add(createTest);
        items.add(manageQuestions);
        return items;
    }
}