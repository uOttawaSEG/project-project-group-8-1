package com.example.seg2105_d1.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Availability {
    //instance variables ------------------------------------------------------------
    private String id;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate date;

    private String tutorId;

    private String tutorName;


    private boolean isBooked;

    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //constructors ------------------------------------------------------------------
    public Availability() {

    }

    //getters and setters -----------------------------------------------------------
    public String getId(){ return this.id; }

    public void setId(String id){
        this.id = id;
    }

    public boolean getIsBooked(){ return this.isBooked;}
    public void setIsBooked(boolean isBooked){ this.isBooked = isBooked;}
    public LocalTime getStartTime() {
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

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId)  {
        this.tutorId = tutorId;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName)  {
        this.tutorName = tutorName;
    }


    //helper methods ------------------------------------------------------------------

    /**
     * Checks if sessions are overlapping. Used for verification of new session creation.
     * @param sessionADate Session A date.
     * @param sessionBDate Session B date.
     * @param sessionAStartTime Session A start time.
     * @param sessionAEndTime Session A end time.
     * @param sessionBStartTime Session B start time.
     * @param sessionBEndTime Session B end time.
     * @return true if sessions overlap, false otherwise
     */
    public static boolean Overlap(LocalDate sessionADate, LocalDate sessionBDate, LocalTime sessionAStartTime, LocalTime sessionAEndTime, LocalTime sessionBStartTime, LocalTime sessionBEndTime) {
        //different dates so no overlap
        if (!sessionADate.equals(sessionBDate)) {
            return false;
        }

        //Overlap exists if:
        //sessionAStartTime < sessionBEndTime AND sessionAEndTime > sessionBStartTime
        return (sessionAStartTime.isBefore(sessionBEndTime) && sessionAEndTime.isAfter(sessionBStartTime));
    }

    /**
     * Checks if start time is before end time. Used for authenticating a new session.
     * @return true if start time is before end time, false otherwise.
     */
    public boolean timeOrderValid() {
        if(startTime.isBefore(endTime)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if date and time are after the current date and time.
     * @return true if date and time is after current actual date and time, false otherwise.
     */
    public boolean timingValid() {
        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        return startDateTime.isAfter(LocalDateTime.now());
    }

}
