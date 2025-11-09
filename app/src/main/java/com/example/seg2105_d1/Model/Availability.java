package com.example.seg2105_d1.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Availability {
    //instance variables ------------------------------------------------------------
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate date;

    private String tutorId;

    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    //constructors ------------------------------------------------------------------
    public Availability() {

    }

    //getters and setters -----------------------------------------------------------
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

    public String getTutor() {
        return tutorId;
    }

    public void setTutor(String tutorId)  {
        this.tutorId = tutorId;
    }

    //helper methods ------------------------------------------------------------------

    /**
     * Checks if sessions are overlapping. Used for verification of new session creation.
     * @param availabilityA should be an existing session
     * @param availabilityB should be a new session to create
     * @return true if sessions don't overlap, false otherwise
     */
    public static boolean noOverlap(Availability availabilityA, Availability availabilityB) {
        LocalDate sessionADate = availabilityA.getDate();
        LocalDate sessionBDate = availabilityB.getDate();
        LocalTime sessionAStartTime = availabilityA.getStartTime();
        LocalTime sessionAEndTime = availabilityA.getEndTime();
        LocalTime sessionBStartTime = availabilityB.getStartTime();
        LocalTime sessionBEndTime = availabilityB.getEndTime();

        if(sessionADate.equals(sessionBDate)) {
            if(sessionBStartTime.isBefore(sessionAEndTime)) {
                return false;
            }
        }
        return true;

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
