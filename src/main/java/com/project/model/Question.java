package com.project.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Question {
    private int questionId;
    private int testId;
    private String questionText;
    private String questionType; // "MULTIPLE_CHOICE", "TRUE_FALSE", "SHORT_ANSWER", "ESSAY"
    private int points;
    private LocalDateTime createdAt;


    private List<AnswerOption> answerOptions;

    // Конструктор для нового вопроса (ID и createdAt будут из БД)
    public Question(int testId, int id, String questionText, String questionType, int points) {
        this.testId = testId;
        this.questionText = questionText;
        this.questionType = questionType;
        this.points = points;
        this.answerOptions = new ArrayList<>(); // Инициализация списка
    }

    // Конструктор для вопроса из БД
    public Question(int questionId, int testId, String questionText, String questionType, int points, LocalDateTime createdAt) {
        this.questionId = questionId;
        this.testId = testId;
        this.questionText = questionText;
        this.questionType = questionType;
        this.points = points;
        this.createdAt = createdAt;
        this.answerOptions = new ArrayList<>(); // Инициализация списка
    }


    public int getQuestionId() { return questionId; }
    public int getTestId() { return testId; }
    public String getQuestionText() { return questionText; }
    public String getQuestionType() { return questionType; }
    public int getPoints() { return points; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public List<AnswerOption> getAnswerOptions() { return answerOptions; }



    public void setQuestionId(int questionId) { this.questionId = questionId; }
    public void setTestId(int testId) { this.testId = testId; } // Обычно не меняется
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }
    public void setPoints(int points) { this.points = points; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void setAnswerOptions(List<AnswerOption> answerOptions) { this.answerOptions = answerOptions; }



    public void addAnswerOption(AnswerOption option) {
        if (this.answerOptions == null) {
            this.answerOptions = new ArrayList<>();
        }
        this.answerOptions.add(option);
    }


    @Override
    public String toString() {
        return questionText; // Для простоты
    }

    // Возможно, вам понадобится этот метод, если вы используете Questions в TableView где-то еще
    // public int getId() { return questionId; }
}