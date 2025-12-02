package com.example.seg2105_d1.ViewController;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.google.type.DateTime;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    private LocalDate selectedDate;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> availabilityList = new ArrayList<>();
    private ArrayList<ArrayList<String>> availabilityIds = new ArrayList<>();

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter twelveHourTimeFormat = DateTimeFormatter.ofPattern("hh:mm a");

    private final DateTimeFormatter DBTimeFormat = DateTimeFormatter.ofPattern("HH:mm");

    private FirebaseFirestore db;
    private String tutorId;

    private String tutorName;
    private boolean manualApproval;


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

        db = FirebaseFirestore.getInstance();

        // Get tutor ID (from firebase)
        SharedPreferences preferences = getSharedPreferences("userPref", MODE_PRIVATE);
        tutorId = preferences.getString("userID", null);
        tutorName = preferences.getString("userName", null);

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
        db.collection("users").document(tutorId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get a field value by key
                        manualApproval = Boolean.TRUE.equals(documentSnapshot.get("manualApproval"));
                        checkManualApproval.setChecked(manualApproval);
                        Log.d("Firestore", "Manual Approval: " + manualApproval);
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error getting document", e));
        checkManualApproval.setOnCheckedChangeListener((buttonView, isChecked) -> {
            db.collection("users")
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
        selectedDate = LocalDate.now();
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = LocalDate.of(year, month+1, dayOfMonth);
        });

    }

    private void setupTimeSpinners(){
        //establishing the set of times for the spinner
        List<String> times = new ArrayList<>();
        LocalTime time = LocalTime.of(0, 0);

        //iterate through list of times in the day and add to spinner
        for(int i =0; i<48; i++){
            times.add(time.format(twelveHourTimeFormat));
            time = time.plusMinutes(30);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, times);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStart.setAdapter(adapter);
        spinnerEnd.setAdapter(adapter);
    }

    private void setupAddButton(){
        btnAdd.setOnClickListener(v -> {
            String start = spinnerStart.getSelectedItem().toString();
            String end = spinnerEnd.getSelectedItem().toString();

            LocalTime localStartTime = LocalTime.parse(start,twelveHourTimeFormat);
            LocalTime localEndTime = LocalTime.parse(end,twelveHourTimeFormat);

            String startTimeDB = localStartTime.format(DBTimeFormat);
            String endTimeDB = localEndTime.format(DBTimeFormat);

            Availability availability = new Availability();
            availability.setDate(dateFormat.format(selectedDate));  // yyyy-MM-dd
            availability.setStartTime(startTimeDB);
            availability.setEndTime(endTimeDB);
            availability.setTutorId(tutorId);
            availability.setTutorName(tutorName);

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

                Availability slot = new Availability();
                slot.setDate(date.toString());
                slot.setStartTime(startTime.format(DBTimeFormat));
                slot.setEndTime(slotEnd.format(DBTimeFormat));
                slot.setTutorId(availability.getTutorId());
                slot.setTutorName(availability.getTutorName());

                slots.add(slot);
                startTime = slotEnd;
            }

            db.collection("availabilities")
                    .whereEqualTo("tutorId", tutorId)
                    .orderBy("date", Query.Direction.ASCENDING)
                    .orderBy("startTime", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener(snap -> {
                        if (snap != null && !snap.isEmpty()) {
                            for (QueryDocumentSnapshot doc : snap) {
                                LocalDate tmpDate = LocalDate.parse(doc.getString("date"));
                                LocalTime tmpStart = LocalTime.parse(doc.getString("startTime"));
                                LocalTime tmpEnd = LocalTime.parse(doc.getString("endTime"));

                                if (date == null || start == null || end == null) continue;

                                if(Availability.Overlap(date, tmpDate, availability.getStartTime(), endTime, tmpStart, tmpEnd)) {
                                    Toast.makeText(this, "Overlapping availability.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
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
                            data.put("tutorName",tutorName);
                            data.put("isBooked",false);
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
        });
    }

    private void setupListClick(){
        listAvailabilities.setOnItemClickListener(((parent, view, position, id) -> {
            ArrayList<String> availabilityID = availabilityIds.get(position);

            new AlertDialog.Builder(this)
                    .setTitle("Delete Availability")
                    .setMessage("Are you sure you want to delete this slot?")
                    .setPositiveButton("Delete", (dialog, which) ->{
                        WriteBatch batch = db.batch();

                        for (String slotId : availabilityID) {
                            //implement check for if availability is linked to a session
                            //availabilities either need a used attribute
                            // or the sessions collection needs to be iterated through to find slotId
                            batch.delete(db.collection("availabilities").document(slotId));
                        }

                        batch.commit()
                                .addOnSuccessListener(aVoid ->{
                                    Toast.makeText(this, "Availability slot deleted.", Toast.LENGTH_SHORT).show();
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
                .orderBy("startTime", Query.Direction.ASCENDING)
                .addSnapshotListener((snap, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error loading data.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    availabilityList.clear();
                    availabilityIds.clear();

                    if (snap != null && !snap.isEmpty()) {
                        //instantiates helper variables for merging
                        String currentDate = null;
                        String currentStart = null;
                        String currentEnd = null;
                        ArrayList<String> currentIds = new ArrayList<>();

                        for (QueryDocumentSnapshot doc : snap) {
                            String date = doc.getString("date");
                            String start = doc.getString("startTime");
                            String end = doc.getString("endTime");
                            LocalDate checkPast = LocalDate.parse(date);

                            if (date == null || start == null || end == null) continue;

                            if (!checkPast.isBefore(LocalDate.now())) {
                                if (currentDate == null) {
                                    currentDate = date;
                                    currentStart = start;
                                    currentEnd = end;
                                    //adds slots to currentIds
                                    currentIds.add(doc.getId());
                                } else if (currentDate.equals(date) && currentEnd.equals(start)) {
                                    //merge continuous slots
                                    currentEnd = end;
                                    //adds slots to currentIds
                                    currentIds.add(doc.getId());
                                } else {
                                    //adds merged slot to the list
                                    String formatted = currentDate + " " + currentStart + " - " + currentEnd;
                                    availabilityList.add(formatted);
                                    //adds the list of slots that is within the merged slot
                                    availabilityIds.add(new ArrayList<>(currentIds));

                                    //resets variables
                                    currentDate = date;
                                    currentStart = start;
                                    currentEnd = end;
                                    currentIds.clear();
                                    currentIds.add(doc.getId());
                                }
                            }
                        }

                        //adds last merged item (if non-empty)
                        if (currentDate != null) {
                            //adds merged slot to the list
                            String formatted = currentDate + " " + currentStart + " - " + currentEnd;
                            availabilityList.add(formatted);
                            //adds the list of slots that is within the merged slot
                            availabilityIds.add(new ArrayList<>(currentIds));
                        }
                    }

                    adapter.notifyDataSetChanged();
                });

    }


}