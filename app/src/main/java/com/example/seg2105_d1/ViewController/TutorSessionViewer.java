package com.example.seg2105_d1.ViewController;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TutorSessionViewer extends AppCompatActivity {

    private enum Mode{
        PENDING,
        UPCOMING,
        PAST
    }

    private FirebaseFirestore db;
    private String tutorEmail;

    private Button btnPending, btnUpcoming, btnPast;
    private RecyclerView recyclerViewSessions;

    private final List<Session> sessionList = new ArrayList<>();
    private SessionAdapter adapter;

    private Mode currentMode = Mode.PENDING;

    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tutor_session_viewer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        tutorEmail = getIntent().getStringExtra("tutorEmail");//change it later!!!!!!!

        btnPending = findViewById(R.id.btnPending);
        btnUpcoming = findViewById(R.id.btnUpcoming);
        btnPast = findViewById(R.id.btnPast);
        recyclerViewSessions = findViewById(R.id.recyclerSessions);

        recyclerViewSessions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SessionAdapter(sessionList);
        recyclerViewSessions.setAdapter(adapter);

        if(tutorEmail == null || tutorEmail.isEmpty()){
            Toast.makeText(this, "Missing tutor email.", Toast.LENGTH_LONG).show();
        }

        btnPending.setOnClickListener(v -> {
            currentMode = Mode.PENDING;
            loadSessionsFromDatabase();
        });

        btnPending.setOnClickListener(v -> {
            currentMode = Mode.PENDING;
            loadSessionsFromDatabase();
        });

        btnPending.setOnClickListener(v -> {
            currentMode = Mode.PENDING;
            loadSessionsFromDatabase();
        });

        loadSessionsFromDatabase();

    }

    private void loadSessionsFromDatabase(){

        if (tutorEmail == null || tutorEmail.isEmpty()) {
            return;
        }

        Query query = db.collection("sessions").whereEqualTo("totorEmail", tutorEmail);

        query.get().addOnSuccessListener(snapshot -> {

            sessionList.clear();
            Date now = new Date();

            for(DocumentSnapshot document : snapshot.getDocuments()){
                Session session = document.toObject(Session.class);
                if(session == null){
                    continue;
                }

                session.setId(document.getId());
                String status = session.getStatus();
                boolean isInTheFuture = isInTheFuture(session, now);

                switch (currentMode){
                    case PENDING:
                        if("PENDING".equals(status)){
                            sessionList.add(session);
                        }
                        break;
                    case UPCOMING:
                        if("APPROVED".equals(status) && isInTheFuture){
                            sessionList.add(session);
                        }
                        break;
                    case PAST:
                        if("APPROVED".equals(status) && !isInTheFuture){
                            sessionList.add(session);
                        }
                        break;
                }
            }

            adapter.notifyDataSetChanged();//refresh whole list


            if(sessionList.isEmpty()){
                Toast.makeText(this, "No sessions in this category.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load sessions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }

    private boolean isInTheFuture(Session s, Date now) {
        try {
            Date when = dateTimeFormat.parse(s.getDate() + " " + s.getStartTime());//make sure the format is the same using
            return when != null && when.after(now);
        } catch (ParseException e) {
            return false;
        }
    }


    private void updateStatusAndRefresh(Session session, String newStatus, boolean freeAvailability){

        if (session.getId() == null || session.getId().isEmpty()) {
            Toast.makeText(this, "Session ID for update is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference sessionReference = db.collection("sessions").document(session.getId());
        sessionReference.update("status", newStatus).addOnSuccessListener(notused -> {
            if(freeAvailability && session.getAvailabilitySlotIds() != null){
                for(String slotId : session.getAvailabilitySlotIds()){
                    if(slotId == null || slotId.isEmpty()){
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

    private class SessionAdapter extends RecyclerView.Adapter<SessionViewHolder>{

        private final List<Session> data;

        public SessionAdapter(List<Session> data){
            this.data = data;
        }

        @NonNull
        @Override
        public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_session, parent, false);
            return new SessionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
            Session session = data.get(position);

            String message = session.getDate() + " " + session.getStartTime() + " " + session.getEndTime();
            holder.tvInfo.setText(message);

            holder.tvStudent.setText("Student Email: " + session.getStudentEmail());
            holder.tvStatus.setText("Status: " + session.getStatus());

            holder.btnApprove.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);

            if(currentMode == Mode.PENDING){
                holder.btnApprove.setVisibility(View.VISIBLE);
                holder.btnReject.setVisibility(View.VISIBLE);

                holder.btnApprove.setOnClickListener(v -> {
                    updateStatusAndRefresh(session, "APPROVED", false);
                });

                holder.btnReject.setOnClickListener(v -> {
                    updateStatusAndRefresh(session, "REJECTED", true);
                });
            }else if(currentMode == Mode.UPCOMING){
                holder.btnCancel.setVisibility(View.VISIBLE);

                holder.btnCancel.setOnClickListener(v -> {
                    updateStatusAndRefresh(session, "CANCELLED", true);
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