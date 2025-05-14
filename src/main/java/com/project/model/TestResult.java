package com.project.model;

import java.time.LocalDateTime;

public class TestResult {
    private String testTitle;
    private int score;
    private LocalDateTime attemptDate;

    public TestResult(String testTitle, int score, LocalDateTime attemptDate) {
        this.testTitle = testTitle;
        this.score = score;
        this.attemptDate = attemptDate;
    }
    public String getTestTitle() { return testTitle; }
    public int getScore() { return score; }
    public LocalDateTime getAttemptDate() { return attemptDate; }
}