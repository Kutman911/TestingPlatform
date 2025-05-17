package com.project.controller.student;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestTakingController {

    @FXML
    private Text testNameDisplay;

    @FXML
    private ToggleGroup answers;


    @FXML
    private Text question_text;

    @FXML
    private RadioButton radio_btn_1;

    @FXML
    private RadioButton radio_btn_2;

    @FXML
    private RadioButton radio_btn_3;

    @FXML
    private RadioButton radio_btn_4;

    @FXML
    private Button answerBtn;

    @FXML
    private Label resultLabel;



    private String currentTestName;
    private List<Question> currentQuestions;
    private int nowQuestion = 0;
    private int correctAnswers = 0;
    private String nowCorrectAnswer;

    public void setCurrentTest(String testName) {
        this.currentTestName = testName;
        testNameDisplay.setText("Test: " + testName);
        loadQuestions();
    }

    private void loadQuestions() {
        if (currentTestName.equals("Java Basics")) {
            currentQuestions = Arrays.asList(
                    new Question("What is the correct way to declare an integer variable in Java?", new String[]{"int variableName;", "Integer variableName;", "variableName int;", "v int;", "int variableName;"}),
                    new Question("Which of the following is NOT a primitive data type in Java?", new String[]{"boolean", "char", "String", "byte", "String"}),
                    new Question("What is the purpose of the main method in Java?", new String[]{"It is the entry point...", "It is used to define global variables.", "It is used for input/output operations.", "It is a method that must be overridden...", "It is the entry point..."})
            );
        } else if (currentTestName.equals("English Grammar")) {
            currentQuestions = Arrays.asList(
                    new Question("Choose the correct form of the verb...", new String[]{"go", "goes", "went", "going", "went"}),
                    new Question("Identify the preposition...", new String[]{"The", "book", "is", "on", "on"}),
                    new Question("Which of the following sentences is grammatically correct?", new String[]{"She don't like pizza.", "They was at the cinema.", "He has been study hard.", "We are going to travel next month.", "We are going to travel next month."})
            );
        } else if (currentTestName.equals("Math Challenge")) {
            currentQuestions = Arrays.asList(
                    new Question("Solve for x: 3x^2 - 5x - 2 = 0", new String[]{"x = 2 or x = -1/3", "x = -2 or x = 1/3", "x = -2 or x = -1/3", "x = 2 or x = 1/3", "x = 2 or x = -1/3"}),
                    new Question("What is the derivative of f(x) = sin(2x) + e^(3x)?", new String[]{"cos(2x) + 3e^(3x)", "2cos(2x) + e^(3x)", "2cos(2x) + 3e^(3x)", "-cos(2x) + 3e^(3x)", "2cos(2x) + 3e^(3x)"}),
                    new Question("Find the definite integral: ∫ from 0 to π of cos(x) dx", new String[]{"0", "1", "-1", "π", "0"})
            );
        }

        if (currentQuestions != null && !currentQuestions.isEmpty()) {
            loadCurrentQuestion();
        } else {
            question_text.setText("No questions available for this test.");
            answerBtn.setDisable(true);
        }
    }

    private void loadCurrentQuestion() {
        if (nowQuestion < currentQuestions.size()) {
            Question currentQ = currentQuestions.get(nowQuestion);
            question_text.setText(currentQ.getQuestion());
            String[] questionAnswers = currentQ.getAnswers();
            nowCorrectAnswer = questionAnswers[questionAnswers.length - 1];

            List<String> shuffledAnswers = Arrays.asList(Arrays.copyOf(questionAnswers, questionAnswers.length - 1));
            Collections.shuffle(shuffledAnswers);

            radio_btn_1.setText(shuffledAnswers.size() > 0 ? shuffledAnswers.get(0) : "");
            radio_btn_2.setText(shuffledAnswers.size() > 1 ? shuffledAnswers.get(1) : "");
            radio_btn_3.setText(shuffledAnswers.size() > 2 ? shuffledAnswers.get(2) : "");
            radio_btn_4.setText(shuffledAnswers.size() > 3 ? shuffledAnswers.get(3) : "");


            if (answers != null && answers.getSelectedToggle() != null) {
                answers.getSelectedToggle().setSelected(false);
            }
            resultLabel.setVisible(false);
            answerBtn.setText("Answer");
            answerBtn.setDisable(false);

        } else {
            question_text.setText("Test completed! You answered " + correctAnswers + " out of " + currentQuestions.size() + " questions correctly.");
            radio_btn_1.setVisible(false);
            radio_btn_2.setVisible(false);
            radio_btn_3.setVisible(false);
            radio_btn_4.setVisible(false);
            answerBtn.setVisible(false);
            resultLabel.setVisible(false);
        }
    }

    @FXML
    void handleAnswerButton(ActionEvent event) {
        if (nowQuestion < currentQuestions.size()) {
            RadioButton selectedRadioButton = (RadioButton) answers.getSelectedToggle();
            if (selectedRadioButton != null) {
                String userAnswer = selectedRadioButton.getText();
                if (userAnswer.equals(nowCorrectAnswer)) {
                    correctAnswers++;
                    resultLabel.setText("Correct!");
                } else {
                    resultLabel.setText("Incorrect. The correct answer was: " + nowCorrectAnswer);
                }
                resultLabel.setVisible(true);
                answerBtn.setText("Next Question");
                answerBtn.setOnAction(this::handleNextQuestion);
                answerBtn.setDisable(false);
            }
        }
    }

    @FXML
    void handleNextQuestion(ActionEvent event) {
        nowQuestion++;
        loadCurrentQuestion();

        answerBtn.setOnAction(this::handleAnswerButton);
    }

    public static class Question {
        private String question;
        private String[] answers;

        public Question(String question, String[] answers) {
            this.question = question;
            this.answers = answers;
        }

        public String getQuestion() {
            return question;
        }

        public String[] getAnswers() {
            return answers;
        }
    }
}