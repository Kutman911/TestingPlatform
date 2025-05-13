
package com.project.controller.teacher;

import com.project.dao.TestDao;
import com.project.model.Test;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ManageTestsController {
    @FXML private TableView<Test> tableTests;
    @FXML private TableColumn<Test, Integer> colId;
    @FXML private TableColumn<Test, String> colTitle;
    @FXML private TableColumn<Test, Integer> colDuration;
    @FXML private TableColumn<Test, Boolean> colPublished;
    @FXML private TextField txtTitle;
    @FXML private TextField txtDuration;
    @FXML private CheckBox chkPublished;
    @FXML private Button btnAddTest;
    @FXML private Button btnDeleteTest;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colPublished.setCellValueFactory(new PropertyValueFactory<>("published"));
        loadTests();
    }

    private void loadTests() {
        tableTests.setItems(FXCollections.observableArrayList(TestDao.getAllTests()));
    }

    @FXML
    private void addTest(ActionEvent event) {
        String title = txtTitle.getText();
        int duration = Integer.parseInt(txtDuration.getText());
        boolean published = chkPublished.isSelected();
        Test test = new Test(title, duration, published);
        TestDao.addTest(test);
        loadTests();
        txtTitle.clear();
        txtDuration.clear();
        chkPublished.setSelected(false);
    }

    @FXML
    private void deleteTest(ActionEvent event) {
        Test selected = tableTests.getSelectionModel().getSelectedItem();
        if (selected != null) {
            TestDao.deleteTest(selected.getId());
            loadTests();
        }
    }
}
