package webControllers;

import com.google.gson.Gson;
import fxControl.MainWindow;
import model.Course;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import utils.DbOperations;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Properties;

@Controller
@RequestMapping(value = "courses")
public class WebCourseController {

    // DbOperations RESTful API for courses

    @RequestMapping(value = "/getAllCourses", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getAllCourses(@RequestParam("courseIs") int courseIs){
        ArrayList<Course> allCourses = new ArrayList<Course>();
        Gson parser = new Gson();
        try{
            allCourses = DbOperations.getAllCourses(courseIs);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return parser.toJson(allCourses);
    }

    @RequestMapping(value = "/getAvailableCourses", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getAvailableCourses(@RequestBody String request){
        ArrayList<Course> allCourses = new ArrayList<Course>();
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String name = data.getProperty("name");
        int courseIS = Integer.parseInt(data.getProperty("courseIS"));
        try{
            allCourses = DbOperations.getAvailableCourses(name, courseIS);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return parser.toJson(allCourses);
    }

    @RequestMapping(value = "/getStudentcourses", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getStudentCourses(@RequestBody String request){
        ArrayList<Course> allCourses = new ArrayList<Course>();
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String name = data.getProperty("name");
        int courseIS = Integer.parseInt(data.getProperty("courseIS"));
        try{
            allCourses = DbOperations.getStudentCourses(name, courseIS);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return parser.toJson(allCourses);
    }

    @RequestMapping(value = "/getCoursesByAdmin", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getCoursesByAdmin(@RequestParam("adminName") String adminName){
        ArrayList<Course> allCourses = new ArrayList<Course>();
        Gson parser = new Gson();
        try{
            allCourses = DbOperations.getCoursesByAdmin(adminName);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return parser.toJson(allCourses);
    }

    @RequestMapping(value = "/viewCourseInfo", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String viewCourseInfo(@RequestParam("courseName") String courseName) {
        Gson parser = new Gson();
        String[] info = new String[3];
        try {
            info = DbOperations.courseInfo(courseName);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return parser.toJson(info);
    }

    @RequestMapping(value = "/enrollToCourse", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String enrollToCourse(@RequestBody String request){
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        int id1 = Integer.parseInt(data.getProperty("id1"));
        int id2 = Integer.parseInt(data.getProperty("id2"));

        try {
            DbOperations.enrollToCourse(id1, id2);
            return "You have succesfully enrolled in a course";
        } catch (Exception e) {
            return "There were errors during insert operation";
        }
    }

    @RequestMapping(value = "/leaveCourse", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String leaveCourse(@RequestBody String request){
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        int id1 = Integer.parseInt(data.getProperty("id1"));
        int id2 = Integer.parseInt(data.getProperty("id2"));
        try {
            DbOperations.leaveCourse(id1, id2);
            return "You have left the course";
        } catch (Exception e) {
            return "There were errors during insert operation";
        }
    }

    @RequestMapping(value = "/insertCourse", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String insertCourse(@RequestBody String request){
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String name = data.getProperty("name");
        LocalDate startDate = LocalDate.parse(data.getProperty("start"));
        LocalDate endDate = LocalDate.parse(data.getProperty("end"));
        int adminId = Integer.parseInt(data.getProperty("adminId"));
        Double price = Double.parseDouble(data.getProperty("price"));
        int courseIs = Integer.parseInt(data.getProperty("courseIs"));
        Course newCourse = new Course(name, startDate, endDate, adminId, price, courseIs);

        try {
            DbOperations.insertCourse(newCourse);
            return parser.toJson(DbOperations.getCourseByName(name));
        } catch (Exception e) {
            return "There were errors during insert operation";
        }
    }

    @RequestMapping(value = "/deleteCourse", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String deleteCourse(@RequestParam("name") String name){
        try{
            DbOperations.deleteCourse(name);
            return "Course deleted";
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "Failed to delete a course";
    }


    @RequestMapping(value = "/updateCourse", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String updateCourse(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        int courseId = Integer.parseInt(data.getProperty("id"));
        String name = data.getProperty("name");
        Double price = Double.parseDouble(data.getProperty("price"));
        try {
            DbOperations.updateCourse(courseId, "name", name);
            if (!data.getProperty("start").equals(""))
                DbOperations.updateCourse(courseId, "start_date", LocalDate.parse(data.getProperty("start")));
            if (!data.getProperty("end").equals(""))
                DbOperations.updateCourse(courseId, "end_date", LocalDate.parse(data.getProperty("end")));
            DbOperations.updateCourse(courseId, "course_price", price);
            return parser.toJson(DbOperations.getCourseByName(name));
        } catch (Exception e) {
            return "There were errors during insert operation";
        }
    }
}
