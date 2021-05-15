package fxControl;

import com.sun.tools.javac.Main;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.DbOperations;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SignUpForm implements Initializable {
    public PasswordField pswField1;
    public PasswordField pswField2;
    public TextField loginField;
    public TextField emailField;
    public ComboBox courseIsBox;
    private int courseIsId = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        courseIsBox.getItems().addAll(DbOperations.getAllCourseIS());
    }

    public void returnToLogin(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/login.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) loginField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void createUser(ActionEvent actionEvent) throws SQLException, IOException {
        String name = loginField.getText();
        String email = emailField.getText();
        String psw1 = pswField1.getText();
        String psw2 = pswField2.getText();
        courseIsId = Integer.parseInt(courseIsBox.getValue().toString().split("\\(")[1].replace(")", ""));

        if(psw1.equals(psw2) && name != null && email != null) {
            DbOperations.insertStudent(name, psw1, email, courseIsId);
            MainWindow.alertMessage("Account created!");
            returnToLogin(actionEvent);
        } else{
            MainWindow.alertMessage("Incorrect information provided!");
        }
    }
}
