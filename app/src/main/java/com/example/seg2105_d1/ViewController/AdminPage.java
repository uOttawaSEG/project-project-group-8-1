package com.example.seg2105_d1.ViewController;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seg2105_d1.Model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.example.seg2105_d1.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class AdminPage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<User, VH> adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    static class VH extends RecyclerView.ViewHolder{
        TextView tvFirstName, tvLastName, tvEmail, tvRole, tvPhoneNumber;

        Chip chipStatus;
        VH(View itemView){
            super(itemView);
            tvFirstName = itemView.findViewById(R.id.tvFirstName);
            tvLastName = itemView.findViewById(R.id.tvLastName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            chipStatus =itemView.findViewById(R.id.chipStatus);
        }
    }

    private void styleStatusChip(Chip chip, String status){
        String s = (status == null || status.isEmpty()) ? "PENDING" : status;
        if ("PENDING".equalsIgnoreCase(s)) {
            chip.setChipBackgroundColorResource(android.R.color.darker_gray);
        } else if ("REJECTED".equalsIgnoreCase(s)) {
            chip.setChipBackgroundColorResource(android.R.color.holo_red_light);
        } else if ("APPROVED".equalsIgnoreCase(s)) {
            chip.setChipBackgroundColorResource(android.R.color.holo_green_light);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_page);

        MaterialToolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("Registration Dashboard");

        recyclerView = findViewById(R.id.recyclerUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        Query query = db.collection("users")
//                        .whereIn("role", Arrays.asList("STUDENT", "TUTOR"))
//                                .orderBy("registrationStatus");
        Query query = db.collection("users");

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                        .build();

        adapter = new FirestoreRecyclerAdapter<User, VH>(options) {
            @Override
            protected void onBindViewHolder(@NonNull VH holder, int position, @NonNull User model) {

                holder.tvFirstName.setText(model.getFirstName());
                holder.tvLastName.setText(model.getLastName());
                holder.tvEmail.setText(model.getEmailAddressUsername());
                holder.tvPhoneNumber.setText(model.getPhoneNumber());
                holder.tvRole.setText(model.getRole());

                String status = model.getRegistrationStatus();
                holder.chipStatus.setText(status);
                styleStatusChip(holder.chipStatus, status);

                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(AdminPage.this, WelcomePage.class);
                    intent.putExtra("EmailAddressUsername", model.getEmailAddressUsername());
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_user, parent, false);

                return new VH(view);
            }

        };

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView.setAdapter(adapter);
    }

    @Override protected void onStart() { super.onStart(); adapter.startListening(); }
    @Override protected void onStop() { super.onStop(); adapter.stopListening(); }
}