
package com.project.controller.teacher;

import com.project.dao.QuestionDao;
import com.project.dao.TestDao;
import com.project.model.Question;
import com.project.model.Test;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ManageQuestionsController {
    @FXML private ComboBox<Test> comboTests;
    @FXML private TableView<Question> tableQuestions;
    @FXML private TableColumn<Question, Integer> colQId;
    @FXML private TableColumn<Question, String> colQText;
    @FXML private TableColumn<Question, String> colQType;
    @FXML private TextField txtQuestion;
    @FXML private TextField txtType;
    @FXML private Button btnAddQuestion;
    @FXML private Button btnDeleteQuestion;

    @FXML
    public void initialize() {
        // Загрузка списка тестов в ComboBox
        comboTests.setItems(FXCollections.observableArrayList(TestDao.getAllTests()));
        comboTests.setOnAction(event -> loadQuestions());
        // Настройка колонок таблицы
        colQId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colQText.setCellValueFactory(new PropertyValueFactory<>("text"));
        colQType.setCellValueFactory(new PropertyValueFactory<>("type"));
    }

    // Загружает вопросы выбранного теста в таблицу
    private void loadQuestions() {
        Test selected = comboTests.getSelectionModel().getSelectedItem();
        if (selected != null) {
            tableQuestions.setItems(FXCollections.observableArrayList(
                    QuestionDao.getQuestionsByTest(selected.getId())
            ));
        }
    }

    @FXML
    private void addQuestion(ActionEvent event) {
        Test selected = comboTests.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String text = txtQuestion.getText();
            String type = txtType.getText();
            Question q = new Question(selected.getId(), text, type);
            QuestionDao.addQuestion(q);
            loadQuestions(); // обновляем список
            txtQuestion.clear();
            txtType.clear();
        }
    }

    @FXML
    private void deleteQuestion(ActionEvent event) {
        Question selected = tableQuestions.getSelectionModel().getSelectedItem();
        if (selected != null) {
            QuestionDao.deleteQuestion(selected.getId());
            loadQuestions();
        }
    }
}
