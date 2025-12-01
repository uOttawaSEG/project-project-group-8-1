package com.example.seg2105_d1.ViewController;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.seg2105_d1.R;

public class WelcomePage extends AppCompatActivity {
    TextView welcomeTextView;
    Button logOffButton;
    LinearLayout linearLayout;

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

        //points variables to ui elements
        welcomeTextView = findViewById(R.id.welcomeTextView);
        logOffButton = findViewById(R.id.logOffButton);
        linearLayout = findViewById(R.id.buttons);

        Intent intent = getIntent();
        //may need to implement the userType differently depending on login activity
        String userType = intent.getStringExtra("user_type");   //gets the user type from intent

        //displays greeting message specific to user type
        if (userType != null) {
            String message = "Welcome! You are logged in as " + userType;
            welcomeTextView.setText(message);
        }else{
            welcomeTextView.setText("Welcome! User type unknown");
        }

        //creates buttons depending on user type
        switch(userType) {
            case "ADMIN":
                //button creation
                Button viewRegistration = new Button(this);
                viewRegistration.setText("View Registration");
                //button formatting
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                //centres the button within the layout
                layoutParams.gravity = Gravity.CENTER;
                viewRegistration.setLayoutParams(layoutParams);
                //adds button to layout
                linearLayout.addView(viewRegistration);

                //send to AdminPage if button selected
                viewRegistration.setOnClickListener(v -> {
                    Intent tmpIntent = new Intent(WelcomePage.this, AdminPage.class);
                    startActivity(tmpIntent);
                });
                break;
            case "TUTOR":
                //button creation
                Button createAvailability = new Button(this);
                createAvailability.setText("Create Availability");
                //button formatting
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams1.gravity = Gravity.CENTER;
                createAvailability.setLayoutParams(layoutParams1);
                linearLayout.addView(createAvailability);

                //button creation
                Button viewAvailability = new Button(this);
                viewAvailability.setText("View Availability");
                //button formatting
                viewAvailability.setLayoutParams(layoutParams1);
                //adds button to layout
                linearLayout.addView(viewAvailability);

                //send to TutorSessionCreator if button selected
                createAvailability.setOnClickListener(v -> {
                    Intent tmpIntent = new Intent(WelcomePage.this, TutorSessionCreator.class);

                    startActivity(tmpIntent);
                });

                //send to TutorSessionViewer if button selected
                viewAvailability.setOnClickListener(v -> {
                    Intent tmpIntent = new Intent(WelcomePage.this, TutorSessionViewer.class);
                    startActivity(tmpIntent);
                });
                break;
            //Temporary placeholder case
            case "STUDENT":
                //button creation
                Button createSession = new Button(this);
                createSession.setText("Create Session");
                //button formatting
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams2.gravity = Gravity.CENTER;
                createSession.setLayoutParams(layoutParams2);
                linearLayout.addView(createSession);

                //button creation
                Button viewSessions = new Button(this);
                viewSessions.setText("View Sessions");
                //button formatting
                viewSessions.setLayoutParams(layoutParams2);
                //adds button to layout
                linearLayout.addView(viewSessions);

                //send to StudentSessionCreator if button selected
                createSession.setOnClickListener(v -> {
                    Intent tmpIntent = new Intent(WelcomePage.this, StudentSearchPage.class);

                    startActivity(tmpIntent);
                });

                //send to TutorSessionViewer if button selected
                viewSessions.setOnClickListener(v -> {
                    Intent tmpIntent = new Intent(WelcomePage.this, StudentSessionViewer.class);
                    startActivity(tmpIntent);
                });
                break;
        }

        //finishes the activity, returns to login activity
        logOffButton.setOnClickListener(v -> {
            finish();
        });
    }
}