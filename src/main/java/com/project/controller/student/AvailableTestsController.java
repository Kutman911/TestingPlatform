package com.project.controller.student;// AvailableTestsController.java
import com.project.dao.TestDao;
import com.project.dao.TestDaoImpl;
import com.project.model.Test;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.util.Callback;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AvailableTestsController implements Initializable {
    @FXML private TableView<Test> testsTable;
    @FXML private TableColumn<Test, String> titleColumn;
    @FXML private TableColumn<Test, Integer> durationColumn;
    @FXML private TableColumn<Test, Void> actionColumn;

    private TestDao testDao = new TestDaoImpl();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Bind columns to Test properties (requires getTitle() and getDuration())
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

        // Load tests from database and populate table
        List<Test> tests = null;
        try {
            tests = testDao.findAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        testsTable.getItems().addAll(tests);

        // Add a "Take Test" button to each row in the actionColumn
        Callback<TableColumn<Test, Void>, TableCell<Test, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Test, Void> call(final TableColumn<Test, Void> param) {
                final TableCell<Test, Void> cell = new TableCell<>() {
                    private final Button takeBtn = new Button("Take Test");
                    {
                        takeBtn.setOnAction((ActionEvent event) -> {
                            Test data = getTableView().getItems().get(getIndex());
                            // Placeholder action: log or handle test-taking
                           // System.out.println("Taking test: " + data.getTitle() + " (ID: " + data.getId() + ")");
                            // TODO: Implement navigation to test-taking scene
                        });
                    }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(takeBtn);
                        }
                    }
                };
                return cell;
            }
        };
        actionColumn.setCellFactory(cellFactory);
    }
}
