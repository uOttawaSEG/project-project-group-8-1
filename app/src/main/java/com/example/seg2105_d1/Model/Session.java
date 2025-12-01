package com.example.seg2105_d1.Model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Session {
    //instance variables-------------------------------------------------
    String id;
    List<String> availabilitySlotIds;
    String course;
    String studentEmail;
    String status;

    String tutorId;

    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate date;

    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //constructors-------------------------------------------------------
    public Session() {

    }
     //getters and setters-----------------------------------------------
    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }
    public List<String> getAvailabilitySlotIds(){
        return availabilitySlotIds;
    }

    public void setAvailabilitySlotIds(List<String> availabilitySlotIds) {
        this.availabilitySlotIds = availabilitySlotIds;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public LocalTime getStartTime(){
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = LocalTime.parse(startTime, timeFormat);;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = LocalTime.parse(endTime, timeFormat);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = LocalDate.parse(date, dateFormat);
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getTutorId(){
        return tutorId;
    }

    public void setTutorId(String tutorId){
        this.tutorId = tutorId;
    }


    //helper methods-------------------------------------------------------



}
