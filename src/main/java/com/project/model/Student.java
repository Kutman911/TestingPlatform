package com.project.model;

import javafx.scene.control.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class Student extends User {

    public Student(int id, String username, String email, String passwordHash) {
        super(id, username, email, "STUDENT", passwordHash);
    }

    @Override
    public String getDashboardTitle() {
        return "Student Dashboard";
    }

    @Override
    public List<MenuItem> getRoleSpecificMenuItems() {
        List<MenuItem> items = new ArrayList<>();

        MenuItem viewTests = new MenuItem("View Available Tests");
        viewTests.setOnAction(e -> System.out.println("Student: Navigating to View Available Tests..."));



        MenuItem viewResults = new MenuItem("View My Results");
        viewResults.setOnAction(e -> System.out.println("Student: Navigating to View My Results..."));

        items.add(viewTests);
        items.add(viewResults);
        return items;
    }
}