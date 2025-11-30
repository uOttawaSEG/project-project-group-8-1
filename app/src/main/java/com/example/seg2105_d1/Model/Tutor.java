package com.example.seg2105_d1.Model;

import java.util.ArrayList;

public class Tutor extends User {

    private String highestDegree;
    private ArrayList<String> coursesOffered;
    private boolean manualApproval;

    private float ratingSum;

    private int numRates;

    private double rating;

    public Tutor() {
        super();
        this.highestDegree = null;
        this.coursesOffered = new ArrayList<String>();
        this.manualApproval = false;
        this.ratingSum=0;
        this.numRates=0;
        this.rating=0;
    }

    public Tutor(String firstName, String lastName, String emailAddressUsername, String accountPassword, String phoneNumber, String highestDegree, ArrayList<String> coursesOffered, String registrationStatus) {
        super(firstName, lastName, emailAddressUsername, accountPassword, phoneNumber, registrationStatus);
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

    //updates tutor rating based on new rating
    public void updateRating(float rating) {
        this.ratingSum+=rating;
        this.numRates++;
        this.rating = (double) ratingSum/numRates;
    }
    public float getRatingSum(){return this.ratingSum;}
    public int getNumRates(){return this.numRates;}
    public double getRating() {
        return this.rating;
    }

}
