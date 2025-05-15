package com.project.controller.teacher;

import com.project.controller.util.UserContextAware; // Импортируем наш интерфейс
import com.project.dao.CourseDao;
import com.project.dao.CourseDaoImpl;
import com.project.dao.TestDao;
import com.project.dao.TestDaoImpl;
import com.project.dao.UserDao;
import com.project.dao.UserDaoImpl;
import com.project.model.Course;
import com.project.model.Test;
import com.project.model.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane; // Для диалога
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ManageTestsController implements UserContextAware { // Реализуем интерфейс

    @FXML
    private TableView<Test> testsTable;
    @FXML
    private TableColumn<Test, Integer> testIdColumn;
    @FXML
    private TableColumn<Test, String> testNameColumn;
    @FXML
    private TableColumn<Test, String> courseNameColumn;
    @FXML
    private TableColumn<Test, Integer> durationColumn;
    @FXML
    private TableColumn<Test, Boolean> activeColumn;
    @FXML
    private TableColumn<Test, String> creatorNameColumn;
    @FXML
    private TableColumn<Test, Void> questionsColumn;


    private TestDao testDao;
    private UserDao userDao;
    private CourseDao courseDao;

    private ObservableList<Test> testList;
    private User loggedInTeacher;

    @Override
    public void setUserContext(User user) {
        this.loggedInTeacher = user;
        if (loggedInTeacher == null || !"TEACHER".equals(loggedInTeacher.getRole())) {

            showAlert(Alert.AlertType.ERROR, "Access Denied", "You do not have permission to manage tests.");
            testsTable.setDisable(true); // Блокируем интерфейс
            return;
        }
        loadTests();
    }

    public void initialize() {
        testDao = new TestDaoImpl();
        userDao = new UserDaoImpl();
        courseDao = new CourseDaoImpl();
        testList = FXCollections.observableArrayList();

        testIdColumn.setCellValueFactory(new PropertyValueFactory<>("testId"));
        testNameColumn.setCellValueFactory(new PropertyValueFactory<>("testName"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseNameDisplay"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
        creatorNameColumn.setCellValueFactory(new PropertyValueFactory<>("creatorNameDisplay"));


        Callback<TableColumn<Test, Void>, TableCell<Test, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Test, Void> call(final TableColumn<Test, Void> param) {
                final TableCell<Test, Void> cell = new TableCell<>() {
                    private final Button btn = new Button("Manage Questions");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Test testData = getTableView().getItems().get(getIndex());
                            handleManageQuestions(testData);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        questionsColumn.setCellFactory(cellFactory);


        testsTable.setItems(testList);
        // loadTests() будет вызван из setUserContext
    }

    private void loadTests() {
        if (loggedInTeacher == null) {
            // Этого не должно произойти, если setUserContext вызывается правильно
            System.err.println("loggedInTeacher is null in loadTests. Cannot load tests.");
            return;
        }
        try {
            // Загружаем тесты, созданные этим учителем
            // TestDaoImpl.findByCreatorId() теперь также загружает course_name и creator_name
            List<Test> testsFromDb = testDao.findByCreatorId(loggedInTeacher.getId());
            testList.setAll(testsFromDb);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load tests: " + e.getMessage());
        }
    }

    @FXML
    void handleRefreshTests(ActionEvent event) {
        if (loggedInTeacher != null) {
            loadTests();
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Info", "User context not set. Cannot refresh.");
        }
    }

    @FXML
    void handleAddTest(ActionEvent event) {
        if (loggedInTeacher == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot add test: User context not available.");
            return;
        }
        showTestEditDialog(null, loggedInTeacher.getId());
    }

    @FXML
    void handleEditTest(ActionEvent event) {
        Test selectedTest = testsTable.getSelectionModel().getSelectedItem();
        if (selectedTest != null) {
            if (loggedInTeacher == null || selectedTest.getCreatorId() != loggedInTeacher.getId()) {
                showAlert(Alert.AlertType.ERROR, "Access Denied", "You can only edit tests you created.");
                return;
            }
            showTestEditDialog(selectedTest, loggedInTeacher.getId());
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a test to edit.");
        }
    }

    @FXML
    void handleDeleteTest(ActionEvent event) {
        Test selectedTest = testsTable.getSelectionModel().getSelectedItem();
        if (selectedTest != null) {
            if (loggedInTeacher == null || selectedTest.getCreatorId() != loggedInTeacher.getId()) {
                showAlert(Alert.AlertType.ERROR, "Access Denied", "You can only delete tests you created.");
                return;
            }

            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Confirm Deletion");
            confirmationDialog.setHeaderText("Delete Test: " + selectedTest.getTestName());
            confirmationDialog.setContentText("Are you sure you want to delete this test and all its associated questions? This action cannot be undone.");

            Optional<ButtonType> result = confirmationDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    boolean deleted = testDao.delete(selectedTest.getTestId());
                    if (deleted) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Test deleted successfully.");
                        loadTests();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Could not delete the test.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (e.getSQLState().equals("23503")) { // foreign_key_violation
                        showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Cannot delete test. It might have student attempts or other related data.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete test: " + e.getMessage());
                    }
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a test to delete.");
        }
    }

    private void handleManageQuestions(Test test) {
        if (test == null) return;
        System.out.println("Manage Questions clicked for test: " + test.getTestName() + " (ID: " + test.getTestId() + ")");
        // Здесь будет логика для перехода к окну управления вопросами для test.getTestId()
        // Например, загрузка нового FXML в центр MainForm или открытие нового окна.
        // mainFormController.loadView("/com/project/view/teacher/ManageQuestionsView.fxml", "Manage Questions for " + test.getTestName(), test);
        showAlert(Alert.AlertType.INFORMATION, "Manage Questions", "Functionality to manage questions for test '" + test.getTestName() + "' will be implemented here.");

        // В ManageTestsController.java -> handleManageQuestions(Test test)

// ... (существующий код)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/view/teacher/ManageQuestionsView.fxml"));
            BorderPane questionsView = loader.load(); // Используем BorderPane, т.к. это корневой элемент ManageQuestionsView.fxml

            ManageQuestionsController controller = loader.getController();
            if (controller != null) { // Проверка, что контроллер успешно загружен
                if (controller instanceof UserContextAware && loggedInTeacher != null) {
                    ((UserContextAware) controller).setUserContext(loggedInTeacher);
                }
                controller.setTestContext(test); // Передаем выбранный тест
            } else {
                System.err.println("ManageQuestionsController was not loaded!");
                showAlert(Alert.AlertType.ERROR, "Internal Error", "Could not initialize question management view controller.");
                return;
            }



            // : Открыть новое модальное окно
            Stage questionsStage = new Stage();
            questionsStage.setTitle("Manage Questions for: " + test.getTestName());
            questionsStage.initModality(Modality.WINDOW_MODAL);
            Stage ownerStage = (Stage) testsTable.getScene().getWindow();
            if (ownerStage != null) {
                questionsStage.initOwner(ownerStage);
            }
            Scene scene = new Scene(questionsView);
            questionsStage.setScene(scene);
            questionsStage.showAndWait();



        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Loading Error", "Could not load the manage questions view: " + e.getMessage());
        }



    }


    private boolean showTestEditDialog(Test test, int creatorId) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/project/view/teacher/TestEditDialog.fxml"));
            GridPane dialogPane = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(test == null ? "Add New Test" : "Edit Test");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(dialogPane);
            dialogStage.setScene(scene);

            TestEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setLoggedInTeacherId(creatorId); // Передаем ID учителя
            controller.setCourses(courseDao.findAll()); // Передаем список курсов для ComboBox
            controller.setTest(test); // null для нового теста

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                loadTests();
            }
            return controller.isSaveClicked();

        } catch (IOException | SQLException e) { // SQLException из-за courseDao.findAll()
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open test edit dialog: " + e.getMessage());
            return false;
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