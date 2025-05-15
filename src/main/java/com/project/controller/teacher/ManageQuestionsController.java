package com.project.controller.teacher;

import com.project.dao.QuestionDao;
import com.project.dao.QuestionDaoImpl; // Предполагаемая реализация
// import com.project.dao.TestDao; // Не нужен здесь, если тест уже передан
// import com.project.dao.TestDaoImpl; // Не нужен здесь
import com.project.model.Question;
import com.project.model.Test;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class ManageQuestionsController {
    // @FXML private ComboBox<Test> comboTests; // Скорее всего, не нужен
    @FXML private Label lblTestName; // Для отображения названия текущего теста
    @FXML private TableView<Question> tableQuestions;
    @FXML private TableColumn<Question, Integer> colQId;
    @FXML private TableColumn<Question, String> colQText;
    @FXML private TableColumn<Question, String> colQType;
    @FXML private TextField txtQuestionText; // Переименовано для ясности
    @FXML private ComboBox<String> comboQuestionType; // Используем ComboBox для типов вопросов
    @FXML private Button btnAddQuestion;
    @FXML private Button btnDeleteQuestion;
    @FXML private Button btnRefreshQuestions; // Добавим кнопку обновления

    private Test currentTest; // Тест, для которого управляем вопросами
    private QuestionDao questionDao;
    private ObservableList<Question> questionList;

    @FXML
    public void initialize() {
        questionDao = new QuestionDaoImpl(); // Создаем экземпляр DAO
        questionList = FXCollections.observableArrayList();

        // comboTests.setOnAction(event -> loadQuestions()); // Удаляем, т.к. тест будет установлен извне

        colQId.setCellValueFactory(new PropertyValueFactory<>("questionId")); // Убедитесь, что поле называется questionId
        colQText.setCellValueFactory(new PropertyValueFactory<>("questionText")); // Убедитесь, что поле называется questionText
        colQType.setCellValueFactory(new PropertyValueFactory<>("questionType")); // Убедитесь, что поле называется questionType

        tableQuestions.setItems(questionList);

        // Заполняем ComboBox для типов вопросов
        comboQuestionType.setItems(FXCollections.observableArrayList(
                "MULTIPLE_CHOICE", "TRUE_FALSE", "SHORT_ANSWER", "ESSAY" // Соответствует CHECK в БД
        ));
        comboQuestionType.setValue("MULTIPLE_CHOICE"); // Тип по умолчанию
    }

    /**
     * Устанавливает текущий тест и загружает его вопросы.
     * Этот метод должен вызываться из ManageTestsController.
     */
    public void setTestContext(Test test) {
        this.currentTest = test;
        if (currentTest != null) {
            lblTestName.setText("Managing Questions for: " + currentTest.getTestName());
            // comboTests.setValue(currentTest); // Если бы ComboBox остался и был для отображения
            // comboTests.setDisable(true);      // и был бы заблокирован
            loadQuestions();
        } else {
            lblTestName.setText("No test selected.");
            questionList.clear(); // Очищаем таблицу, если тест не выбран
        }
    }

    @FXML
    private void handleRefreshQuestions(ActionEvent event) {
        loadQuestions();
    }

    private void loadQuestions() {
        if (currentTest != null) {
            try {
                List<Question> questionsFromDb = questionDao.getQuestionsByTestId(currentTest.getTestId());
                questionList.setAll(questionsFromDb);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load questions: " + e.getMessage());
            }
        } else {
            questionList.clear();
        }
    }

    @FXML
    private void handleAddQuestion(ActionEvent event) {
        if (currentTest == null) {
            showAlert(Alert.AlertType.WARNING, "No Test Selected", "Please select a test first (this should not happen).");
            return;
        }

        String text = txtQuestionText.getText();
        String type = comboQuestionType.getValue(); // Берем из ComboBox

        if (text == null || text.trim().isEmpty() || type == null || type.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Question text and type cannot be empty.");
            return;
        }

        // ID вопроса будет сгенерирован БД. Передаем 0 или null.
        // Предполагаем, что Question model и DAO готовы к этому.
        // Для простоты, пока не добавляем баллы (points) при создании.
        Question newQuestion = new Question(0, currentTest.getTestId(), text, type, 1); // ID 0, points 1 по умолчанию
        try {
            questionDao.addQuestion(newQuestion);
            loadQuestions(); // обновляем список
            txtQuestionText.clear();
            // comboQuestionType.setValue("MULTIPLE_CHOICE"); // Оставляем выбранный тип или сбрасываем
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add question: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteQuestion(ActionEvent event) {
        Question selectedQuestion = tableQuestions.getSelectionModel().getSelectedItem();
        if (selectedQuestion != null) {
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete this question and all its associated answers/options?",
                    ButtonType.YES, ButtonType.NO);
            confirmationDialog.setTitle("Confirm Deletion");
            confirmationDialog.setHeaderText("Delete Question: " + selectedQuestion.getQuestionText().substring(0, Math.min(selectedQuestion.getQuestionText().length(), 30)) + "...");

            confirmationDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        questionDao.deleteQuestion(selectedQuestion.getQuestionId());
                        loadQuestions();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete question: " + e.getMessage());
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a question to delete.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}