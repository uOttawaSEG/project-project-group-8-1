package com.example.seg2105_d1.ViewController;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
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

import java.util.List;

public class TutorInfo extends AppCompatActivity {
    private TextView tvTutorName, tvTutorEmail, tvHighestDegree, tvTutorPhone;
    private LinearLayout tvCoursesOffered;

    private FirebaseFirestore db;
    private String tutorEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tutor_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        tvTutorName = findViewById(R.id.tvTutorName);
        tvTutorEmail = findViewById(R.id.tvTutorEmail);
        tvHighestDegree = findViewById(R.id.tvHighestDegree);
        tvCoursesOffered = findViewById(R.id.tvCoursesOffered);
        tvTutorPhone = findViewById(R.id.tvTutorPhone);

        Intent intent = getIntent();
        tutorEmail = intent.getStringExtra("tutorEmail");

        if (tutorEmail == null || tutorEmail.isEmpty()) {
            Toast.makeText(this, "No tutor email provided.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadStudentInfo();
    }

    private void loadStudentInfo() {
        Query query = db.collection("users")
                .whereEqualTo("emailAddressUsername", tutorEmail)
                .whereEqualTo("role", "TUTOR")
                .limit(1);
        query.get().addOnSuccessListener(snapshot -> {
            if (snapshot.isEmpty()) {
                Toast.makeText(this, "No tutor record found.", Toast.LENGTH_SHORT).show();
                return;
            }

            DocumentSnapshot doc = snapshot.getDocuments().get(0);

            String firstName = doc.getString("firstName");
            String lastName = doc.getString("lastName");
            String name = (firstName + " " + lastName).trim();

            String email = doc.getString("emailAddressUsername");
            String degree = doc.getString("highestDegree");
            List<String> program = (List<String>) doc.get("coursesOffered");
            String phone = doc.getString("phoneNumber");

            tvTutorName.setText(name);
            tvTutorEmail.setText(email);
            tvHighestDegree.setText(degree);
            tvTutorPhone.setText(phone.isEmpty() ? "unknown" : phone);

            for(int i = 0; i < program.size(); i++) {
                TextView tv = new TextView(this);
                tv.setText(program.get(i));
                tv.setTextSize(16);
                tvCoursesOffered.addView(tv);
            }

        }).addOnFailureListener(e -> {
            Toast.makeText(this,
                    "Failed to load student info: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        });
    }
}
