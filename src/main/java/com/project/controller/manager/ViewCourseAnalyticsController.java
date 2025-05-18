package com.project.controller.manager;

import com.project.controller.util.UserContextAware;
import com.project.dao.CourseDao;
import com.project.dao.CourseDaoImpl;
import com.project.dao.StudentTestDao;
import com.project.dao.StudentTestDaoImpl;
import com.project.dao.TestDao;
import com.project.dao.TestDaoImpl;
import com.project.dao.QuestionDao;
import com.project.dao.QuestionDaoImpl;

import com.project.model.Course;
import com.project.model.User;
import com.project.model.Test;
import com.project.model.TestAnalyticsData;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;


public class ViewCourseAnalyticsController implements Initializable, UserContextAware {

    @FXML
    private ComboBox<Course> courseComboBox;
    @FXML
    private Label selectedCourseNameLabel;

    @FXML
    private TableView<TestAnalyticsData> testsAnalyticsTable;
    @FXML
    private TableColumn<TestAnalyticsData, String> testNameColumn;
    @FXML
    private TableColumn<TestAnalyticsData, Double> averageScorePercentageColumn;

    private User loggedInManager;
    private CourseDao courseDao;
    private TestDao testDao;
    private StudentTestDao studentTestDao;
    private QuestionDao questionDao;

    private ObservableList<Course> courseList;
    private ObservableList<TestAnalyticsData> analyticsDataList;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        courseDao = new CourseDaoImpl();
        testDao = new TestDaoImpl();
        studentTestDao = new StudentTestDaoImpl();
        questionDao = new QuestionDaoImpl();

        courseList = FXCollections.observableArrayList();
        courseComboBox.setItems(courseList);

        courseComboBox.setConverter(new javafx.util.StringConverter<Course>() {
            @Override
            public String toString(Course course) {
                return course != null ? course.getCourseName() : "";
            }

            @Override
            public Course fromString(String s) {
                return null;
            }
        });

        courseComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        selectedCourseNameLabel.setText("Selected Course: " + newValue.getCourseName());
                        loadCourseAnalytics(newValue);
                    } else {
                        selectedCourseNameLabel.setText("Selected Course: None");
                        if (analyticsDataList != null) analyticsDataList.clear();
                    }
                }
        );

        loadCourses();

        testNameColumn.setCellValueFactory(new PropertyValueFactory<>("testName"));
        averageScorePercentageColumn.setCellValueFactory(new PropertyValueFactory<>("averageScorePercentage"));

        analyticsDataList = FXCollections.observableArrayList();
        testsAnalyticsTable.setItems(analyticsDataList);
    }

    @Override
    public void setUserContext(User user) {
        this.loggedInManager = user;
        if (user != null) {
            System.out.println("Manager context set in ViewCourseAnalyticsController: " + user.getUsername());
            loadCourses();
        } else {
            System.err.println("User context set in ViewCourseAnalyticsController is null.");
            showAlert(Alert.AlertType.ERROR, "Ошибка пользователя", "Контекст менеджера не установлен.");
            if (courseList != null) courseList.clear();
            if (analyticsDataList != null) analyticsDataList.clear();
            selectedCourseNameLabel.setText("Selected Course: None");
        }
    }

    private void loadCourses() {
        if (loggedInManager == null) {
            System.err.println("loggedInManager is null in loadCourses. Cannot load courses.");
            return;
        }
        try {
            List<Course> courses = courseDao.findAll();
            courseList.setAll(courses);
            System.out.println("Loaded " + courses.size() + " courses for selection.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка базы данных", "Не удалось загрузить список курсов: " + e.getMessage());
        }
    }

    private void loadCourseAnalytics(Course course) {
        if (course == null) {
            if (analyticsDataList != null) analyticsDataList.clear();
            selectedCourseNameLabel.setText("Selected Course: None");
            return;
        }
        System.out.println("Loading analytics for course: " + course.getCourseName() + " (ID: " + course.getCourseId() + ")");

        if (analyticsDataList != null) analyticsDataList.clear();

        try {
            List<Test> testsInCourse = testDao.findByCourseId(course.getCourseId());

            if (testsInCourse != null && !testsInCourse.isEmpty()) {
                for (Test test : testsInCourse) {
                    int totalPossiblePoints = questionDao.getTotalPointsForTest(test.getTestId());

                    double averageScore = studentTestDao.getAverageScoreForTest(test.getTestId());

                    double averageScorePercentage = 0.0;
                    if (totalPossiblePoints > 0) {
                        averageScorePercentage = (averageScore / totalPossiblePoints) * 100.0;
                        averageScorePercentage = Math.round(averageScorePercentage * 100.0) / 100.0;
                    } else {

                        System.out.println("Test ID " + test.getTestId() + " has 0 total points. Average percentage is 0.");
                    }

                    TestAnalyticsData analyticsData = new TestAnalyticsData(
                            test.getTestId(),
                            test.getTestName(),
                            averageScorePercentage
                    );
                    analyticsDataList.add(analyticsData);
                }
                System.out.println("Loaded analytics for " + testsInCourse.size() + " tests in course ID: " + course.getCourseId());

            } else {

                System.out.println("No tests found for course ID: " + course.getCourseId());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Ошибка базы данных", "Не удалось загрузить аналитику по курсу: " + e.getMessage());
            if (analyticsDataList != null) analyticsDataList.clear();
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