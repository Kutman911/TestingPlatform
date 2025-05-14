package com.project.model;

import java.time.LocalDateTime;

public class Test {
    private int testId;
    private Integer courseId;
    private int creatorId;
    private String testName;
    private String description;
    private int durationMinutes;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    private String courseNameDisplay;
    private String creatorNameDisplay;



    public Test(Integer courseId, int creatorId, String testName, String description, int durationMinutes, boolean isActive) {
        this.courseId = courseId;
        this.creatorId = creatorId;
        this.testName = testName;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.isActive = isActive;
    }

    public Test(int testId, Integer courseId, int creatorId, String testName, String description,
                int durationMinutes, boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.testId = testId;
        this.courseId = courseId;
        this.creatorId = creatorId;
        this.testName = testName;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public int getTestId() { return testId; }
    public Integer getCourseId() { return courseId; } // Может быть null
    public int getCreatorId() { return creatorId; }
    public String getTestName() { return testName; }
    public String getDescription() { return description; }
    public int getDurationMinutes() { return durationMinutes; }
    public boolean isActive() { return isActive; } // Геттер для boolean
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public String getCourseNameDisplay() { return courseNameDisplay; }
    public String getCreatorNameDisplay() { return creatorNameDisplay; }



    public void setTestId(int testId) { this.testId = testId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }
    public void setCreatorId(int creatorId) { this.creatorId = creatorId; } // Обычно не меняется после создания
    public void setTestName(String testName) { this.testName = testName; }
    public void setDescription(String description) { this.description = description; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public void setActive(boolean active) { isActive = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public void setCourseNameDisplay(String courseNameDisplay) { this.courseNameDisplay = courseNameDisplay; }
    public void setCreatorNameDisplay(String creatorNameDisplay) { this.creatorNameDisplay = creatorNameDisplay; }


    @Override
    public String toString() {
        return testName + (courseId != null ? " (Course ID: " + courseId + ")" : "");
    }

    public int getId() {
        return testId;
    }
}