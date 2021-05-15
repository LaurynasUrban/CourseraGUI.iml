package webControllers;

import com.google.gson.Gson;
import model.Administrator;
import model.Course;
import model.Student;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import utils.DbOperations;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Properties;

@Controller
@RequestMapping(value = "/users")
public class WebUserController {

    @RequestMapping(value = "/getAllAdmins", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getAllAdmins(@RequestParam("courseIs") int courseIs){
        ArrayList<Administrator> allAdmins = new ArrayList<>();
        Gson parser = new Gson();
        try{
            allAdmins = DbOperations.getAllAdmins(courseIs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parser.toJson(allAdmins);
    }

    @RequestMapping(value = "/getAllStudents", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getAllCourses(@RequestParam("courseIs") int courseIs){
        ArrayList<Student> allStudents = new ArrayList<>();
        Gson parser = new Gson();
        try{
            allStudents = DbOperations.getAllStudents(courseIs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parser.toJson(allStudents);
    }

    @RequestMapping(value = "/getUser/{login}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getUser(@PathVariable("login") String name) {
        Gson parser = new Gson();

        try {
            return parser.toJson(DbOperations.getUserByName(name));
        } catch (Exception e) {
            e.printStackTrace();
            return "Error selecting";
        }
    }

    @RequestMapping(value = "/insertAdministrator", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String insertAdmin(@RequestBody String request){
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String name = data.getProperty("login");
        String password = data.getProperty("psw");
        String email = data.getProperty("email");
        String phoneNr = data.getProperty("phone");
        int courseIs = Integer.parseInt(data.getProperty("courseIs"));

        try {
            DbOperations.insertAdmin(name, password, email, phoneNr, courseIs);
            return parser.toJson(DbOperations.getUserByName(name));
        } catch (Exception e) {
            return "There were errors during insert operation";
        }
    }

    @RequestMapping(value = "/insertStudent", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String insertStudent(@RequestBody String request){
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String name = data.getProperty("login");
        String password = data.getProperty("psw");
        String email = data.getProperty("email");
        int courseIs = Integer.parseInt(data.getProperty("courseIs"));

        try {
            DbOperations.insertStudent(name, password, email, courseIs);
            return parser.toJson(DbOperations.getUserByName(name));
        } catch (Exception e) {
            return "There were errors during insert operation";
        }
    }

    //Authorization
    @RequestMapping(value = "/adminLogin", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String loginAdmin(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String loginName = data.getProperty("login");
        String password = data.getProperty("psw");
        int courseIs = Integer.parseInt(data.getProperty("courseIs"));
        Administrator administrator;
        try {
            administrator = DbOperations.getAdminAcc(loginName, password, courseIs);
        } catch (Exception e) {
            return "Error";
        }
        if (administrator == null) {
            return "Wrong credentials";
        }
        return parser.toJson("Login succesfull, " + administrator.getLogin());
    }

    @RequestMapping(value = "/studentLogin", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String loginStudent(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String loginName = data.getProperty("login");
        String password = data.getProperty("psw");
        int courseIs = Integer.parseInt(data.getProperty("courseIs"));
        Student stud;
        try {
            stud = DbOperations.getStudentAcc(loginName, password, courseIs);
        } catch (Exception e) {
            return "Error";
        }
        if (stud == null) {
            return "Wrong credentials";
        }
        return parser.toJson("Login succesfull, " + stud.getLogin());
    }

    @RequestMapping(value = "/updateStudent", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String updateStudent(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String oldLogin = data.getProperty("oldLogin");
        String newLogin = data.getProperty("newLogin");
        String email = data.getProperty("email");
        try {
            DbOperations.updateStudent(oldLogin, newLogin, email);
            return parser.toJson(DbOperations.getUserByName(newLogin));
        } catch (Exception e) {
            return "There were errors during insert operation";
        }
    }

    @RequestMapping(value = "/updateAdmin", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String updateAdmin(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String oldLogin = data.getProperty("oldLogin");
        String newLogin = data.getProperty("newLogin");
        String email = data.getProperty("email");
        String phoneNr = data.getProperty("phNr");
        try {
            DbOperations.updateAdmin(oldLogin, newLogin, email, phoneNr);
            return parser.toJson(DbOperations.getUserByName(newLogin));
        } catch (Exception e) {
            return "There were errors during insert operation";
        }
    }

    @RequestMapping(value = "/updateUserPsw", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String updateUserPsw(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String name = data.getProperty("name");
        String psw = data.getProperty("psw");
        try {
            DbOperations.updateUserPsw(name, psw);
            return "Password changed";
        } catch (Exception e) {
            return "There were errors during insert operation";
        }
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String deleteCourse(@RequestParam("name") String name){
        try{
            DbOperations.deleteUser(name);
            return "User deleted";
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "Failed to delete a user";
    }

}
