package com.example.seg2105_d1.ViewController;

import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.seg2105_d1.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class UserDetail extends AppCompatActivity {

    public FirebaseFirestore db;

    public TextView tvName, tvEmail, tvNum, tvClasses;
    public Button btnApprove, btnReject;
    private String userDocId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        //UI elements
        tvName = findViewById(R.id.tvUserDetailName);
        tvEmail = findViewById(R.id.tvUserDetailEmail);
        tvNum = findViewById(R.id.tvUserDetailNum);
        tvClasses = findViewById(R.id.tvUserDetailClasses);

        btnApprove = findViewById(R.id.btnApprove);
        btnReject = findViewById(R.id.btnReject);

        //emailaddress account from AdminList screen ***CHECK LOGIC

        String emailAddressUsername = getIntent().getStringExtra("EmailAddressUsername");

        loadUserByAccount(emailAddressUsername);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
                finish();
            }
        });

        btnApprove.setOnClickListener(v -> updateUserStatus("APPROVED"));
        btnReject.setOnClickListener(v -> updateUserStatus("REJECTED"));
    }

    private void loadUserByAccount(String emailAddressUsername) {
        db.collection("users")
                .whereEqualTo("emailAddressUsername", emailAddressUsername)
                .get()
                .addOnSuccessListener(this::populateUserDetails)
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading user", Toast.LENGTH_SHORT).show());
    }

    private void populateUserDetails (QuerySnapshot queryDocumentSnapshots) {

        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
        userDocId = doc.getId();

        String firstName = doc.getString("firstName");
        String lastName = doc.getString("lastName");
        String email = doc.getString("emailAddressUsername");
        String phone = doc.getString("phoneNumber");
        String role = doc.getString("role");

        String fullName = firstName + " " + lastName;

        tvName.setText(fullName);
        tvEmail.setText(email);
        tvNum.setText(phone);

        //only show courses when tutor
        if ("TUTOR".equalsIgnoreCase(role)) {
            List<String> courses = (List<String>) doc.get("courseOffered");
            if (courses != null && !courses.isEmpty()) {
                tvClasses.setVisibility(View.VISIBLE);
                tvClasses.setText("Courses: " + String.join(", ", courses));
            }
        } else {
            tvClasses.setVisibility(View.GONE);
        }
    }

    private void updateUserStatus(String updatedStatus){
        if (userDocId == null) {
            Toast.makeText(this, "User not loaded yet", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .document(userDocId)
                .update("registrationStatus", updatedStatus)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, "Successfully " + updatedStatus, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show());
    }
}