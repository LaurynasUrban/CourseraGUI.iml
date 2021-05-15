package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Student extends User implements Serializable {

  private String accNumber;
  private ArrayList<Course> myEnrolledCourses = new ArrayList<>();

  public Student() {}

  public Student(String login, String psw, String email, String accNumber) {
    super(login, psw, email);
    this.accNumber = accNumber;
  }

  public String getAccNumber() {
    return accNumber;
  }

  public void setAccNumber(String accNumber) {
    this.accNumber = accNumber;
  }

  public ArrayList<Course> getMyEnrolledCourses() {
    return myEnrolledCourses;
  }

  public void setMyEnrolledCourses(ArrayList<Course> myEnrolledCourses) {
    this.myEnrolledCourses = myEnrolledCourses;
  }

}
