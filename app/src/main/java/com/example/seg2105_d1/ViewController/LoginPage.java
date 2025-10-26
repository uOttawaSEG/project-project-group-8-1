package com.example.seg2105_d1.ViewController;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seg2105_d1.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;
import java.util.Objects;

public class LoginPage extends AppCompatActivity {

    EditText editTextEmailAddress, editTextPassword;
    Button btnLogIn;
    TextView errorText;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Link UI elements
        editTextEmailAddress = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        btnLogIn = findViewById(R.id.logInButton);
        errorText = findViewById(R.id.error_text);

        db = FirebaseFirestore.getInstance();

//        btnLogIn.setOnClickListener(v ->{
//
//                //Get User input
//                String emailAddress = editTextEmailAddress.getText().toString();
//                String password = editTextPassword.getText().toString();
//                try {
//                    User user = login(emailAddress, password);
//
//                    Intent intent = new Intent(LoginActivity.this, WelcomeScreen.class);
//                    intent.putExtra("user_type", user.getClass().toString());
//                }
//                catch (IncorrectLoginException e){
//                    errorText.setText("Incorrect email or password.");
//                    errorText.setVisibility(View.VISIBLE);
//                }
//
//        });

        btnLogIn.setOnClickListener(v -> loginAction());
    }

    private void loginAction(){
        String email = editTextEmailAddress.getText() == null ? "" : editTextEmailAddress.getText().toString().trim();
        String password = editTextPassword.getText() == null ? "" : editTextPassword.getText().toString().trim();

        errorText.setVisibility(View.GONE);

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            errorText.setText("Please enter email and password.");
            errorText.setVisibility(View.VISIBLE);
        }else {
            db.collection("users")
                    .whereEqualTo("emailAddressUsername", email)
                    .get()
                    .addOnSuccessListener(this::handleLoginQueryResult)
                    .addOnFailureListener(e -> {
                        errorText.setText("Database connection lost due to " + e.getMessage());
                        errorText.setVisibility(View.VISIBLE);
                    });
        }
    }

    private void handleLoginQueryResult(QuerySnapshot queryDocumentSnapshots) {

        String email = editTextEmailAddress.getText() == null ? "" : editTextEmailAddress.getText().toString().trim();
        String password = editTextPassword.getText() == null ? "" : editTextPassword.getText().toString().trim();
        boolean loginSuccessful = true;

        if(queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()){
            errorText.setText("User not found");
            errorText.setVisibility(View.VISIBLE);
            loginSuccessful = false;
            return;
        }

        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
        Map<String, Object> data = document.getData();

        if(data == null){
            errorText.setText("Invalid user data");
            errorText.setVisibility(View.VISIBLE);
            loginSuccessful = false;
            return;
        }

        if(!Objects.toString(data.get("accountPassword"), "").equals(password)){
            errorText.setText("Incorrect email or password");
            errorText.setVisibility(View.VISIBLE);
            loginSuccessful = false;
            return;
        }

        if(loginSuccessful) {
            String registrationStatus = Objects.toString(data.get("registrationStatus"), "PENDING").toLowerCase();
            switch (registrationStatus){
                case "registered":
                    Intent registeredIntent = new Intent(LoginPage.this, WelcomePage.class);
                    registeredIntent.putExtra("user_type", Objects.toString(data.get("role"), "UNKNOWN"));
                    startActivity(registeredIntent);
                    finish();
                    break;

                case "pending":
                    Intent pendingIntent = new Intent(LoginPage.this, AwaitingApprovalPage.class);
                    pendingIntent.putExtra("registrationStatus", registrationStatus);
                    pendingIntent.putExtra("user_email", email);
                    startActivity(pendingIntent);
                    finish();
                    break;

                case "rejected":
                    Intent rejectedIntent = new Intent(LoginPage.this, AwaitingApprovalPage.class);
                    rejectedIntent.putExtra("registrationStatus", registrationStatus);
                    rejectedIntent.putExtra("user_email", email);
                    startActivity(rejectedIntent);
                    finish();
                    break;

                default:
                    errorText.setText("Unknown Account Status. Please Contact Support at 666 666-6666");
                    errorText.setVisibility(View.VISIBLE);
                    break;
            }


        }

    }

}