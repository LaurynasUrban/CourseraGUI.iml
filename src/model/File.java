package model;

import java.io.Serializable;
import java.util.Date;

public class File implements Serializable {

  private int id;
  private String name;
  private Date dateAdded;

  public File(String name, Date dateAdded, String linkToFile) {
    this.name = name;
    this.dateAdded = dateAdded;
  }

  public File(String name) {
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

  public Date getDateAdded() {
    return dateAdded;
  }

  public void setDateAdded(Date dateAdded) {
    this.dateAdded = dateAdded;
  }
}
