package webControllers;

import com.google.gson.Gson;
import model.Administrator;
import model.Folder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import utils.DbOperations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

@Controller
@RequestMapping(value = "/folders")
public class WebFolderController {

    @RequestMapping(value = "/getCourseFolders", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getCourseFolders(@RequestParam("courseName") String courseName){
        ArrayList<Folder> folders = new ArrayList<>();
        Gson parser = new Gson();
        try{
            folders = DbOperations.getCourseFolders(courseName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parser.toJson(folders);
    }

    @RequestMapping(value = "/deleteFolder", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String deleteFolder(@RequestBody String request){
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String fName = data.getProperty("folderName");
        String cName = data.getProperty("courseName");

        try {
            DbOperations.deleteCourseFolder(fName, cName);
            return "Folder was deleted";
        } catch (Exception e) {
            return "There were errors during insert operation";
        }
    }

    @RequestMapping(value = "/insertFolder", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String insertFolder(@RequestBody String request){
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String fName = data.getProperty("folderName");
        String cName = data.getProperty("courseName");

        try {
            DbOperations.createCourseFolder(fName, cName);
            return "Folder was created";
        } catch (Exception e) {
            return "There were errors during insert operation";
        }
    }
}
