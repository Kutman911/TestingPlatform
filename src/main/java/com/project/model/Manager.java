package com.project.model;

import javafx.scene.control.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class Manager extends User {

    public Manager(int id, String username, String email, String passwordHash) {
        super(id, username, email, "MANAGER", passwordHash);
    }

    @Override
    public String getDashboardTitle() {
        return "Course Manager Dashboard";
    }

    @Override
    public List<MenuItem> getRoleSpecificMenuItems() {
        List<MenuItem> items = new ArrayList<>();

        MenuItem manageCourses = new MenuItem("Manage Courses");
        manageCourses.setOnAction(e -> System.out.println("Manager: Navigating to Manage Courses..."));

        MenuItem assignTests = new MenuItem("Assign Tests to Courses");
        assignTests.setOnAction(e -> System.out.println("Manager: Navigating to Assign Tests..."));

        MenuItem viewAnalytics = new MenuItem("View Course Analytics");
        viewAnalytics.setOnAction(e -> System.out.println("Manager: Navigating to Course Analytics..."));


        items.add(manageCourses);
        items.add(assignTests);
        items.add(viewAnalytics);

        return items;
    }
}