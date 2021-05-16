package fxControl;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.*;
import utils.DbOperations;

import javax.swing.*;
import java.net.URL;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainWindow implements Initializable {
    public ListView allCourses;
    public Tab myCreatedCourses;
    public Tab myCourses;
    public ListView studentCourses;
    public TextField enterName;
    public DatePicker getStartDate;
    public DatePicker getEndDate;
    public TextField enterPrice;
    public ListView adminCourses;
    public ComboBox myCoursesBox;
    public ListView folders;
    public ListView files;
    public Tab faf;
    public TextField newFolderName;
    public TextField newFileName;
    public ListView courses;
    public ListView studentFolders;
    public ListView studentFiles;
    public TextField eLogin;
    public TextField eEmail;
    public TextField ePhoneNr;
    public PasswordField eConfirm1;
    public PasswordField eNewPsw1;
    public PasswordField eNewPsw2;
    public PasswordField eConfirm2;
    private int courseIS;
    private String currentUser;
    private boolean isAdmin;
    private Connection connection;
    private PreparedStatement statement;

    public void setFormData(int courseIS, String loginName, boolean is_admin) throws SQLException {
        // Set starting settings to the application and collect info from login form
        this.enterName.setDisable(true);
        this.courseIS = courseIS;
        this.currentUser = loginName;
        this.isAdmin = is_admin;
        if(isAdmin) {
            this.myCourses.setDisable(true);
        } else {
            this.myCreatedCourses.setDisable(true);
            this.faf.setDisable(true);
            this.ePhoneNr.setDisable(true);
        }
        // Fill fields with required data
        fillWithData();
        fillWithData2();
        fillWithData3();
        fillWithData4();
        showAllCourses();
        fillFields();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public static void alertMessage(String alertMessage){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(alertMessage);
        alert.showAndWait();
    }

    // Shows all courses in the current information system
    private void showAllCourses() throws SQLException {
        this.courses.getItems().clear();
        DbOperations.getAllCourses(courseIS).forEach((c -> courses.getItems().add(c.toString())));
    }

    //  Fills listView elements with courses, based on which courses the Student is enrolled in or which courses Administrator is responsible for

    private void fillWithData() throws SQLException {
        this.studentCourses.getItems().clear();
        ArrayList<Course> courses = DbOperations.getStudentCourses(this.currentUser, this.courseIS);

        for(int i = 0; i < courses.size(); i++)
            this.studentCourses.getItems().add(courses.get(i).getName());
    }

    private void fillWithData2() throws SQLException {
        this.allCourses.getItems().clear();
        ArrayList<Course> courses = DbOperations.getAvailableCourses(this.currentUser, this.courseIS);

        for(int i = 0; i < courses.size(); i++)
                this.allCourses.getItems().add(courses.get(i).getName());

    }
    private void fillWithData3() throws SQLException {
        this.adminCourses.getItems().clear();
        ArrayList<Course> courses = DbOperations.getCoursesByAdmin(this.currentUser);

        for(int i = 0; i < courses.size(); i++)
            this.adminCourses.getItems().add(courses.get(i).getName());
    }
    private void fillWithData4() throws SQLException {
        this.myCoursesBox.getItems().clear();
        ArrayList<Course> courses = DbOperations.getCoursesByAdmin(this.currentUser);

        for(int i = 0; i < courses.size(); i++)
            this.myCoursesBox.getItems().add(courses.get(i).getName());
    }


    // Student enrolls in a course
    public void enroll(ActionEvent actionEvent) throws SQLException {
        if (this.isAdmin)
            alertMessage("You are not a student!");
        else{
            // Gathers the required data from database
            int id1=DbOperations.getCourseID(this.allCourses.getSelectionModel().getSelectedItem().toString());
            int id2=DbOperations.getUserID(this.currentUser);

            if(id1 == 0 || id2 == 0){
                alertMessage("Oops, something went wrong when enrolling to a course!");
            }else{
                // Fill the database with enrolled course for a user
                DbOperations.enrollToCourse(id1, id2);
                alertMessage("You enrolled in a course, congratz!");

                // Fill new data
                fillWithData();
                fillWithData2();
                fillWithData3();
                fillWithData4();
            }
        }
    }

    // Pop up window for view course info
    public static void showStage(String name, String startDate, String endDate, String price){
        Stage newStage = new Stage();
        newStage.setTitle("Information");
        VBox comp = new VBox();
        Text nameText = new Text("Name: " + name);
        nameText.setStyle("-fx-font: 24 arial;");
        Text startText = new Text("Start date: " + startDate);
        startText.setStyle("-fx-font: 24 arial;");
        Text endText = new Text("End date: " + endDate);
        endText.setStyle("-fx-font: 24 arial;");
        Text priceText = new Text("Price: " + price);
        priceText.setStyle("-fx-font: 24 arial;");
        comp.getChildren().add(nameText);
        comp.getChildren().add(startText);
        comp.getChildren().add(endText);
        comp.getChildren().add(priceText);

        Scene stageScene = new Scene(comp, 400, 120);
        newStage.setScene(stageScene);
        newStage.show();
    }

    // Student leaves course
    public void leaveCourse(ActionEvent actionEvent) throws SQLException {
        // Collect required data from database based on field info
        String name = this.studentCourses.getSelectionModel().getSelectedItem().toString();
        int id1 = DbOperations.getUserID(this.currentUser);
        int id2 = DbOperations.getCourseID(name);
        // Remove enrolled course from database and fill new data
        DbOperations.leaveCourse(id1, id2);
        fillWithData();
        fillWithData2();
        fillWithData3();
        fillWithData4();
    }

    // get course info on button click
    public void viewCourseInfo1(ActionEvent actionEvent) throws SQLException {
        String name = this.allCourses.getSelectionModel().getSelectedItem().toString();
        String[] info = DbOperations.courseInfo(name);
        showStage(name, info[0], info[1], info[2]);
    }

    public void viewCourseInfo2() throws SQLException {
        String name = this.studentCourses.getSelectionModel().getSelectedItem().toString();
        String[] info = DbOperations.courseInfo(name);
        showStage(name, info[0], info[1], info[2]);
    }

    // Administrator user creates new course
    public void createCourse(ActionEvent actionEvent) throws SQLException {
        int id = DbOperations.getUserID(this.currentUser);

        // get values from fields required for course creation
        LocalDate date1 = this.getStartDate.getValue();
        LocalDate date2 = this.getEndDate.getValue();
        Course newCourse = new Course(this.enterName.getText(), date1, date2, id, Double.parseDouble(this.enterPrice.getText()), this.courseIS);

        // Add course to the database
        DbOperations.insertCourse(newCourse);

        // Refresh shown data
        fillWithData();
        fillWithData2();
        fillWithData3();
        fillWithData4();
        showAllCourses();
    }

    // Administrator user deletes one of his courses
    public void deleteCourse(ActionEvent actionEvent) throws SQLException {
        // Delete course from database based on selected course
        String name = this.adminCourses.getSelectionModel().getSelectedItem().toString();
        DbOperations.deleteCourse(name);
        alertMessage("Course deleted!");

        // Refresh data
        fillWithData();
        fillWithData2();
        fillWithData3();
        fillWithData4();
        showAllCourses();
    }

    // Shows courses folders
    public void showFolders() throws SQLException {
        this.folders.getItems().clear();
        // Gathers data to decide which courses folders to show
        String name = this.myCoursesBox.getSelectionModel().getSelectedItem().toString();
        // Gets folders from database
        ArrayList<Folder> folders= DbOperations.getCourseFolders(name);

        // Fills with data
        for(int i = 0; i < folders.size(); i++)
            this.folders.getItems().add(folders.get(i).getName());

    }

    public void showFiles() throws SQLException {
        this.files.getItems().clear();
        // Gathers data to decide which folders files to show
        String name = this.folders.getSelectionModel().getSelectedItem().toString();
        // Gets files from database
        ArrayList<File> files = DbOperations.getAllFolderFiles(name);

        // Fills with data
        for(int i = 0; i < files.size(); i++)
            this.files.getItems().add(files.get(i).getName());
    }

    public void createNewFolder(ActionEvent actionEvent) throws SQLException {
        if(this.newFolderName.getText() != null)
            DbOperations.createCourseFolder(this.newFolderName.getText(), this.myCoursesBox.getSelectionModel().getSelectedItem().toString());
        // Refresh data
        showFolders();
    }


    public void deleteFolder(ActionEvent actionEvent) throws SQLException {
        String folderName = this.folders.getSelectionModel().getSelectedItem().toString();
        String courseName = this.myCoursesBox.getValue().toString();
        // Deletes folder based on selection
        DbOperations.deleteCourseFolder(folderName, courseName);
        alertMessage("Folder deleted!");
        this.files.getItems().clear();
        // Refresh data
        showFolders();
    }

    public void createNewFile(ActionEvent actionEvent) throws SQLException {
        String folderName = this.folders.getSelectionModel().getSelectedItem().toString();
        String fileName = this.newFileName.getText();
        // Create new file name if it exists
        if(fileName.equals(""))
            DbOperations.createFolderFile(folderName, fileName);
        // Refresh data
        showFiles();
    }

    public void deleteFile(ActionEvent actionEvent) throws SQLException {
        String folderName = this.folders.getSelectionModel().getSelectedItem().toString();
        String fileName = this.files.getSelectionModel().getSelectedItem().toString();
        // Deletes folder based on selection
        DbOperations.deleteFolderFile(folderName, fileName);
        alertMessage("File deleted!");
        // Refresh data
        showFiles();
    }

    // Show selected course folders to student
    public void showStudentFolders(ActionEvent actionEvent) throws SQLException {
        this.studentFolders.getItems().clear();
        String name = this.studentCourses.getSelectionModel().getSelectedItem().toString();
        ArrayList<Folder> folders= DbOperations.getCourseFolders(name);

        for(int i = 0; i < folders.size(); i++)
            this.studentFolders.getItems().add(folders.get(i).getName());
    }

    // Show selected course folder files to student
    public void showStudentFiles(ActionEvent actionEvent) throws SQLException {
        this.studentFiles.getItems().clear();
        String name = this.studentFolders.getSelectionModel().getSelectedItem().toString();
        ArrayList<File> files = DbOperations.getAllFolderFiles(name);

        for(int i = 0; i < files.size(); i++)
            this.studentFiles.getItems().add(files.get(i).getName());
    }

    // User can change his password
    public void savePsw(ActionEvent actionEvent) throws SQLException {
        String psw1 = this.eNewPsw1.getText();
        String psw2 = this.eNewPsw2.getText();
        String oldPsw = this.eConfirm2.getText();
        // Checks if new password fields match
        // Checks if new password is not old password ( has to confirm with old password)
        if(psw1.equals(psw2) && oldPsw.equals(DbOperations.getPassword(this.currentUser)) && psw1.length() > 4 && !psw1.equals(DbOperations.getPassword(this.currentUser))){
            DbOperations.updateUserPsw(this.currentUser, psw1);
            alertMessage("Password changed!");
        } else{
            alertMessage("Passwords dont match / Incorrect current password!");
        }
    }

    // User can edit his info (login, email, phone number)
    public void saveInfo(ActionEvent actionEvent) throws SQLException {
        String login = this.eLogin.getText();
        String email = this.eEmail.getText();
        String phoneNr = this.ePhoneNr.getText();
        // If not admin, phone number field is not used in user update operation
        if(isAdmin && DbOperations.getPassword(this.currentUser).equals(this.eConfirm1.getText()) && login.equals("") && email.equals("") && phoneNr.equals("")){
            DbOperations.updateAdmin(this.currentUser, login, email, phoneNr);
            alertMessage("Profile saved!");
        } else if(!isAdmin && DbOperations.getPassword(this.currentUser).equals(this.eConfirm1.getText()) && login.equals("") && email.equals("")){
            DbOperations.updateStudent(this.currentUser, login, email);
            alertMessage("Profile saved!");
        } else{
            alertMessage("Check the field information!");
        }
    }

    // Fills user edit info with his current information
    public void fillFields() throws SQLException {
        User user = DbOperations.getUserByName(this.currentUser);
        this.eLogin.setText(user.getLogin());
        this.eEmail.setText(user.getEmail());

        if(isAdmin)
            this.ePhoneNr.setText(DbOperations.getAdminPhoneNumber(this.currentUser));
    }
}
