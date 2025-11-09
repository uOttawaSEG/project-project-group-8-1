package com.example.seg2105_d1.Model;

public class Session {
    //instance variables-------------------------------------------------
    Availability availability;
    String course;
    Student student;

    String status;

    //constructors-------------------------------------------------------
    public Session() {

    }
     //getters and setters-----------------------------------------------
    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStatus() {
        return status;
    }

    //possible statuses: PENDING, APPROVED REJECTED
    public void setStatus(String status) {
        this.status = status;
    }


    //helper methods-------------------------------------------------------



}
