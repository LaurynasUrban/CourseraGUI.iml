package utils;

import fxControl.MainWindow;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import model.*;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class DbOperations {
    private static Connection connection;
    private static PreparedStatement statement;

    public static Connection connectToDb() {

        String DB_URL = "jdbc:mysql://localhost/coursera";
        String USER = "root";
        String PASS = "";
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void disconnectFromDb(Connection connection, Statement statement) {
        try {
            if (connection != null && statement != null) {
                connection.close();
                statement.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getAllCourseIS(){
        List<String> options = new ArrayList<>();
        connection = connectToDb();
        if (connection == null) {
            MainWindow.alertMessage("Unable to connect");
            Platform.exit();
        } else {
            try {
                statement = connection.prepareStatement("SELECT * FROM course_is");
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    options.add(rs.getString(2) + "(" + rs.getInt(1) + ")");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        disconnectFromDb(connection, statement);
        return options;
    }

    //  COURSES  |
    //           V

    public static ArrayList<Course> getAllCourses(int courseIs) throws SQLException {
        ArrayList<Course> allCourses = new ArrayList<>();
        connection = connectToDb();
        String sql = "SELECT * FROM course AS c WHERE c.course_is = " + courseIs;
        statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            allCourses.add(new Course(rs.getInt(1), rs.getString(2), LocalDate.parse(rs.getString(3)), LocalDate.parse(rs.getString(4)), rs.getInt(5), rs.getDouble(6), rs.getInt(7)));
        }
        disconnectFromDb(connection, statement);
        return allCourses;
    }

    public static ArrayList<Course> getCoursesByAdmin(String name) throws SQLException {
        int id = getAdminId(name);
        ArrayList<Course> courses = new ArrayList<>();
        connection = connectToDb();
        String sql = "SELECT * FROM course AS c WHERE c.admin_id = " + id;
        statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            courses.add(new Course(rs.getInt(1), rs.getString(2), LocalDate.parse(rs.getString(3)), LocalDate.parse(rs.getString(4)), rs.getInt(5), rs.getDouble(6), rs.getInt(7)));
        }
        disconnectFromDb(connection, statement);
        return courses;
    }

    public static int getAdminId(String name) throws SQLException {
        connection = connectToDb();
        String sql = "SELECT * FROM `users` AS u WHERE u.login = " + "'" + name + "'";
        statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery(sql);
        int id=0;
        while (rs.next()) {
            id = rs.getInt(1);
        }
        disconnectFromDb(connection, statement);
        return id;
    }

    public static Course getCourseByName(String name) throws SQLException {
        Course course = null;
        connection = connectToDb();
        String sql = "SELECT * FROM course AS c WHERE c.name = ?";
        statement = connection.prepareStatement(sql);
        statement.setString(1, name);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            course = new Course(rs.getInt(1), rs.getString(2), LocalDate.parse(rs.getString(3)), LocalDate.parse(rs.getString(4)), rs.getInt(5), rs.getDouble(6), rs.getInt(7));
        }
        disconnectFromDb(connection, statement);
        return course;
    }

    public static void deleteCourse(String name) throws SQLException {
        int courseId = getCourseID(name);
        ArrayList<Integer> folders = getAllFoldersIdFromCourse(courseId);

        for(int i = 0; i < folders.size(); i++){
            deleteFile(folders.get(i));
        }
        deleteFolders(courseId);
        deleteCourseStudents(courseId);


        connection = connectToDb();
        String sql = "DELETE FROM `course` WHERE name = " + "'" + name + "'";
        statement = connection.prepareStatement(sql);
        statement.execute();
        disconnectFromDb(connection, statement);
    }

    public static void deleteCourseStudents(int id) throws SQLException {
        connection = connectToDb();
        String sql = "DELETE FROM `user_enroll_course` WHERE course_id = ?";
        statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.execute();
        disconnectFromDb(connection, statement);
    }


    public static ArrayList<Integer> getAllFoldersIdFromCourse(int id) throws SQLException {
        ArrayList<Integer> IdList = new ArrayList<>();
        connection = connectToDb();
        String sql = "SELECT * FROM folder WHERE folder.course_id = " + id;
        statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            IdList.add(rs.getInt(1));
        }
        disconnectFromDb(connection, statement);
        return IdList;
    }

    public static void deleteFolders(int id) throws SQLException {
        connection = connectToDb();
        String sql = "DELETE FROM `folder` WHERE course_id = ?";
        statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.execute();
        disconnectFromDb(connection, statement);
    }

    public static void deleteFile(int id) throws SQLException {
        connection = connectToDb();
        String sql = "DELETE FROM `folder_files` WHERE folder_id = ?";
        statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.execute();
        disconnectFromDb(connection, statement);
    }

    public static int getCourseID(String name) throws SQLException {
        connection = connectToDb();
        String sql = "SELECT * FROM course WHERE course.name = " + "'" + name + "'";
        statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery(sql);
        int id=0;
        while (rs.next()) {
            id = rs.getInt(1);
        }
        disconnectFromDb(connection, statement);
        return id;
    }


    public static void insertCourse(Course course){
        try {
            connection = connectToDb();
            String sql = "INSERT INTO `course` (`name`, `start_date`, `end_date`, `admin_id`, `course_price`, `course_is`) VALUES (?,?,?,?,?,?)";
            statement = connection.prepareStatement(sql);
            PreparedStatement insert = connection.prepareStatement(sql);
            insert.setString(1, course.getName());
            insert.setDate(2, java.sql.Date.valueOf(course.getStartDate()));
            insert.setDate(3, java.sql.Date.valueOf(course.getEndDate()));
            insert.setInt(4, course.getAdminId());
            insert.setDouble(5, course.getCoursePrice());
            insert.setInt(6, course.getCourseIs());
            insert.execute();
            disconnectFromDb(connection, statement);

            MainWindow.alertMessage("Course created!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateCourse(int id, String colName, Double newValue) throws SQLException {
        if (newValue != 0) {
            connection = connectToDb();
            String sql = "UPDATE course SET `" + colName + "`  = ? WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setDouble(1, newValue);
            statement.setInt(2, id);
            statement.executeUpdate();
            disconnectFromDb(connection, statement);
        }
    }

    public static void updateCourse(int id, String colName, String newValue) throws SQLException {
        if (!newValue.equals("")) {
            connection = connectToDb();
            String sql = "UPDATE `course` SET `" + colName + "`  = ? WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, newValue);
            statement.setInt(2, id);
            statement.executeUpdate();
            disconnectFromDb(connection, statement);
        }
    }

    public static void updateCourse(int id, String colName, LocalDate newValue) throws SQLException {
        connection = connectToDb();
        String sql = "UPDATE `course` SET `" + colName + "`  = ? WHERE id = ?";
        statement = connection.prepareStatement(sql);
        statement.setDate(1, Date.valueOf(newValue));
        statement.setInt(2, id);
        statement.executeUpdate();
        disconnectFromDb(connection, statement);
    }

    public static String[] courseInfo(String name) throws SQLException {
        String[] info = new String[3];
        connection = connectToDb();
        String sql = "SELECT * FROM course WHERE course.name = " + "'" + name + "'";
        statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            info[0] = rs.getString(3);
            info[1] = rs.getString(4);
            info[2] = rs.getString(6);
        }
        disconnectFromDb(connection, statement);
        return info;
    }


    public static void leaveCourse(int id1, int id2) throws SQLException {
        connection = connectToDb();
        String sql = "DELETE FROM `user_enroll_course` WHERE user_id = ? AND course_id = ?";
        statement = connection.prepareStatement(sql);
        statement.setInt(1, id1);
        statement.setInt(2, id2);
        statement.execute();
        disconnectFromDb(connection, statement);
    }

    public static void enrollToCourse(int id1, int id2) throws SQLException {
        connection = connectToDb();
        String sql = "INSERT INTO `user_enroll_course` (`user_id`, `course_id`) VALUES (?,?)";
        statement = connection.prepareStatement(sql);
        PreparedStatement insert = connection.prepareStatement(sql);
        insert.setInt(1, id2);
        insert.setInt(2, id1);
        insert.execute();
        DbOperations.disconnectFromDb(connection, statement);
    }

    public static ArrayList<Integer> getUserCourses(String name) throws SQLException {
        ArrayList<Integer> courses = new ArrayList<Integer>();
        int userID = getUserID(name);
        connection = connectToDb();
        String sql = "SELECT * FROM user_enroll_course AS c WHERE c.user_id = " + userID;
        statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            courses.add(rs.getInt(2));
        }
        disconnectFromDb(connection, statement);

        return courses;
    }

    public static ArrayList<Course> getStudentCourses(String name, int courseIS) throws SQLException {
        ArrayList<Course> courses = new ArrayList<>();
        ArrayList<Integer> check = getUserCourses(name);
        connection = connectToDb();
        String sql = "SELECT * FROM course AS c WHERE c.course_is = " + courseIS;
        statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            int courseID = rs.getInt(1);
            if(check.contains(courseID))
                courses.add(new Course(rs.getInt(1), rs.getString(2), LocalDate.parse(rs.getString(3)), LocalDate.parse(rs.getString(4)), rs.getInt(5), rs.getDouble(6), rs.getInt(7)));
        }
        disconnectFromDb(connection, statement);

        return courses;
    }

    public static ArrayList<Course> getAvailableCourses(String name, int courseIS) throws SQLException {
        ArrayList<Course> courses = new ArrayList<>();
        ArrayList<Integer> check = getUserCourses(name);
        connection = connectToDb();
        String sql = "SELECT * FROM course AS c WHERE c.course_is = " + courseIS;
        statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            int courseID = rs.getInt(1);
            if(!(check.contains(courseID)))
                courses.add(new Course(rs.getInt(1), rs.getString(2), LocalDate.parse(rs.getString(3)), LocalDate.parse(rs.getString(4)), rs.getInt(5), rs.getDouble(6), rs.getInt(7)));
        }
        disconnectFromDb(connection, statement);

        return courses;
    }



    //  USERS  |
    //         V



    public static ArrayList<Administrator> getAllAdmins(int courseIs) throws SQLException {
        ArrayList<Administrator> allAdmins = new ArrayList<>();
        connection = connectToDb();
        String sql = "SELECT * FROM `users` AS c WHERE c.course_is = ? AND c.phone_number is not NULL";
        statement = connection.prepareStatement(sql);
        statement.setInt(1, courseIs);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            allAdmins.add(new Administrator(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
        }
        disconnectFromDb(connection, statement);
        return allAdmins;
    }

    public static ArrayList<Student> getAllStudents(int courseIs) throws SQLException {
        ArrayList<Student> allStudents = new ArrayList<>();
        connection = connectToDb();
        String sql = "SELECT * FROM `users` AS u WHERE u.course_is = ? AND u.phone_number is NULL";
        statement = connection.prepareStatement(sql);
        statement.setInt(1, courseIs);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            allStudents.add(new Student(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)));
        }
        disconnectFromDb(connection, statement);
        return allStudents;
    }

    public static User getUserByName(String login) throws SQLException {
        User user = null;
        connection = connectToDb();
        String sql = "SELECT * FROM users AS u WHERE u.login = ?";
        statement = connection.prepareStatement(sql);
        statement.setString(1, login);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            user = new User(rs.getString(2), rs.getString(3), rs.getString(4));
        }
        disconnectFromDb(connection, statement);
        return user;
    }

    public static Administrator getAdminAcc(String login, String psw, int courseIs) throws SQLException {
        Administrator Admin = null;
        connection = connectToDb();
        String sql = "SELECT * FROM users AS u WHERE u.login = ? AND u.psw = ? AND u.course_is = ? AND u.is_admin = ?";
        statement = connection.prepareStatement(sql);
        statement.setString(1, login);
        statement.setString(2, psw);
        statement.setInt(3, courseIs);
        statement.setBoolean(4, true);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            Admin = new Administrator(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
        }
        disconnectFromDb(connection, statement);
        return Admin;
    }

    public static Student getStudentAcc(String login, String psw, int courseIs) throws SQLException {
        Student stud = null;
        connection = connectToDb();
        String sql = "SELECT * FROM users AS u WHERE u.login = ? AND u.psw = ? AND u.course_is = ? AND u.is_admin = ?";
        statement = connection.prepareStatement(sql);
        statement.setString(1, login);
        statement.setString(2, psw);
        statement.setInt(3, courseIs);
        statement.setBoolean(4, false);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            stud = new Student(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
        }
        disconnectFromDb(connection, statement);
        return stud;
    }

    public static void insertAdmin(String login, String password, String email, String phoneNr, int courseIs) throws SQLException {
        connection = connectToDb();
        String sql = "INSERT INTO `users`(`login`, `psw`, `email`, `phone_number`, `course_is`, `is_admin`) VALUES(?,?,?,?,?,?)";
        statement = connection.prepareStatement(sql);
        statement.setString(1, login);
        statement.setString(2, password);
        statement.setString(3, email);
        statement.setString(4, phoneNr);
        statement.setInt(5, courseIs);
        statement.setBoolean(5, true);
        statement.execute();
        disconnectFromDb(connection, statement);
    }

    public static void insertStudent(String login, String password, String email, int courseIs) throws SQLException {
        connection = connectToDb();
        String sql = "INSERT INTO `users`(`login`, `psw`, `email`, `course_is`, `is_admin`) VALUES(?,?,?,?,?)";
        statement = connection.prepareStatement(sql);
        statement.setString(1, login);
        statement.setString(2, password);
        statement.setString(3, email);
        statement.setInt(4, courseIs);
        statement.setBoolean(5, false);
        statement.execute();
        disconnectFromDb(connection, statement);
    }

    public static void updateStudent(String oldLogin, String newLogin,String email) throws SQLException {
        Date date = new java.sql.Date(new java.util.Date().getTime());
        connection = connectToDb();
        String sql = "UPDATE `users` SET `login` = ?, `email` = ?, `date_modified` = ? WHERE `login` = ? AND `is_admin` = ?";
        statement = connection.prepareStatement(sql);
        statement.setString(1, newLogin);
        statement.setString(2, email);
        statement.setDate(3, date);
        statement.setString(4, oldLogin);
        statement.setBoolean(5, false);
        statement.executeUpdate();
        disconnectFromDb(connection, statement);
    }

    public static void updateAdmin(String oldLogin, String newLogin, String email, String phoneNr) throws SQLException {
        Date date = new java.sql.Date(new java.util.Date().getTime());
        connection = connectToDb();
        String sql = "UPDATE `users` SET `login` = ?, `email` = ?, `date_modified` = ?, `phone_number` = ? WHERE `login` = ? AND `is_admin` = ?";
        statement = connection.prepareStatement(sql);
        statement.setString(1, newLogin);
        statement.setString(2, email);
        statement.setDate(3, date);
        statement.setString(4, phoneNr);
        statement.setString(5, oldLogin);
        statement.setBoolean(6, true);
        statement.executeUpdate();
        disconnectFromDb(connection, statement);
    }

    public static void updateUserPsw(String name, String psw) throws SQLException {
        connection = connectToDb();
        String sql = "UPDATE `users` SET `psw` = ? WHERE `login` = ?";
        statement = connection.prepareStatement(sql);
        statement.setString(1, psw);
        statement.setString(2, name);
        statement.executeUpdate();
        disconnectFromDb(connection, statement);
    }

    public static void deleteUser(String name) throws SQLException {
        connection = connectToDb();
        String sql = "DELETE FROM `users` WHERE login = ?";
        statement = connection.prepareStatement(sql);
        statement.setString(1, name);
        statement.execute();
        disconnectFromDb(connection, statement);
    }

    public static String getAdminPhoneNumber(String name) throws SQLException {
        connection = connectToDb();
        String sql = "SELECT * FROM users WHERE users.login = " + "'" + name + "'";
        statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery(sql);
        String phoneNr = null;
        while (rs.next()) {
            phoneNr = rs.getString(5);
        }
        DbOperations.disconnectFromDb(connection, statement);
        return phoneNr;
    }

    public static String getPassword(String name) throws SQLException {
        connection = connectToDb();
        String sql = "SELECT * FROM users WHERE users.login = " + "'" + name + "'";
        statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery(sql);
        String psw = null;
        while (rs.next()) {
            psw = rs.getString(3);
        }
        DbOperations.disconnectFromDb(connection, statement);
        return psw;
    }

    public static int getUserID(String name) throws SQLException {
        connection = connectToDb();
        String sql = "SELECT * FROM users WHERE users.login = " + "'" + name + "'";
        statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery(sql);
        int id=0;
        while (rs.next()) {
            id = rs.getInt(1);
        }
        DbOperations.disconnectFromDb(connection, statement);
        return id;
    }


    //  FOLDERS  |
    //           V



    public static ArrayList<Folder> getCourseFolders(String name) throws SQLException {
        ArrayList<Folder> folders =  new ArrayList<>();
        int id = getCourseID(name);
        connection = connectToDb();
        String sql = "SELECT * FROM folder WHERE folder.course_id = " + id;
        statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            folders.add(new Folder(rs.getString(2)));
        }
        disconnectFromDb(connection, statement);
        return folders;
    }

    public static void createCourseFolder(String folderName, String courseName) throws SQLException {
        int id = getCourseID(courseName);
        if(!(doesFolderExist(folderName, id))) {
            connection = connectToDb();
            String sql = "INSERT INTO `folder` (`folder_name`, `course_id`) VALUES (?,?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, folderName);
            statement.setInt(2, id);

            statement.execute();
            disconnectFromDb(connection, statement);
        }
    }

    public static boolean doesFolderExist(String name, int id) throws SQLException {
        boolean exist = false;
        connection = connectToDb();
        String sql = "SELECT * FROM folder AS f WHERE f.folder_name = ? AND f.course_id = ?";
        statement = connection.prepareStatement(sql);
        statement.setString(1, name);
        statement.setInt(2, id);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            exist = true;
        }
        disconnectFromDb(connection, statement);


        return exist;
    }

    public static void deleteCourseFolder(String folderName, String courseName) throws SQLException {
        int id = getCourseID(courseName);
        connection = connectToDb();
        String sql = "DELETE FROM `folder` WHERE `course_id` = ? AND `folder_name` = ?";
        statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.setString(2, folderName);
        statement.execute();
        disconnectFromDb(connection, statement);

        deleteFile(getFolderId(folderName));
    }

    public static int getFolderId(String name) throws SQLException {
        connection = connectToDb();
        String sql = "SELECT * FROM folder WHERE folder.folder_name = " + "'" + name + "'";
        statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery(sql);
        int id=0;
        while (rs.next()) {
            id = rs.getInt(1);
        }
        disconnectFromDb(connection, statement);
        return id;
    }




    //  FILES  |
    //         V



    public static ArrayList<File> getAllFolderFiles(String folderName) throws SQLException {
        ArrayList<File> files =  new ArrayList<>();
        int id = getFolderId(folderName);
        connection = connectToDb();
        String sql = "SELECT * FROM folder_files WHERE folder_files.folder_id = " + id;
        statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery(sql);
        while (rs.next()) {
            files.add(new File(rs.getString(2)));
        }
        disconnectFromDb(connection, statement);
        return files;
    }

    public static void createFolderFile(String folderName, String fileName) throws SQLException {
        java.util.Date date = java.sql.Date.valueOf(java.time.LocalDate.now());
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());

        int id = getFolderId(folderName);
        connection = connectToDb();
        String sql = "INSERT INTO `folder_files` (`name`, `date_added`, `folder_id`) VALUES (?,?,?)";
        statement = connection.prepareStatement(sql);
        PreparedStatement insert = connection.prepareStatement(sql);
        insert.setString(1, fileName);
        insert.setDate(2, sqlDate);
        insert.setInt(3, id);

        insert.execute();
        disconnectFromDb(connection, statement);
    }

    public static void deleteFolderFile(String folderName, String fileName) throws SQLException {
        int id = getFolderId(folderName);
        connection = connectToDb();
        String sql = "DELETE FROM `folder_files` WHERE `name` = ? AND `folder_id` = ?";
        statement = connection.prepareStatement(sql);
        statement.setString(1, fileName);
        statement.setInt(2, id);
        statement.execute();
        disconnectFromDb(connection, statement);
    }


}
