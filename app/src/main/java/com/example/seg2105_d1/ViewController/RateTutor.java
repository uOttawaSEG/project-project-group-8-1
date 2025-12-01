package com.example.seg2105_d1.ViewController;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.seg2105_d1.Model.Session;
import com.example.seg2105_d1.Model.Tutor;
import com.example.seg2105_d1.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class RateTutor extends AppCompatActivity {
    private TextView tvTutorName;
    private RatingBar ratingBar;
    private Button ratebtn;
    private FirebaseFirestore db;
    private String tutorEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rate_tutor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        tvTutorName = findViewById(R.id.tvTutorName);
        ratingBar = findViewById(R.id.ratingBar);
        ratebtn = findViewById(R.id.ratebtn);

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

            ratebtn.setOnClickListener(v -> {
                float rating = ratingBar.getRating();
                Tutor tutor = doc.toObject(Tutor.class);

                tutor.updateRating(rating);

                doc.getReference().update(
                                "ratingSum", tutor.getRatingSum(),
                                "numRates", tutor.getNumRates(),
                                "rating", tutor.getRating())
                        .addOnSuccessListener(ref -> {
                            Toast.makeText(this,"Successfully rated " + name, Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this,"Unexpected error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });

        }).addOnFailureListener(e -> {
            Toast.makeText(this,
                    "Failed to load student info: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        });
    }
}

