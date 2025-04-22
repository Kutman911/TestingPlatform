package com.project.controller;

import com.project.db.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
public class LoggedInController {
    public TextField txtUserName;
    public PasswordField txtPassword;
    public AnchorPane root;
    public void btnSignIn(ActionEvent actionEvent) {
        try {
            String Username =  txtUserName.getText();
            String Password =  txtPassword.getText();
            Connection connection = Database.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select * from user where username =? and password = ?");
            preparedStatement.setString(1,Username);
            preparedStatement.setString(2,Password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
            {
                Parent parent = FXMLLoader.load(this.getClass().getResource("../view/MainForm.fxml"));
                Scene scene = new Scene(parent);
                Stage primarystage = (Stage) root.getScene().getWindow();
                primarystage.setScene(scene);
                primarystage.setTitle("Main Form");
                primarystage.centerOnScreen();
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "UserName or Password Do not Match");
                alert.showAndWait();
                txtUserName.clear();
                txtPassword.clear();
                txtUserName.requestFocus();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    public void btnSignup(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("../resources/com/project/view/logged-in.fxml"));
        Scene scene = new Scene(parent);
        Stage primaryStage = (Stage) root.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Register Form");
        primaryStage.centerOnScreen();
    }
}