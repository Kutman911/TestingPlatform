package com.project.controller.student;

import com.project.controller.MainFormController;
import com.project.controller.util.UserContextAware;
import com.project.dao.QuestionDao;
import com.project.dao.QuestionDaoImpl;
import com.project.model.AnswerOption;
import com.project.model.Question;
import com.project.model.Test;
import com.project.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.project.dao.StudentTestDao;
import com.project.dao.StudentTestDaoImpl;
import com.project.model.StudentAnswer;
import com.project.model.TestAttempt;

public class TestTakingController implements UserContextAware {

    @FXML
    private Text testNameDisplay;

    @FXML
    private Button answerBtn;

    @FXML
    public Button btnReturnToMainMenu;

    @FXML
    private Label resultLabel;

    @FXML
    private Text question_text;

    @FXML
    private VBox answersContainer;

    private Test currentTest;
    private ToggleGroup answersToggleGroup;
    private QuestionDao questionDao;
    private List<Question> currentQuestions;
    private int nowQuestion = 0;
    private int correctAnswers = 0;
    private User loggedInUser;

    private StudentTestDao studentTestDao;
    private List<StudentAnswer> studentAnswers;
    private int currentAttemptId = -1;
    private LocalDateTime attemptStartTime;

    public void setCurrentTest(Test test) {
        this.currentTest = test;
        if (test != null) {
            testNameDisplay.setText("Test: " + test.getTestName());
            loadQuestions();
        } else {
            testNameDisplay.setText("Test: N/A");
            question_text.setText("No test selected.");
            hideQuestionElements();
            if (answerBtn != null) answerBtn.setVisible(false);
            if (btnReturnToMainMenu != null) {
                btnReturnToMainMenu.setVisible(true);
            }
        }
    }

    @Override
    public void setUserContext(User user) {
        this.loggedInUser = user;
        if (user != null) {
            System.out.println("User context set in TestTakingController: " + user.getUsername());
        } else {
            System.err.println("User context set in TestTakingController is null.");
            showAlert(Alert.AlertType.ERROR, "User Error", "User context not set. Cannot proceed.");
        }
    }

    public void initialize() {
        questionDao = new QuestionDaoImpl();
        studentTestDao = new StudentTestDaoImpl();
        studentAnswers = new ArrayList<>();

        if (btnReturnToMainMenu != null) {
            btnReturnToMainMenu.setVisible(false);
        }
        hideQuestionElements();
        if (answerBtn != null) answerBtn.setVisible(false);
    }

