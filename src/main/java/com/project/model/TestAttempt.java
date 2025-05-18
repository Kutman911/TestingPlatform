package com.project.model;

import java.time.LocalDateTime;

public class TestAttempt {
    private int attemptId;
    private int studentId;
    private int testId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int score;


    public TestAttempt(int studentId, int testId, LocalDateTime startTime) {
        this.studentId = studentId;
        this.testId = testId;
        this.startTime = startTime;

    }


    public TestAttempt(int attemptId, int studentId, int testId, LocalDateTime startTime, LocalDateTime endTime, int score) {
        this.attemptId = attemptId;
        this.studentId = studentId;
        this.testId = testId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.score = score;
    }

    // Getters
    public int getAttemptId() { return attemptId; }
    public int getStudentId() { return studentId; }
    public int getTestId() { return testId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public int getScore() { return score; }

    // Setters
    public void setAttemptId(int attemptId) { this.attemptId = attemptId; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setScore(int score) { this.score = score; }
}