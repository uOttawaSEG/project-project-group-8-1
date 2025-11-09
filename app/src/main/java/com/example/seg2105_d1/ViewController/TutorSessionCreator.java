package com.example.seg2105_d1.ViewController;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.seg2105_d1.Model.Availability;
import com.example.seg2105_d1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TutorSessionCreator extends AppCompatActivity {

    private CalendarView calendarView;
    private Spinner spinnerStart, spinnerEnd;
    private CheckBox checkManualApproval;
    private Button btnAdd;
    private ListView listAvailabilities;

    private Date selectedDate;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> availabilityList = new ArrayList<>();
    private ArrayList<String> availabilityIds = new ArrayList<>();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    private FirebaseFirestore db;
    private String tutorId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tutor_session_creator);

        calendarView = findViewById(R.id.calendarView);
        spinnerStart = findViewById(R.id.spinnerStart);
        spinnerEnd = findViewById(R.id.spinnerEnd);
        checkManualApproval = findViewById(R.id.checkManualApproval);
        btnAdd = findViewById(R.id.btnAddAvailability);
        listAvailabilities = findViewById(R.id.listAvailabilities);

        // Get tutor ID (from firebase)
        SharedPreferences preferences = getSharedPreferences("userPref", MODE_PRIVATE);
        tutorId = preferences.getString("userID", null);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, availabilityList);
        listAvailabilities.setAdapter(adapter);

        setupTimeSpinners();
        setupCalendar();
        setupAddButton();
        setupListClick();
        setupManualAvailability();
        loadAvailabilities();
    }

    private void setupManualAvailability(){
        checkManualApproval.setOnCheckedChangeListener((buttonView, isChecked) -> {
            db.collection("tutors")
                    .document(tutorId)
                    .update("manualApproval", isChecked)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Manual approval updated.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error updating manual approval.", Toast.LENGTH_SHORT).show();
                    });
        });

    }

    private void setupCalendar(){
        calendarView.setMinDate(System.currentTimeMillis()-1000);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth, 0, 0 , 0);
            selectedDate = cal.getTime();
        });
        selectedDate = new Date();
    }

    private void setupTimeSpinners(){
        //establishing the set of times for the spinner
        List<String> times = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        //initial conditions
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);

        //iterate through list of times in the day and add to spinner
        for(int i =0; i<48; i++){
            times.add(timeFormat.format(cal.getTime()));
            cal.add(Calendar.MINUTE, 30);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, times);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void setupAddButton(){
        btnAdd.setOnClickListener(v -> {
            String start = spinnerStart.getSelectedItem().toString();
            String end = spinnerEnd.getSelectedItem().toString();

            Availability availability = new Availability();
            availability.setDate(dateFormat.format(selectedDate));  // yyyy-MM-dd
            availability.setStartTime(start);
            availability.setEndTime(end);
            availability.setTutor(tutorId);

            if (!availability.timeOrderValid()) {
                Toast.makeText(this, "End time must be after start time.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!availability.timingValid()) {
                Toast.makeText(this, "Cannot add availability in the past.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Split into 30-minute increments
            LocalTime startTime = availability.getStartTime();
            LocalTime endTime = availability.getEndTime();
            LocalDate date = availability.getDate();

            List<Availability> slots = new ArrayList<>();
            while (startTime.isBefore(endTime)) {
                LocalTime slotEnd = startTime.plusMinutes(30);
                if (slotEnd.isAfter(endTime)) break;

                Availability slot = new Availability();
                slot.setDate(date.toString());
                slot.setStartTime(startTime.toString());
                slot.setEndTime(slotEnd.toString());
                slot.setTutor(availability.getTutor());

                slots.add(slot);
                startTime = slotEnd;
            }

            // Push all slots to Firestore
            WriteBatch batch = db.batch();

            for (Availability slot : slots) {
                DocumentReference docRef = db.collection("availabilities").document();
                Map<String, Object> data = new HashMap<>();
                data.put("date", slot.getDate().toString());
                data.put("startTime", slot.getStartTime().toString());
                data.put("endTime", slot.getEndTime().toString());
                data.put("tutorId", tutorId);
                batch.set(docRef, data);
            }

            batch.commit()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Availability added (" + slots.size() + " slots).", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error saving availability.", Toast.LENGTH_SHORT).show();
                    });
        });

    }

    private void setupListClick(){
        listAvailabilities.setOnItemClickListener(((parent, view, position, id) -> {
            String availabilityID = availabilityIds.get(position);

            new AlertDialog.Builder(this)
                    .setTitle("Delete Availability")
                    .setMessage("Are you sure you want to delete this 30-minute slot?")
                    .setPositiveButton("Delete", (dialog, which) ->{
                        db.collection("availabilities")
                                .document(availabilityID)
                                .delete()
                                .addOnSuccessListener(aVoid ->{
                                    Toast.makeText(this, "Slot Deleted.", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->{
                                    Toast.makeText(this, "Error deleting slot.", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }));
    }

    private void loadAvailabilities(){
        db.collection("availabilities")
                .whereEqualTo("tutorId", tutorId)
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener( snap -> {

                    availabilityList.clear();
                    availabilityIds.clear();

                    if (snap != null) {
                        List<DocumentSnapshot> documentSnapshots = snap.getDocuments();
                        for (DocumentSnapshot doc : documentSnapshots) {
                            Availability availability = new Availability();
                            availability.setDate(doc.getString("date"));
                            availability.setStartTime(doc.getString("startTime"));
                            availability.setEndTime(doc.getString("endTime"));

                            availabilityIds.add(doc.getId());
                            String formatted = availability.getDate() + " " +
                                    availability.getStartTime() + " - " +
                                    availability.getEndTime();

                            availabilityList.add(formatted);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });

    }


}