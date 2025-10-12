package com.example.seg2105_d1.Model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Student extends User {
    private String programOfStudy;

    public Student() {
        super();
        this.programOfStudy = null;
    }
    public Student(String programOfStudy, String firstName, String lastName, String emailAddressUsername, String accountPassword, String phoneNumber) {
        super(firstName, lastName, emailAddressUsername, accountPassword, phoneNumber);
        this.programOfStudy = programOfStudy;
    }

    public String getProgramOfStudy() { return this.programOfStudy; }

    public void setProgramOfStudy(String programOfStudy) {
        this.programOfStudy = programOfStudy;
    }
}
