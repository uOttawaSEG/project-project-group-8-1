package com.example.seg2105_d1.ViewController;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.seg2105_d1.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class StudentInfo extends AppCompatActivity {

    private TextView tvStudentName, tvStudentEmail, tvStudentProgram, tvStudentPhone;

    private FirebaseFirestore db;
    private String studentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        tvStudentName = findViewById(R.id.tvStudentName);
        tvStudentEmail = findViewById(R.id.tvStudentEmail);
        tvStudentProgram = findViewById(R.id.tvStudentProgram);
        tvStudentPhone = findViewById(R.id.tvStudentPhone);

        Intent intent = getIntent();
        studentEmail = intent.getStringExtra("studentEmail");

        if (studentEmail == null || studentEmail.isEmpty()) {
            Toast.makeText(this, "No student email provided.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvStudentEmail.setText(studentEmail);

        loadStudentInfo();
    }

    private void loadStudentInfo() {
        Query query = db.collection("users")
                .whereEqualTo("emailAddressUsername", studentEmail)
                .whereEqualTo("role", "STUDENT")
                .limit(1);
        query.get().addOnSuccessListener(snapshot -> {
            if (snapshot.isEmpty()) {
                Toast.makeText(this, "No student record found.", Toast.LENGTH_SHORT).show();
                return;
            }

            DocumentSnapshot doc = snapshot.getDocuments().get(0);

            String firstName = doc.getString("firstName");
            String lastName = doc.getString("lastName");
            String name = (firstName + " " + lastName).trim();

            String email = doc.getString("emailAddressUsername");
            String program = doc.getString("programOfStudy");
            String phone = doc.getString("phoneNumber");

            tvStudentName.setText(name);
            tvStudentEmail.setText(email);
            tvStudentProgram.setText(program.isEmpty() ? "unknown" : program);
            tvStudentPhone.setText(phone.isEmpty() ? "unknown" : phone);

        }).addOnFailureListener(e -> {
            Toast.makeText(this,
                    "Failed to load student info: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        });
    }
}

