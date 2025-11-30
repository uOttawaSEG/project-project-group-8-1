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
import com.example.seg2105_d1.R;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StudentSearchPage extends AppCompatActivity {

    private EditText editTextCourseCode;
    private Button buttonSearchSessions;
    private RecyclerView recyclerViewSearchResults;
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

        editTextCourseCode = findViewById(R.id.editTextCourseCode);
        buttonSearchSessions = findViewById(R.id.buttonSearchSessions);
        recyclerViewSearchResults = findViewById(R.id.recyclerViewSearchResults);

        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SearchResultsAdapter(resultsList, availability -> {
            handleRequest(availability);
        });

        recyclerViewSearchResults.setAdapter(adapter);

        // 4. Search button logic
        buttonSearchSessions.setOnClickListener(v -> performSearch());
    }

    private void performSearch() {
        String course = editTextCourseCode.getText().toString().trim();

        if (course.isEmpty()) {
            Toast.makeText(this, "Please enter a course code", Toast.LENGTH_SHORT).show();
            return;
        }

        // Test search
        List<Availability> found = mockSearch(course);

       //update results
        resultsList.clear();
        resultsList.addAll(found);
        adapter.notifyDataSetChanged();
    }

    // TODO: implement actual db search
    private List<Availability> mockSearch(String course) {
        List<Availability> allslots = new ArrayList<>();

        //fake data for testing
        Availability av1 = new Availability();
        Availability av2 = new Availability();

        av1.setDate("2025-12-01");
        av1.setStartTime("10:00 AM");
        av1.setEndTime("11:00 PM");
        av1.setTutor("tutorid123");
        av1.setCourse("TUT123");

        av2.setDate("2025-12-02");
        av2.setStartTime("10:00 AM");
        av2.setEndTime("11:00 PM");
        av2.setTutor("tutorid9999");
        av2.setCourse("SEG123");

        allslots.add(av1);
        allslots.add(av2);

        List<Availability> filtered = new ArrayList<>();
        for (Availability slot : allslots) {
            if (slot.getCourse().equalsIgnoreCase(course)) {
                filtered.add(slot);
            }
        }

        return filtered;
    }

    private void handleRequest(Availability slot) {
        Toast.makeText(this, "Requested " + slot.getDate(), Toast.LENGTH_SHORT).show();

        // TODO: Add request logic here

        resultsList.remove(slot);
        adapter.notifyDataSetChanged();
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
            // TODO: holder.tutorRating.setText("Rating: " + slot.getTutorRating());  Not yet implemented ratings
            holder.date.setText("Date: " + formattedDate);
            holder.time.setText("Time: " + formattedTimeRange);

            holder.requestButton.setOnClickListener(v -> {
                if (listener != null)
                    listener.onRequestClick(slot);
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        //missing course info?
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