package com.example.seg2105_d1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class StudentSignUp extends AppCompatActivity {

    EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword, editTextPhone, editTextProgram, editTextAge;
    Button btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_signup_page);

        // Link UI elements
        editTextFirstName = findViewById(R.id.fNameStudentSignUp);
        editTextLastName = findViewById(R.id.lNameStudentSignUp);
        editTextEmail = findViewById(R.id.emailAdressStudentSignUp);
        editTextPassword = findViewById(R.id.passwordStudentSignUp);
        editTextPhone = findViewById(R.id.phoneNumberStudentSignUp);
        editTextProgram = findViewById(R.id.programOfStudySignUp);
        btnSignUp = findViewById(R.id.button);

        btnSignUp.setOnClickListener(v -> {
            //Get user input
            String firstName = editTextFirstName.getText().toString();
            String lastName = editTextLastName.getText().toString();
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String phone = editTextPhone.getText().toString();
            String programOfStudy = editTextProgram.getText().toString();

            //Create a new Student object using the constructor
            User user = new Student(programOfStudy);
            //Set all the information necessary
            user.setAccountPassword(password);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmailAddressUsername(email);
            user.setPhoneNumber(phone);
            user.register(user);

            //Show confirmation (or save to database / SharedPreferences)
            Toast.makeText(this, "User created: " + user.getFirstName() + " " + user.getLastName(), Toast.LENGTH_SHORT).show();

        });
    }
    }
