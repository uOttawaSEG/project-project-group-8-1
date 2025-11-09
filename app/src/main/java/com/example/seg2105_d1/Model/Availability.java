package com.example.seg2105_d1.Model;

public class Availability {

    private String id;              // Firestore 文档 ID（代码中手动 set，或从 snapshot 取）
    private String tutorEmail;
    private String date;            // 格式: "yyyy-MM-dd"
    private String startTime;       // 格式: "HH:mm"
    private String endTime;         // 格式: "HH:mm"
    private boolean manualApproval; // true = 需要手动审批, false = 自动批准
    private boolean booked;         // true = 已被预约 (Session 占用)

    public Availability() {
        // Firestore 反序列化需要
    }

    public Availability(String tutorEmail, String date,
                            String startTime, String endTime,
                            boolean manualApproval) {
        this.tutorEmail = tutorEmail;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.manualApproval = manualApproval;
        this.booked = false;
    }

    // ===== Getters & Setters =====

    public String getId() {
        return id;
    }

    // 注意：id 不会自动从 Firestore 带回来，需要你在读取 snapshot 时手动 set 一下（可选）
    public void setId(String id) {
        this.id = id;
    }

    public String getTutorEmail() {
        return tutorEmail;
    }

    public void setTutorEmail(String tutorEmail) {
        this.tutorEmail = tutorEmail;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isManualApproval() {
        return manualApproval;
    }

    public void setManualApproval(boolean manualApproval) {
        this.manualApproval = manualApproval;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

}
