package com.project.model;

import java.time.LocalDateTime;

public class AnswerOption {
    private int optionId;
    private int questionId;
    private String optionText;
    private boolean isCorrect;
    private LocalDateTime createdAt;


    public AnswerOption(int optionId, int questionId, String optionText, boolean isCorrect, LocalDateTime createdAt) {
        this.optionId = optionId;
        this.questionId = questionId;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
        this.createdAt = createdAt;
    }


    public AnswerOption(int questionId, String optionText, boolean isCorrect) {
        this.questionId = questionId;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
    }


    public int getOptionId() { return optionId; }
    public int getQuestionId() { return questionId; }
    public String getOptionText() { return optionText; }
    public boolean isCorrect() { return isCorrect; } // Геттер для boolean
    public LocalDateTime getCreatedAt() { return createdAt; }


    public void setOptionId(int optionId) { this.optionId = optionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }
    public void setOptionText(String optionText) { this.optionText = optionText; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return optionText; // Для простого отображения
    }
}