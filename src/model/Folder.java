package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Folder implements Serializable {

  private int id;
  private String name;
  private ArrayList<File> folderFiles;

  public Folder(String name, ArrayList<File> folderFiles) {
    this.name = name;
    this.folderFiles = folderFiles;
  }

  public Folder(String name) {
    this.name = name;
  }


  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ArrayList<File> getFolderFiles() {
    return folderFiles;
  }

  public void setFolderFiles(ArrayList<File> folderFiles) {
    this.folderFiles = folderFiles;
  }
}
