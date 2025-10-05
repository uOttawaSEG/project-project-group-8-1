package com.example.seg2105_d1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WelcomeScreen extends AppCompatActivity {
    TextView welcomeTextView;
    Button logOffButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        welcomeTextView = findViewById(R.id.welcomeTextView);
        logOffButton = findViewById(R.id.logOffButton);

        Intent intent = getIntent();
        //may need to implement the userType differently depending on login activity
        String userType = intent.getStringExtra("user_type");   //gets the user type from intent

        if (userType != null) {
            String message = "Welcome! You are logged in as" + userType;
            welcomeTextView.setText(message);
        }else{
            welcomeTextView.setText("Welcome! User type unknown");
        }

        //finishes the activity, returns to login activity
        logOffButton.setOnClickListener(v -> {
            finish();
        });
    }
}