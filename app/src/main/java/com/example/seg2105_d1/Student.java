package com.example.seg2105_d1;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Student extends User{
    private String programOfStudy;
    private DatabaseReference mDatabase;

    public Student(String programOfStudy, String firstName, String lastName, String emailAddressUsername, String accountPassword, String phoneNumber) {
        super(firstName, lastName, emailAddressUsername, accountPassword, phoneNumber);
        this.programOfStudy = programOfStudy;
    }

    public String getProgramOfStudy() { return this.programOfStudy; }

    public void setProgramOfStudy(String programOfStudy) { this.programOfStudy = programOfStudy; }

    @Override
    public void register(User u, RegisterCallback r) {
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        mDatabase.child(u.getEmailAddressUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    r.onFailure("Email already registered.");
                } else {
                    mDatabase.child(u.getEmailAddressUsername()).setValue(u)
                            .addOnSuccessListener(aVoid -> r.onSuccess())
                            .addOnFailureListener(e -> r.onFailure(e.getMessage()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                r.onFailure(error.getMessage());
            }
        });
    }
}
