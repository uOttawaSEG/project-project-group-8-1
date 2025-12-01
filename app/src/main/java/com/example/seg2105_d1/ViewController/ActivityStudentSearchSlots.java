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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ActivityStudentSearchSlots extends AppCompatActivity {

    private EditText etCourseCode;
    private Button btnSearch;
    private RecyclerView recyclerViewSlots;

    private FirebaseFirestore db;
    private String studentEmail;

    private final List<Availability> slotList = new ArrayList<>();
    private final List<String> availabilityIds = new ArrayList<>();

    private String studentId;
    private SearchSlotsAdapter adapter;

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatSpinner = DateTimeFormatter.ofPattern("hh:mm a"); //12hour timeformat
    private final DateTimeFormatter timeFormatDb = DateTimeFormatter.ofPattern("HH:mm");//database 24hour timeformat

    private String searchedCourse = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_search_slots);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etCourseCode = findViewById(R.id.etCourseCode);
        btnSearch = findViewById(R.id.btnSearch);
        recyclerViewSlots = findViewById(R.id.recyclerViewSlots);

        recyclerViewSlots.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        SharedPreferences pref = getSharedPreferences("userPref", MODE_PRIVATE);
        studentId = pref.getString("userID", null);
        studentEmail = pref.getString("userEmail", null);

        btnSearch.setOnClickListener(v -> {
            searchedCourse = etCourseCode.getText().toString().trim();
            searchTutors();
        });
    }

    private void searchTutors() {
        String courseCode = etCourseCode.getText().toString().trim();

        if (courseCode.isEmpty()) {
            Toast.makeText(this, "Enter course code", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .whereEqualTo("role", "TUTOR")
                .whereArrayContains("courseOffered", courseCode)
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<String> tutorIds = new ArrayList<>();

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        tutorIds.add(doc.getId());
                    }

                    if (tutorIds.isEmpty()) {
                        Toast.makeText(this, "No tutors offer this course.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    loadAvailabilities(tutorIds);

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadAvailabilities(List<String> tutorIds) {

        db.collection("availabilities")
                .whereIn("tutorId", tutorIds)
                .whereEqualTo("isBooked", false)
                .get()
                .addOnSuccessListener(snapshot -> {

                    slotList.clear();
                    availabilityIds.clear();

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Availability slot = doc.toObject(Availability.class);
                        if (slot == null) continue;

                        slotList.add(slot);
                        availabilityIds.add(doc.getId());
                    }

                    adapter = new SearchSlotsAdapter(slotList, availabilityIds, searchedCourse, this::requestSession);
                    recyclerViewSlots.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    if (slotList.isEmpty()) {
                        Toast.makeText(this, "No available slots.", Toast.LENGTH_SHORT).show();
                    }

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading slots: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void requestSession(Availability slot, String availabilityId) {
        checkForConflictAndCreateSession(slot, availabilityId);
    }

    private void createSessionAndRemoveSlot(Availability slot, String availabilityId) {

        SharedPreferences pref = getSharedPreferences("userPref", MODE_PRIVATE);

        String studentEmail = pref.getString("userEmail", null);
        String course = etCourseCode.getText().toString().trim();

        db.collection("users")
                .document(slot.getTutorId())
                .get()
                .addOnSuccessListener(tutorDoc -> {

                    boolean autoApprove = !tutorDoc.getBoolean("manualApproval");

                    Session session = new Session();

                    session.setTutorId(slot.getTutorId());
                    session.setStudentEmail(studentEmail);
                    session.setCourse(course);

                    session.setStatus(autoApprove ? "APPROVED" : "PENDING");

                    session.setDate(slot.getDate().format(dateFormat));
                    session.setStartTime(timeFormatDb.format(slot.getStartTime()));
                    session.setEndTime(timeFormatDb.format(slot.getStartTime()));

                    List<String> slotList = new ArrayList<>();
                    slotList.add(availabilityId);
                    session.setAvailabilitySlotIds(slotList);

                    db.collection("sessions")
                            .add(session)
                            .addOnSuccessListener(ref -> {

                                // Step 2: mark availability booked
                                db.collection("availabilities")
                                        .document(availabilityId)
                                        .update("isBooked", true);

                                Toast.makeText(this,
                                        autoApprove ? "Session booked" : "Request sent to tutor",
                                        Toast.LENGTH_SHORT).show();

                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this,
                                            "Failed to create session: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show());
                });
    }

    private static class SearchSlotsAdapter extends RecyclerView.Adapter<SearchSlotsAdapter.ViewHolder> {

        private final List<Availability> items;
        private final List<String> ids;
        private final OnRequestListener listener;

        private final String courseCode;

        interface OnRequestListener {
            void onRequest(Availability slot, String availabilityId);
        }

        SearchSlotsAdapter(List<Availability> items,
                           List<String> ids,
                           String courseCode,
                           OnRequestListener listener) {
            this.items = items;
            this.ids = ids;
            this.courseCode = courseCode;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_available_slot, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            Availability slot = items.get(position);

            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");

            holder.tvTutorName.setText("Tutor: " + slot.getTutorName());
            holder.tvCourse.setText("Course: " + courseCode);
            holder.tvDateTime.setText(
                    slot.getDate().format(df) + " | " +
                            slot.getStartTime().format(tf) + "–" +
                            slot.getEndTime().format(tf)
            );

            holder.btnRequest.setOnClickListener(v ->
                    listener.onRequest(slot, ids.get(position)));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvTutorName, tvCourse, tvDateTime;
            Button btnRequest;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTutorName = itemView.findViewById(R.id.tvTutorName);
                tvCourse = itemView.findViewById(R.id.tvCourse);
                tvDateTime = itemView.findViewById(R.id.tvDateTime);
                btnRequest = itemView.findViewById(R.id.btnRequest);
            }
        }
    }

    private void checkForConflictAndCreateSession(Availability slot, String availabilityId) {

        db.collection("sessions")
                .whereEqualTo("studentEmail", studentEmail)
                .get()
                .addOnSuccessListener(snapshot -> {

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Session s = doc.toObject(Session.class);
                        if (s == null) continue;

                        if (!s.getStatus().equals("REJECTED")) {
                            if (isConflict(s, slot)) {
                                Toast.makeText(this,
                                        "You already have a session at this time.",
                                        Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }

                    createSessionAndRemoveSlot(slot, availabilityId);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error checking conflicts: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private boolean isConflict(Session existing, Availability newSlot) {

        if (!existing.getDate().equals(newSlot.getDate())) {
            return false;
        }

        return existing.getStartTime().isBefore(newSlot.getEndTime())
                && existing.getEndTime().isAfter(newSlot.getStartTime());
    }

}