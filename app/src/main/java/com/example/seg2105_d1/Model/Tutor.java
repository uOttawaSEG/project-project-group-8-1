package com.example.seg2105_d1.Model;

import java.util.ArrayList;

public class Tutor extends User {

    private String highestDegree;
    private ArrayList<String> coursesOffered;

    public Tutor() {
        super();
        this.highestDegree = null;
        this.coursesOffered = new ArrayList<String>();
    }

    public Tutor(String firstName, String lastName, String emailAddressUsername, String accountPassword, String phoneNumber, String highestDegree, ArrayList<String> coursesOffered) {
        super(firstName, lastName, emailAddressUsername, accountPassword, phoneNumber);
        this.highestDegree = highestDegree;
        this.coursesOffered = coursesOffered;
    }

    public String getHighestDegree() {
        return this.highestDegree;
    }

    public void setHighestDegree(String highestDegree){
        this.highestDegree = highestDegree;
    }

    public ArrayList<String> getCoursesOffered(){
        return this.coursesOffered;
    }

    public void addCourses(String newCourse){
        coursesOffered.add(newCourse);
    }


}
