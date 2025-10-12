package com.example.seg2105_d1.ViewController;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seg2105_d1.Model.Student;
import com.example.seg2105_d1.Model.User;
import com.example.seg2105_d1.R;


public class StudentSignUpPage extends AppCompatActivity {

    EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword, editTextPhone, editTextProgram;
    Button btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_signup_page);

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
            User user = new Student(programOfStudy, firstName, lastName, email, password, phone);
            //Register User into list of Students
//            user.register(user, new RegisterCallback() {
//                @Override
//                public void onSuccess() {
//                    //Show confirmation
//                    Toast.makeText(StudentSignUp.this, "User created: " + user.getFirstName() + " " + user.getLastName(), Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onFailure(String s) {
//                    Toast.makeText(StudentSignUp.this, "Registration failed: " + s, Toast.LENGTH_SHORT).show();
//                }
//            });
        });
    }
    }
