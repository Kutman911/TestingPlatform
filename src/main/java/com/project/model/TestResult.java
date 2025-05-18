package com.project.model;

import java.time.LocalDateTime;

public class TestResult {
    private int attemptId;
    private int studentId;
    private int testId;
    private String testTitle;
    private int score;
    private LocalDateTime attemptDate;


    public TestResult(int attemptId, int studentId, int testId, String testTitle, int score, LocalDateTime attemptDate) {
        this.attemptId = attemptId;
        this.studentId = studentId;
        this.testId = testId;
        this.testTitle = testTitle;
        this.score = score;
        this.attemptDate = attemptDate;
    }


    public String getTestTitle() {
        return testTitle;
    }

    public int getScore() {
        return score;
    }

    public LocalDateTime getAttemptDate() {
        return attemptDate;
    }


    public int getAttemptId() {
        return attemptId;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getTestId() {
        return testId;
    }



    @Override
    public String toString() {
        return "TestResult{" +
                "testTitle='" + testTitle + '\'' +
                ", score=" + score +
                ", attemptDate=" + attemptDate +
                '}';
    }
}