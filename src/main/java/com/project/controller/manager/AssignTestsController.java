package com.project.controller.manager;

import com.project.controller.util.UserContextAware;
import com.project.dao.TestDao;
import com.project.dao.TestDaoImpl;
import com.project.dao.CourseDao;
import com.project.dao.CourseDaoImpl;
import com.project.dao.UserDao;
import com.project.dao.UserDaoImpl;

import com.project.model.Test;
import com.project.model.User;
import com.project.model.Course;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class AssignTestsController implements Initializable, UserContextAware {

    @FXML
    private TableView<Test> testsTable;
    @FXML private TableColumn<Test, Integer> testIdColumn;
    @FXML private TableColumn<Test, String> testNameColumn;
    @FXML private TableColumn<Test, String> courseNameColumn;
    @FXML private TableColumn<Test, Integer> durationColumn;
    @FXML private TableColumn<Test, Boolean> activeColumn;
    @FXML private TableColumn<Test, String> creatorNameColumn;

    @FXML
    private Button btnEditAssignment;
    @FXML
    private Button btnRefresh;

    private TestDao testDao;
    private CourseDao courseDao;
    private UserDao userDao;

    private ObservableList<Test> testList;
    private User loggedInManager;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        testDao = new TestDaoImpl();
        courseDao = new CourseDaoImpl();
        userDao = new UserDaoImpl();

        testList = FXCollections.observableArrayList();

        testIdColumn.setCellValueFactory(new PropertyValueFactory<>("testId"));
        testNameColumn.setCellValueFactory(new PropertyValueFactory<>("testName"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseNameDisplay"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
        creatorNameColumn.setCellValueFactory(new PropertyValueFactory<>("creatorNameDisplay"));

        testsTable.setItems(testList);

        if (btnEditAssignment != null) btnEditAssignment.setDisable(true);
        testsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    boolean isSelected = newValue != null;
                    if (btnEditAssignment != null) btnEditAssignment.setDisable(!isSelected);
                }
        );
    }

    @Override
    public void setUserContext(User user) {
        this.loggedInManager = user;
        if (user != null) {
            System.out.println("Manager context set in AssignTestsController: " + user.getUsername());
            loadTests();
        } else {
            System.err.println("User context set in AssignTestsController is null.");
            showAlert(Alert.AlertType.ERROR, "Ошибка пользователя", "Контекст менеджера не установлен.");
            if (testList != null) testList.clear();
        }
    }

    private void loadTests() {
        if (loggedInManager == null) {
            System.err.println("loggedInManager is null in loadTests. Cannot load tests.");
            return;
        }
        try {
            List<Test> testsFromDb = testDao.findAll();
            testList.setAll(testsFromDb);
            System.out.println("Loaded " + testsFromDb.size() + " tests for assignment.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load tests for assignment: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadTests();
    }

    private void handleEditAssignment(ActionEvent event) {
        Test selectedTest = testsTable.getSelectionModel().getSelectedItem();
        if (selectedTest != null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/com/project/view/manager/AssignmentEditDialog.fxml"));
                GridPane dialogPane = loader.load();

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Edit Test Assignment: " + selectedTest.getTestName());
                dialogStage.initModality(Modality.WINDOW_MODAL);

                Stage ownerStage = (Stage) testsTable.getScene().getWindow();
                if (ownerStage != null) {
                    dialogStage.initOwner(ownerStage);
                }

                Scene scene = new Scene(dialogPane);
                dialogStage.setScene(scene);

                AssignmentEditDialogController controller = loader.getController();
                controller.setDialogStage(dialogStage);
                controller.setTest(selectedTest);
                controller.setCourses(courseDao.findAll());

                dialogStage.showAndWait();

                if (controller.isSaveClicked()) {
                    loadTests();
                    showAlert(Alert.AlertType.INFORMATION, "Save Successful", "Test assignment updated.");
                } else {
                    System.out.println("Assignment edit cancelled.");
                }

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Loading Error", "Could not load the assignment edit dialog: " + e.getMessage());
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load courses for assignment dialog: " + e.getMessage());
            } catch (ClassCastException e) {
                e.printStackTrace();
                System.err.println("Error casting FXML root or stage owner in handleEditAssignment.");
                showAlert(Alert.AlertType.ERROR, "Internal Error", "Could not set up assignment edit dialog.");
            }

        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a test to edit assignment.");
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