package com.example.seg2105_d1.ViewController;

import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.seg2105_d1.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class UserDetail extends AppCompatActivity {

    public FirebaseFirestore db;

    public TextView tvName, tvEmail, tvNum, tvClasses;
    public Button btnApprove, btnReject;

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

        //userID from AdminList screen ***CHECK LOGIC
        //get id from database id?

        String userId = getIntent().getStringExtra("userId");

        if (userId != null) {
            loadUserById(userId);
        } else {
            Toast.makeText(this, "No user data here", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadUserById(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(this::populateUserDetails)
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading user", Toast.LENGTH_SHORT).show());
    }

    private void populateUserDetails (DocumentSnapshot doc) {
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

}