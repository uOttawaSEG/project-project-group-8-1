package com.example.seg2105_d1.ViewController;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seg2105_d1.R;

public class AwaitingApprovalPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_awaiting_approval_page);

        TextView status = findViewById(R.id.status_message);
        Button backButton = findViewById(R.id.back_to_login_button);

        String approvalStatus = getIntent().getStringExtra("registrationStatus");
        String userEmail = getIntent().getStringExtra("user_email");

        String statusMessage = "Your account status could not be determined. Please contact support at 666 666-6666";

        if("pending".equalsIgnoreCase(approvalStatus)){
            statusMessage = "Your account is currently pending administrator approval. Please wait.";
        }
        else if ("rejected".equalsIgnoreCase(approvalStatus)){
            statusMessage = "Your account has been rejected by the Administrator. Unfortunately you Do not have access to this service. If you have questions, please contact admin@otams.ca.";
        }

        status.setText(statusMessage);

        backButton.setOnClickListener(v ->{
            Intent intent = new Intent(AwaitingApprovalPage.this, LoginPage.class);
            startActivity(intent);
            finish();
        });
    }
}