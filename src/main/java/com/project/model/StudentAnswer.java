package com.project.model;

import java.time.LocalDateTime;

public class StudentAnswer {
    private int answerId;
    private int attemptId;
    private int questionId;
    private Integer chosenOptionId;
    private boolean isCorrect;
    private LocalDateTime answeredAt;


    public StudentAnswer(int attemptId, int questionId, Integer chosenOptionId, boolean isCorrect) {
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.chosenOptionId = chosenOptionId;
        this.isCorrect = isCorrect;
        this.answeredAt = LocalDateTime.now();
    }


    public StudentAnswer(int answerId, int attemptId, int questionId, Integer chosenOptionId, boolean isCorrect, LocalDateTime answeredAt) {
        this.answerId = answerId;
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.chosenOptionId = chosenOptionId;
        this.isCorrect = isCorrect;
        this.answeredAt = answeredAt;
    }

    // Getters
    public int getAnswerId() { return answerId; }
    public int getAttemptId() { return attemptId; }
    public int getQuestionId() { return questionId; }
    public Integer getChosenOptionId() { return chosenOptionId; }
    public boolean isCorrect() { return isCorrect; }
    public LocalDateTime getAnsweredAt() { return answeredAt; }

    // Setters
    public void setAnswerId(int answerId) { this.answerId = answerId; }
    public void setAttemptId(int attemptId) { this.attemptId = attemptId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }
    public void setChosenOptionId(Integer chosenOptionId) { this.chosenOptionId = chosenOptionId; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
    public void setAnsweredAt(LocalDateTime answeredAt) { this.answeredAt = answeredAt; }
}