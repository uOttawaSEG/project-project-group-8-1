package com.example.seg2105_d1.ViewController;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.seg2105_d1.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class TutorSessionCreator extends AppCompatActivity {

    private FirebaseFirestore db;
    private DocumentReference tutorRef;

    private String selectedDate = "";
    private String startTime, endTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tutor_session_creator);

        CalendarView calendarView = findViewById(R.id.selectDateCalendar);
        Spinner startTime = findViewById(R.id.spinnerStartTime);
        Spinner endTime = findViewById(R.id.spinnerEndTime);
        CheckBox cbManualApproval = findViewById(R.id.cbManualApproval);
        Button addAvailabilitySlot = findViewById(R.id.btnAddSlot);
        TextView yourAvailability = findViewById(R.id.tvYourAvailability);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = dayOfMonth + "/" + (month+1) + "/" + year;
                Toast.makeText(TutorSessionCreator.this, "Selected: " + selectedDate, Toast.LENGTH_SHORT).show();
            }
        });

        addAvailabilitySlot.setOnClickListener(v -> {

        });

    }


}