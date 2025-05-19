package com.project.model;


public class Submission {
    private int id;
    private String studentName;
    private String testName;
    private double score;
    private String status;
    private int maxScore;
    private int attemptId;

    public Submission() { }

    public Submission(int attemptId, String studentName, String testName, int score, int maxScore) {
        this.attemptId = attemptId;
        this.studentName = studentName;
        this.testName = testName;
        this.score = score;
        this.maxScore = maxScore;

    }

    public Submission(String studentName, String testName, double score, String status) {
        this.studentName = studentName;
        this.testName = testName;
        this.score = score;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAttemptId() { return attemptId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public int getMaxScore() { return maxScore; }
    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Submission{" +
                "attemptId=" + attemptId +
                ", studentName='" + studentName + '\'' +
                ", testName='" + testName + '\'' +
                ", score=" + score +
                ", maxScore=" + maxScore +
                '}';
    }
}
