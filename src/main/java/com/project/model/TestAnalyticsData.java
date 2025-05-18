package com.project.model;

public class TestAnalyticsData {
    private int testId;
    private String testName;
    private double averageScorePercentage;

    public TestAnalyticsData(int testId, String testName, double averageScorePercentage) {
        this.testId = testId;
        this.testName = testName;
        this.averageScorePercentage = averageScorePercentage;

    }


    public String getTestName() {
        return testName;
    }

    public double getAverageScorePercentage() {
        return averageScorePercentage;
    }


    public int getTestId() {
        return testId;
    }

    @Override
    public String toString() {
        return "TestAnalyticsData{" +
                "testName='" + testName + '\'' +
                ", averageScorePercentage=" + averageScorePercentage +
                '}';
    }
}