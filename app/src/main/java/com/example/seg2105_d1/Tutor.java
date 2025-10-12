package com.example.seg2105_d1;

import java.util.ArrayList;

public class Tutor extends User{

    private String highestDegree;
    private ArrayList<String> coursesOffered;

    public Tutor() {
        super();
        this.highestDegree = null;
        this.coursesOffered = new ArrayList<String>();
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

    //temp structure for register method
    @Override
    public void register(User u, RegisterCallback r) {

    }

}
