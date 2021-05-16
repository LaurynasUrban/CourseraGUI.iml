package fxControl;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Administrator;
import model.CourseIS;
import model.Student;
import model.User;
import utils.DbOperations;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Login implements Initializable {

    public Button logInBtn;
    public Button signUpBtn;
    public TextField loginField;
    public CheckBox empChk;
    public PasswordField pswField;
    public ComboBox coursesBox;
    private Connection connection;
    private PreparedStatement statement;
    private User currentUser = null;
    private int courseIsId = 0;
    private boolean is_admin;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        coursesBox.getItems().addAll(DbOperations.getAllCourseIS());
    }

    //Login button
    public void validateAndLogin(ActionEvent actionEvent) throws IOException, SQLException{
        // Takes the id of an information system from the combo box
        courseIsId = Integer.parseInt(coursesBox.getValue().toString().split("\\(")[1].replace(")", ""));

        // Checks whether to create and Administrator object or Student object based on login fields info
        boolean is_admin = false;
        if(empChk.isSelected() == true && loginField.getText() != null && pswField.getText() != null && courseIsId != 0){
            currentUser = DbOperations.getAdminAcc(loginField.getText(), pswField.getText(), courseIsId);
            is_admin = true;
        } else if(empChk.isSelected() == false && loginField.getText() != null && pswField.getText() != null && courseIsId != 0){
            currentUser = DbOperations.getStudentAcc(loginField.getText(), pswField.getText(), courseIsId);
        }

        // If the with the input credentials exists, then load the mainWindow form
        if(currentUser != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/mainWindow.fxml"));
            Parent root = loader.load();
            MainWindow mainWindow = loader.getController();
            mainWindow.setFormData(courseIsId, currentUser.getLogin(), is_admin);
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } else{
            MainWindow.alertMessage("Wrong credentials!");
        }
    }

        //Signup button, takes the user to signUp form
        public void loadSignUpForm (ActionEvent actionEvent) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/signUpForm.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        }

    }
