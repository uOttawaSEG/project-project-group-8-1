package com.example.seg2105_d1.Model;

import java.util.List;

public class Session {

    private String id;
    private String tutorEmail;
    private String studentEmail;

    private String date;         // 整个 session 的日期
    private String startTime;    // 整个 session 的开始时间
    private String endTime;      // 整个 session 的结束时间

    private String status;       // PENDING / APPROVED / REJECTED / CANCELLED

    // 新增：这个 session 占用了哪些 30min availability slots
    private List<String> availabilitySlotIds;

    public Session() {}

    public Session(String tutorEmail,
                   String studentEmail,
                   String date,
                   String startTime,
                   String endTime,
                   String status,
                   List<String> availabilitySlotIds) {
        this.tutorEmail = tutorEmail;
        this.studentEmail = studentEmail;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.availabilitySlotIds = availabilitySlotIds;
    }

    // getters & setters...

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTutorEmail() { return tutorEmail; }
    public void setTutorEmail(String tutorEmail) { this.tutorEmail = tutorEmail; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getAvailabilitySlotIds() { return availabilitySlotIds; }
    public void setAvailabilitySlotIds(List<String> availabilitySlotIds) {
        this.availabilitySlotIds = availabilitySlotIds;
    }
}
