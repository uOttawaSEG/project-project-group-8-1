package com.example.seg2105_d1.ViewController;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seg2105_d1.Model.Availability;
import com.example.seg2105_d1.Model.Session;
import com.example.seg2105_d1.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StudentSearchPage extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText editTextCourseCode;
    private Button buttonSearchSessions;
    private RecyclerView recyclerViewSearchResults;
    private String studentId;
    private SearchResultsAdapter adapter;
    private List<Availability> resultsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_search_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        SharedPreferences preferences = getSharedPreferences("userPref", MODE_PRIVATE);
        studentId = preferences.getString("userID", null);

        editTextCourseCode = findViewById(R.id.editTextCourseCode);
        buttonSearchSessions = findViewById(R.id.buttonSearchSessions);
        recyclerViewSearchResults = findViewById(R.id.recyclerViewSearchResults);

        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SearchResultsAdapter(resultsList, availability -> {
            handleRequest(availability);
        });

        recyclerViewSearchResults.setAdapter(adapter);

        buttonSearchSessions.setOnClickListener(v -> performSearch());
    }

    //Search db for Availabilities with matching course
    private void performSearch() {
        String courseCode = editTextCourseCode.getText().toString().trim();

        if (courseCode.isEmpty()) {
            Toast.makeText(this, "Please enter a course code", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference ref = db.collection("availabilities");

        ref.whereEqualTo("course", courseCode).get().addOnSuccessListener(snapshot -> {

            resultsList.clear();

            for(DocumentSnapshot document : snapshot.getDocuments()) {
                Availability slot = document.toObject(Availability.class);
                if(slot == null){
                    continue;
                }

                resultsList.add(slot);
            }

            adapter.notifyDataSetChanged();

            if (resultsList.isEmpty()) {
                Toast.makeText(this, "No available sessions found", Toast.LENGTH_SHORT).show();
            }

            }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to load sessions: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
    }


    //Needs tutor email implemented? Cant get email from availability
    //Need to get user email
    private void handleRequest(Availability slot) {

        SharedPreferences preferences = getSharedPreferences("userPref", MODE_PRIVATE);
        studentId = preferences.getString("userID", null);

        // Create new session aka the request
        Session session = new Session();

        session.setStudentId(studentId);
        //session.setStudentEmail(studentId);
        session.setTutorId(slot.getTutor());
        //session.setTutorEmail(slot.getTutorEmail());
        session.setCourse(slot.getCourse());
        session.setStatus("PENDING");
        session.setStudentEmail(preferences.getString("email", null));

        session.setDate(slot.getDate().toString());
        session.setStartTime(slot.getStartTime().toString());
        session.setEndTime(slot.getEndTime().toString());

        //add session request to db
        // TODO: How to access slot(availability) ID from db to mark as USED?
        db.collection("sessions")
                .add(session)
                .addOnSuccessListener(docRef -> {
                    db.collection("availabilities")
                            .document(slot.getId())
                            .update("used", true)
                            .addOnSuccessListener(v -> Toast.makeText(this,
                                    "Session request sent!",
                                    Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Request failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }

    private static class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

        private List<Availability> items;
        private OnRequestClickListener listener;

        public interface OnRequestClickListener {
            void onRequestClick(Availability availability);
        }

        public SearchResultsAdapter(List<Availability> items, OnRequestClickListener listener) {
            this.listener = listener;
            this.items = items;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Availability slot = items.get(position);

            // Date and time formatters
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

            String formattedDate = slot.getDate().format(dateFormatter);

            String formattedTimeRange = slot.getStartTime().format(timeFormatter) + " - " + slot.getEndTime().format(timeFormatter);


            holder.tutorName.setText("Tutor: " + slot.getTutor());
            holder.date.setText("Date: " + formattedDate);
            holder.time.setText("Time: " + formattedTimeRange);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(slot.getTutor())
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            Double rating = document.getDouble("rating");
                            if (rating != null) {
                                holder.tutorRating.setText("Rating: " + String.format("%.1f", rating));
                            } else {
                                holder.tutorRating.setText("Rating: N/A");
                            }
                        } else {
                            holder.tutorRating.setText("Rating: N/A");
                        }
                    })
                    .addOnFailureListener(e -> holder.tutorRating.setText("Rating: N/A"));

            holder.requestButton.setOnClickListener(v -> {
                if (listener != null)
                    listener.onRequestClick(slot);
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tutorName, tutorRating, date, time;
            Button requestButton;

            ViewHolder(@NonNull View itemView) {
                super(itemView);

                tutorName = itemView.findViewById(R.id.textTutorName);
                tutorRating = itemView.findViewById(R.id.textTutorRating);
                date = itemView.findViewById(R.id.textSessionDate);
                time = itemView.findViewById(R.id.textSessionTime);
                requestButton = itemView.findViewById(R.id.buttonRequestSession);
            }
        }
    }

}