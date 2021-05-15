package webControllers;

import com.google.gson.Gson;
import model.File;
import model.Folder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import utils.DbOperations;

import java.util.ArrayList;
import java.util.Properties;

@Controller
@RequestMapping(value = "/files")
public class WebFileController {

    @RequestMapping(value = "/getFolderFiles", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getFolderFiles(@RequestParam("folderName") String folderName){
        ArrayList<File> files = new ArrayList<>();
        Gson parser = new Gson();
        try{
            files = DbOperations.getAllFolderFiles(folderName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parser.toJson(files);
    }

    @RequestMapping(value = "/deleteFile", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String deleteFile(@RequestBody String request){
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String folderName = data.getProperty("folderName");
        String fileName = data.getProperty("fileName");

        try {
            DbOperations.deleteFolderFile(folderName, fileName);
            return "File was deleted";
        } catch (Exception e) {
            return "There were errors during insert operation";
        }
    }

    @RequestMapping(value = "/insertFile", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String insertFile(@RequestBody String request){
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String folderName = data.getProperty("folderName");
        String fileName = data.getProperty("fileName");

        try {
            DbOperations.createFolderFile(folderName, fileName);
            return "File was created";
        } catch (Exception e) {
            return "There were errors during insert operation";
        }
    }
}
