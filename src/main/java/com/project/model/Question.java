package com.project.model;




public class Question {
    private int id;
    private int testId;
    private String text;
    private String type;

    public Question() { }

    public Question(int id, int testId, String text, String type) {
        this.id = id;
        this.testId = testId;
        this.text = text;
        this.type = type;
    }

    public Question(int testId, String text, String type) {
        this.testId = testId;
        this.text = text;
        this.type = type;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTestId() { return testId; }
    public void setTestId(int testId) { this.testId = testId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
