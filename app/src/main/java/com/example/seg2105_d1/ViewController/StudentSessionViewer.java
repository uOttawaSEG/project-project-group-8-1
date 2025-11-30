package com.example.seg2105_d1.ViewController;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import com.example.seg2105_d1.Model.Session;
import com.example.seg2105_d1.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class StudentSessionViewer extends AppCompatActivity {


    private enum Mode{
        UPCOMING,
        PAST
    }

    private FirebaseFirestore db;
    private String studentId;

    private Button btnUpcoming, btnPast;
    private RecyclerView recyclerViewSessions;

    private final List<Session> sessionList = new ArrayList<>();
    private StudentSessionViewer.SessionAdapter adapter;

    private StudentSessionViewer.Mode currentMode = StudentSessionViewer.Mode.UPCOMING;

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_session_viewer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        SharedPreferences preferences = getSharedPreferences("userPref", MODE_PRIVATE);
        studentId = preferences.getString("userID", null);

        btnUpcoming = findViewById(R.id.btnUpcoming);
        btnPast = findViewById(R.id.btnPast);
        recyclerViewSessions = findViewById(R.id.recyclerSessions);

        recyclerViewSessions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentSessionViewer.SessionAdapter(sessionList);
        recyclerViewSessions.setAdapter(adapter);

        if(studentId == null || studentId.isEmpty()){
            Toast.makeText(this, "Missing student id.", Toast.LENGTH_LONG).show();
        }

        btnUpcoming.setOnClickListener(v -> {
            currentMode = StudentSessionViewer.Mode.UPCOMING;
            loadSessionsFromDatabase();
        });

        btnPast.setOnClickListener(v -> {
            currentMode = StudentSessionViewer.Mode.PAST;
            loadSessionsFromDatabase();
        });

        loadSessionsFromDatabase();
    }

    private void loadSessionsFromDatabase(){

        if (studentId == null || studentId.isEmpty()) {
            return;
        }

        Query query = db.collection("sessions")
                .whereEqualTo("studentId", studentId)
                .orderBy("status", Query.Direction.ASCENDING)
                .orderBy("date", Query.Direction.ASCENDING)
                .orderBy("startTime", Query.Direction.ASCENDING);

        query.get().addOnSuccessListener(snapshot -> {

            sessionList.clear();
            LocalDate now = LocalDate.now();

            for(DocumentSnapshot document : snapshot.getDocuments()){
                Session session = document.toObject(Session.class);
                if(session == null){
                    continue;
                }

                session.setId(document.getId());
                String status = session.getStatus();
                boolean isInTheFuture = isInTheFuture(session, now);

                switch (currentMode){
                    case UPCOMING:
                        if(isInTheFuture){
                            sessionList.add(session);
                        }
                        break;
                    case PAST:
                        if(!status.equals("REJECTED") && !status.equals("PENDING") && !isInTheFuture){
                            sessionList.add(session);
                        }
                        break;
                }
            }

            switch (currentMode) {
                case UPCOMING:
                    btnUpcoming.setAlpha(0.8f);
                    btnPast.setAlpha(1f);
                    break;
                case PAST:
                    btnUpcoming.setAlpha(1f);
                    btnPast.setAlpha(0.8f);
                    break;
            }

            adapter.notifyDataSetChanged();//refresh whole list


            if(sessionList.isEmpty()){
                Toast.makeText(this, "No sessions in this category.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load sessions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }

    private boolean isInTheFuture(Session s, LocalDate now) {
        try {
            LocalDate sessionDate = s.getDate();
            if (sessionDate.isAfter(now)) {
                return true;
            } else if (sessionDate.isEqual(now)) {
                return s.getStartTime().isAfter(LocalTime.now());
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }


    private void updateStatusAndRefresh(Session session, String newStatus, boolean freeAvailability){

        if (session.getId() == null || session.getId().isEmpty()) {
            Toast.makeText(this, "Session ID for update is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        LocalDateTime sessionDateTime = LocalDateTime.of(session.getDate(), session.getStartTime());
        LocalDateTime now = LocalDateTime.now();

        Duration diff = Duration.between(now, sessionDateTime);

        boolean isWithin24Hours = diff.toHours() <= 24;

        if (isWithin24Hours && newStatus.equals("CANCELLED")) {
            Toast.makeText(this,
                    "This session cannot be canceled as it is within the next 24 hours.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        DocumentReference sessionReference = db.collection("sessions").document(session.getId());
        sessionReference.update("status", newStatus).addOnSuccessListener(notused -> {
            if (freeAvailability && session.getAvailabilitySlotIds() != null) {
                for (String slotId : session.getAvailabilitySlotIds()) {
                    if (slotId == null || slotId.isEmpty()) {
                        continue;
                    }
                    db.collection("availabilities").document(slotId).update("used", false);
                }
            }

            String message;
            switch (newStatus) {
                case "APPROVED":
                    message = "Session approved.";
                    break;
                case "REJECTED":
                    message = "Session rejected.";
                    break;
                case "CANCELLED":
                    message = "Session cancelled.";
                    break;
                default:
                    message = "Updated.";
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            loadSessionsFromDatabase();

        }).addOnFailureListener(e -> {
            Toast.makeText(this,
                    "Failed to update: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void openDetail(String tutorEmail, boolean rating){
        if (tutorEmail == null || tutorEmail.isEmpty()) {
            Toast.makeText(this, "No tutor email for this session.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!rating) {
            Intent intent = new Intent(StudentSessionViewer.this, TutorInfo.class);
            intent.putExtra("tutorEmail", tutorEmail);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(StudentSessionViewer.this, RateTutor.class);
            intent.putExtra("tutorEmail", tutorEmail);
            startActivity(intent);
        }
    }

    private class SessionAdapter extends RecyclerView.Adapter<StudentSessionViewer.SessionViewHolder>{

        private final List<Session> data;

        public SessionAdapter(List<Session> data){
            this.data = data;
        }

        @NonNull
        @Override
        public StudentSessionViewer.SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_session, parent, false);
            return new StudentSessionViewer.SessionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StudentSessionViewer.SessionViewHolder holder, int position) {
            Session session = data.get(position);

            String dateStr = "";
            String startStr = "";
            String endStr = "";

            if (session.getDate() != null) {
                dateStr = session.getDate().format(dateFormatter); // yyyy-MM-dd
            }
            if (session.getStartTime() != null) {
                startStr = session.getStartTime().format(timeFormatter); // hh:mm a
            }
            if (session.getEndTime() != null) {
                endStr = session.getEndTime().format(timeFormatter); // hh:mm a
            }

            String message = dateStr + "  " + startStr + " - " + endStr;
            holder.tvInfo.setText(message.trim());

            holder.tvStudent.setText("Tutor Email: " + session.getTutorEmail());
            holder.tvStatus.setText("Status: " + session.getStatus());

            holder.btnApprove.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);

            if(currentMode == StudentSessionViewer.Mode.UPCOMING){

                if(Objects.equals(session.getStatus(), "APPROVED")) {
                    holder.btnCancel.setVisibility(View.VISIBLE);
                }

                holder.btnCancel.setOnClickListener(v -> {
                    updateStatusAndRefresh(session, "CANCELLED", true);
                });

                holder.itemView.setOnClickListener(v -> {
                    openDetail(session.getTutorEmail(), false);
                });
            }else{
                holder.itemView.setOnClickListener(v -> {
                    openDetail(session.getTutorEmail(), true);
                });
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class SessionViewHolder extends RecyclerView.ViewHolder{

        TextView tvInfo, tvStudent, tvStatus;
        Button btnApprove, btnReject, btnCancel;
        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInfo = itemView.findViewById(R.id.tvSessionInfo);
            tvStudent = itemView.findViewById(R.id.tvSessionStudent);
            tvStatus = itemView.findViewById(R.id.tvSessionStatus);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}