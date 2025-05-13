package com.project.model;

import java.time.LocalDateTime; // Для created_at, updated_at

public class Course {
    private int courseId;
    private String courseName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Course(String courseName, String description) {
        this.courseName = courseName;
        this.description = description;
    }


    public Course(int courseId, String courseName, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public int getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return courseName;
    }
}