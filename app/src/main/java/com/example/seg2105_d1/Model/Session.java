package com.example.seg2105_d1.Model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Session {
    //instance variables ------------------------------------------------------------
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate date;

    private Tutor tutor;
    private Student student;

    //constructors ------------------------------------------------------------------
    public Session() {

    }

    //getters and setters -----------------------------------------------------------
    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = LocalTime.parse(startTime);;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = LocalTime.parse(endTime);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = LocalDate.parse(date);
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor)  {
        this.tutor = tutor;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent() {
        this.student = student;
    }

    //helper methods ------------------------------------------------------------------

    /**
     * Checks if sessions are overlapping. Used for verification of new session creation.
     * @param sessionA should be an existing session
     * @param sessionB should be a new session to create
     * @return true if sessions don't overlap, false otherwise
     */
    public static boolean noOverlap(Session sessionA, Session sessionB) {
        LocalDate sessionADate = sessionA.getDate();
        LocalDate sessionBDate = sessionB.getDate();
        LocalTime sessionAStartTime = sessionA.getStartTime();
        LocalTime sessionAEndTime = sessionA.getEndTime();
        LocalTime sessionBStartTime = sessionB.getStartTime();
        LocalTime sessionBEndTime = sessionB.getEndTime();

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
        if(date.isAfter(LocalDate.now()) || date.equals(LocalDate.now())) {
            if(startTime.isAfter(LocalTime.now())) {
                return true;
            }
        }
        return false;
    }

}