    private void loadQuestions() {
        if (currentTest != null) {
            try {
                currentQuestions = questionDao.getQuestionsByTestId(currentTest.getTestId());

                if (currentQuestions != null && !currentQuestions.isEmpty()) {
                    nowQuestion = 0;
                    correctAnswers = 0;
                    studentAnswers.clear();
                    attemptStartTime = LocalDateTime.now();

                    if (loggedInUser != null) {
                        TestAttempt newAttempt = new TestAttempt(loggedInUser.getId(), currentTest.getTestId(), attemptStartTime);
                        try {
                            currentAttemptId = studentTestDao.saveTestAttempt(newAttempt);
                            if (currentAttemptId == -1) {
                                throw new SQLException("Failed to get generated attempt ID from database.");
                            }
                            System.out.println("New test attempt started with ID: " + currentAttemptId + " for test ID: " + currentTest.getTestId() + " by user ID: " + loggedInUser.getId());
                        } catch (SQLException saveAttemptE) {
                            saveAttemptE.printStackTrace();
                            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to start test attempt: " + saveAttemptE.getMessage());
                            question_text.setText("Error starting test attempt.");
                            hideQuestionElements();
                            if (answerBtn != null) answerBtn.setVisible(false);
                            if (btnReturnToMainMenu != null) {
                                btnReturnToMainMenu.setVisible(true);
                            }
                            return;
                        }
                    } else {
                        System.err.println("loggedInUser is null. Cannot start test attempt.");
                        showAlert(Alert.AlertType.ERROR, "User Error", "User not logged in. Cannot start test.");
                        hideQuestionElements();
                        if (answerBtn != null) answerBtn.setVisible(false);
                        if (btnReturnToMainMenu != null) {
                            btnReturnToMainMenu.setVisible(true);
                        }
                        return;
                    }

                    setupQuestionUI();
                    loadCurrentQuestion();

                } else {
                    question_text.setText("No questions available for this test.");
                    hideQuestionElements();
                    if (answerBtn != null) answerBtn.setVisible(false);
                    if (btnReturnToMainMenu != null) {
                        btnReturnToMainMenu.setVisible(true);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database error", "Failed to load questions for test: " + e.getMessage());
                question_text.setText("Error loading questions.");
                hideQuestionElements();
                if (answerBtn != null) answerBtn.setVisible(false);
                if (btnReturnToMainMenu != null) {
                    btnReturnToMainMenu.setVisible(true);
                }
            }
        } else {
            question_text.setText("No test selected.");
            hideQuestionElements();
            if (answerBtn != null) answerBtn.setVisible(false);
            if (btnReturnToMainMenu != null) {
                btnReturnToMainMenu.setVisible(true);
            }
        }
    }

    private void setupQuestionUI() {
        if (answersContainer != null) answersContainer.setVisible(true);

        answersToggleGroup = new ToggleGroup();

        if (answerBtn != null) answerBtn.setVisible(true);
        if (btnReturnToMainMenu != null) btnReturnToMainMenu.setVisible(false);
        if (resultLabel != null) resultLabel.setVisible(false);
    }

    private void hideQuestionElements() {
        if (answersContainer != null) answersContainer.setVisible(false);
        if (resultLabel != null) resultLabel.setVisible(false);
        if (answerBtn != null) answerBtn.setVisible(false);
    }

    @FXML
    void handleReturnToMainMenu(ActionEvent event) {
        try {
            String mainFormFxmlPath = "/com/project/view/MainForm.fxml";

            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(mainFormFxmlPath)));
            Parent mainFormRoot = loader.load();

            MainFormController mainFormController = loader.getController();

            if (mainFormController != null) {
                if (loggedInUser != null) {
                    mainFormController.initializeUser(loggedInUser);
                } else {
                    System.err.println("loggedInUser is null when returning to main menu. Attempting logout.");
                }
            }

            Scene scene = new Scene(mainFormRoot);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Online Testing Platform - Main Menu");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка навигации", "Не удалось загрузить главное меню: " + e.getMessage());
        } catch (ClassCastException e) {
            e.printStackTrace();
            System.err.println("Error casting event source to Button. Ensure event source is a Button.");
        }
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        if (user != null) {
            System.out.println("User context set in TestTakingController: " + user.getUsername());
        } else {
            System.err.println("User context set in TestTakingController is null.");
        }
    }

    private void loadCurrentQuestion() {
        if (currentQuestions != null && nowQuestion < currentQuestions.size()) {
            Question currentQ = currentQuestions.get(nowQuestion);
            question_text.setText(currentQ.getQuestionText());

            if (answersContainer != null) {
                answersContainer.getChildren().clear();

                if ("MULTIPLE_CHOICE".equals(currentQ.getQuestionType()) || "TRUE_FALSE".equals(currentQ.getQuestionType())) {
                    List<AnswerOption> options = currentQ.getAnswerOptions();

                    if (options != null && !options.isEmpty()) {
                        Collections.shuffle(options);

                        if (answersToggleGroup == null) {
                            answersToggleGroup = new ToggleGroup();
                            System.err.println("answersToggleGroup was null in loadCurrentQuestion. Re-creating.");
                        }

                        for (AnswerOption option : options) {
                            RadioButton optionButton = new RadioButton(option.getOptionText());
                            optionButton.setToggleGroup(answersToggleGroup);
                            optionButton.setUserData(option);
                            answersContainer.getChildren().add(optionButton);
                        }
                        answersContainer.setVisible(true);
                        if (answerBtn != null) answerBtn.setVisible(true);
                        if (resultLabel != null) resultLabel.setVisible(false);

                    } else {
                        answersContainer.setVisible(false);
                        question_text.setText(currentQ.getQuestionText() + "\n\n(There are no answer options for this question)");
                        if (answerBtn != null) answerBtn.setVisible(false);
                    }
                } else {
                    answersContainer.setVisible(false);
                    question_text.setText(currentQ.getQuestionText() + "\n\n(This question type is not supported in the current UI implementation)");
                    if (answerBtn != null) {
                        answerBtn.setText("Next Question");
                        answerBtn.setOnAction(this::handleNextQuestion);
                        answerBtn.setDisable(false);
                        resultLabel.setVisible(false);
                        return;
                    } else {
                        System.err.println("answerBtn is null. Cannot proceed with unsupported question type.");
                        return;
                    }
                }
            }

            if (answersToggleGroup != null && answersToggleGroup.getSelectedToggle() != null) {
                answersToggleGroup.getSelectedToggle().setSelected(false);
            }

            resultLabel.setVisible(false);
            answerBtn.setText("Answer");
            answerBtn.setOnAction(this::handleAnswerButton);
            if (answerBtn != null) answerBtn.setDisable(false);

        } else {
            question_text.setText("Test completed! You answered " + correctAnswers + " out of " + (currentQuestions != null ? currentQuestions.size() : 0) + " questions correctly.");
            hideQuestionElements();
            if (answerBtn != null) answerBtn.setVisible(false);
            if (resultLabel != null) resultLabel.setVisible(false);

            if (currentAttemptId != -1) {
                LocalDateTime attemptEndTime = LocalDateTime.now();
                int finalScore = correctAnswers;

                try {
                    if (!studentAnswers.isEmpty()) {
                        studentTestDao.saveStudentAnswers(studentAnswers);
                        System.out.println("Student answers saved for attempt ID: " + currentAttemptId + ". Count: " + studentAnswers.size());
                    } else {
                        System.out.println("No student answers to save for attempt ID: " + currentAttemptId);
                    }

                    TestAttempt finishedAttempt = new TestAttempt(
                            currentAttemptId,
                            loggedInUser != null ? loggedInUser.getId() : -1,
                            currentTest != null ? currentTest.getTestId() : -1,
                            attemptStartTime,
                            attemptEndTime,
                            finalScore
                    );
                    if (finishedAttempt.getStudentId() != -1 && finishedAttempt.getTestId() != -1) {
                        boolean updated = studentTestDao.updateTestAttempt(finishedAttempt);
                        if (updated) {
                            System.out.println("Test attempt ID " + currentAttemptId + " updated with score " + finalScore + " and end time.");
                            showAlert(Alert.AlertType.INFORMATION, "Тест завершен", "Ваш результат: " + finalScore + " правильных ответов.");
                        } else {
                            System.err.println("Failed to update test attempt ID " + currentAttemptId + " in DB.");
                            showAlert(Alert.AlertType.WARNING, "Тест завершен", "Ваш результат: " + finalScore + " правильных ответов, но не удалось обновить запись в БД.");
                        }
                    } else {
                        System.err.println("Cannot update test attempt: Invalid student or test ID in finishedAttempt object.");
                        showAlert(Alert.AlertType.ERROR, "Internal Error", "Cannot finalize test attempt due to missing user or test info.");
                    }

                } catch (SQLException saveResultE) {
                    saveResultE.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save test results: " + saveResultE.getMessage());
                } finally {
                    currentAttemptId = -1;
                    attemptStartTime = null;
                    studentAnswers.clear();
                }

            } else {
                System.err.println("Cannot save test result: currentAttemptId is -1.");
            }

            if (btnReturnToMainMenu != null) {
                btnReturnToMainMenu.setVisible(true);
            }
        }
    }

    @FXML
    void handleAnswerButton(ActionEvent event) {
        if ("Answer".equals(answerBtn.getText())) {
            if (currentQuestions != null && nowQuestion < currentQuestions.size()) {
                Toggle selectedToggle = answersToggleGroup != null ? answersToggleGroup.getSelectedToggle() : null;

                if (selectedToggle != null) {
                    AnswerOption selectedOption = (AnswerOption) selectedToggle.getUserData();
                    Question currentQ = currentQuestions.get(nowQuestion);

                    if (selectedOption != null) {
                        boolean isCorrect = selectedOption.isCorrect();

                        if (currentAttemptId != -1) {
                            StudentAnswer studentAnswer = new StudentAnswer(
                                    currentAttemptId,
                                    currentQ.getQuestionId(),
                                    selectedOption.getOptionId(),
                                    isCorrect
                            );
                            studentAnswers.add(studentAnswer);
                            System.out.println("Recorded answer for Q " + currentQ.getQuestionId() + ": Option " + selectedOption.getOptionId() + (isCorrect ? " (Correct)" : " (Incorrect)"));
                        } else {
                            System.err.println("Cannot record answer: currentAttemptId is -1. Answer not saved.");
                        }

                        if (isCorrect) {
                            resultLabel.setText("Correct!");
                            resultLabel.setTextFill(javafx.scene.paint.Color.GREEN);
                        } else {
                            resultLabel.setText("Incorrect.");
                            resultLabel.setTextFill(javafx.scene.paint.Color.RED);

                            if (currentQ.getAnswerOptions() != null) {
                                for (AnswerOption option : currentQ.getAnswerOptions()) {
                                    if (option.isCorrect()) {
                                        resultLabel.setText(resultLabel.getText() + " The correct answer was: " + option.getOptionText());
                                        break;
                                    }
                                }
                            }
                        }
                        resultLabel.setVisible(true);
                        answerBtn.setText("Next Question");
                        answerBtn.setOnAction(this::handleNextQuestion);
                        answerBtn.setDisable(false);

                    } else {
                        System.err.println("Error: UserData not set for selected RadioButton.");
                        showAlert(Alert.AlertType.ERROR, "Internal Error", "Could not retrieve selected answer data.");
                    }
                } else {
                    showAlert(Alert.AlertType.WARNING, "No Answer Selected", "Please select an answer.");
                }
            }
        } else if ("Next Question".equals(answerBtn.getText())) {
            handleNextQuestion(event);
        }
    }

    @FXML
    void handleNextQuestion(ActionEvent event) {
        if (nowQuestion < currentQuestions.size() && !studentAnswers.isEmpty()) {
            StudentAnswer lastAnswer = studentAnswers.get(studentAnswers.size() - 1);
            if (lastAnswer.isCorrect()) {
                correctAnswers++;
            }
        }

        nowQuestion++;
        if (answersContainer != null) {
            answersContainer.getChildren().clear();
        }

        loadCurrentQuestion();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}