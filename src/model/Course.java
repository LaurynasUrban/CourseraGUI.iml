package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class Course implements Serializable {

    private int id;
    private int courseIs;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private int adminId;
    private ArrayList<Student> enrolledUsers;
    private ArrayList<Folder> folders;
    private double coursePrice;

    public Course(
            String name,
            LocalDate startDate,
            LocalDate endDate,
            int adminId,
            ArrayList<Student> enrolledUsers,
            ArrayList<Folder> folders,
            double coursePrice) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.adminId = adminId;
        this.enrolledUsers = enrolledUsers;
        this.folders = folders;
        this.coursePrice = coursePrice;
    }

    public Course(String name, LocalDate startDate, LocalDate endDate, int adminId, double coursePrice, int courseIs) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.coursePrice = coursePrice;
        this.adminId = adminId;
        this.courseIs = courseIs;
    }

    public Course(int id, String name, LocalDate startDate, LocalDate endDate, int administrator, double coursePrice, int courseIs) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.adminId = administrator;
        this.coursePrice = coursePrice;
        this.courseIs = courseIs;
    }

    public Course(String name, double coursePrice) {
        this.name = name;
        this.coursePrice = coursePrice;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int administrator) {
        this.adminId = administrator;
    }

    public ArrayList<Student> getEnrolledUsers() {
        return enrolledUsers;
    }

    public void setEnrolledUsers(ArrayList<Student> enrolledUsers) {
        this.enrolledUsers = enrolledUsers;
    }

    public ArrayList<Folder> getFolders() {
        return folders;
    }

    public void setFolders(ArrayList<Folder> folders) {
        this.folders = folders;
    }

    public double getCoursePrice() {
        return coursePrice;
    }

    public void setCoursePrice(double coursePrice) {
        this.coursePrice = coursePrice;
    }

    public int getCourseIs() {
        return courseIs;
    }

    public void setCourseIs(int courseIs) {
        this.courseIs = courseIs;
    }

    @Override
    public String toString() {
        return name + ": (" + startDate + ") - " + "(" + endDate + ")";
    }
}
