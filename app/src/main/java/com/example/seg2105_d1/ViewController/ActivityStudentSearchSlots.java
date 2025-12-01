package com.example.seg2105_d1.ViewController;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ActivityStudentSearchSlots extends AppCompatActivity {

    private EditText etCourseCode;
    private Button btnSearch;
    private RecyclerView recyclerView;

    private FirebaseFirestore db;
    private String studentEmail;

    private final List<Availability> slotList = new ArrayList<>();
    private SlotAdapter adapter;

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("hh:mm a");

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
        recyclerView = findViewById(R.id.recyclerViewSlots);

        db = FirebaseFirestore.getInstance();
        studentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();//change it later for getting the email

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SlotAdapter(slotList);
        recyclerView.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> searchSlots());
    }

    private void searchSlots() {
        String course = etCourseCode.getText().toString().trim();
        if (course.isEmpty()) {
            Toast.makeText(this, "Enter course code.", Toast.LENGTH_SHORT).show();
            return;
        }

        Query query = db.collection("availabilities")
                .whereEqualTo("course", course)
                .whereEqualTo("isBooked", false);

        query.get().addOnSuccessListener(snapshot -> {
            slotList.clear();

            snapshot.getDocuments().forEach(doc -> {
                Availability slot = doc.toObject(Availability.class);
                if (slot != null) {
                    slot.setId(doc.getId());
                    slotList.add(slot);
                }
            });

            adapter.notifyDataSetChanged();

            if (slotList.isEmpty()) {
                Toast.makeText(this, "No available slots found.", Toast.LENGTH_SHORT).show();
            }

        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private class SlotAdapter extends RecyclerView.Adapter<SlotViewHolder> {

        private final List<Availability> data;

        SlotAdapter(List<Availability> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_available_slot, parent, false);
            return new SlotViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
            Availability slot = data.get(position);

            holder.tvTutorName.setText(slot.getTutorName());
            holder.tvCourse.setText(slot.getCourse());
            holder.tvDateTime.setText(slot.getDate() + " | " + slot.getStartTime() + "-" + slot.getEndTime());

            holder.btnRequest.setOnClickListener(v -> requestSession(slot));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView tvTutorName, tvCourse, tvDateTime;
        Button btnRequest;

        SlotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTutorName = itemView.findViewById(R.id.tvTutorName);
            tvCourse = itemView.findViewById(R.id.tvCourse);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            btnRequest = itemView.findViewById(R.id.btnRequest);
        }
    }

    private void requestSession(Availability slot) {

        Session s = new Session();
        s.setTutorId(slot.getTutorId());
        s.setStudentEmail(studentEmail);
        s.setDate(dateFormat.format(slot.getDate()));
        s.setStartTime(timeFormat.format(slot.getStartTime()));
        s.setEndTime(timeFormat.format(slot.getEndTime()));
        s.setCourse(slot.getCourse());
        s.setStatus("PENDING");
        s.setAvailabilitySlotIds(List.of(slot.getId()));

        db.collection("sessions")
                .add(s)
                .addOnSuccessListener(ref -> {
                    // mark slot as booked
                    db.collection("availabilities").document(slot.getId())
                            .update("isBooked", true);

                    slotList.remove(slot); // remove from list
                    adapter.notifyDataSetChanged();

                    Toast.makeText(this, "Session requested!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}