package com.example.seg2105_d1.ViewController;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seg2105_d1.Model.Student;
import com.example.seg2105_d1.Model.User;
import com.example.seg2105_d1.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class StudentSignUpPage extends AppCompatActivity {

    EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword, editTextPhone, editTextProgram;
    Button btnSignUp;

    TextView errorBox;

    //student initialization
    Student newStudent = new Student();

    //database initialization
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_signup_page);

        db = FirebaseFirestore.getInstance();

        // Link UI elements
        editTextFirstName = findViewById(R.id.fNameStudentSignUp);
        editTextLastName = findViewById(R.id.lNameStudentSignUp);
        editTextEmail = findViewById(R.id.emailAdressStudentSignUp);
        editTextPassword = findViewById(R.id.passwordStudentSignUp);
        editTextPhone = findViewById(R.id.phoneNumberStudentSignUp);
        editTextProgram = findViewById(R.id.programOfStudySignUp);
        btnSignUp = findViewById(R.id.button);

        errorBox = findViewById(R.id.studentSignUpErrorText);

        btnSignUp.setOnClickListener(v -> {
            //Get user input
            String firstName = editTextFirstName.getText().toString();
            String lastName = editTextLastName.getText().toString();
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String phone = editTextPhone.getText().toString();
            String programOfStudy = editTextProgram.getText().toString();

            //Create a new Student object using the constructor
            //User user = new Student(programOfStudy, firstName, lastName, email, password, phone);
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

            //update student parameters
            try {
                newStudent.setFirstName(firstName);
                newStudent.setLastName(lastName);
                newStudent.setEmailAddressUsername(email);
                newStudent.setAccountPassword(password);
                newStudent.setPhoneNumber(phone);
                newStudent.setProgramOfStudy(programOfStudy);
                newStudent.setRegistrationStatus("PENDING");
                submitResgistration();
            } catch(IllegalArgumentException e) {
                switch (e.getMessage()){
                    case "empty firstname":
                        errorBox.setText("Firstname is empty");
                        break;
                    case "empty lastname":
                        errorBox.setText("Lastname is empty");
                        break;
                    case "empty email":
                        errorBox.setText("Email is empty");
                        break;
                    case "invalid email":
                        errorBox.setText("Email format is incorrect");
                        break;
                    case "empty password":
                        errorBox.setText("Password is empty");
                        break;
                    case "empty phone number":
                        errorBox.setText("Phone number is empty");
                        break;
                    case "invalid phone number":
                        errorBox.setText("Phone number is invalid");
                        break;
                    case "empty programOfStudy":
                        errorBox.setText("Program of Study is empty");
                        break;
                }
            }
        });
    }

    private void submitResgistration(){
        db.collection("users")
                .whereEqualTo("emailAddressUsername", newStudent.getEmailAddressUsername())
                .get()
                .addOnSuccessListener(this::handleResgistration)
                .addOnFailureListener(e -> {
                    errorBox.setText("Database connection lost due to " + e.getMessage());
                });
    }

    private void handleResgistration(QuerySnapshot queryDocumentSnapshots){

        if(queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){
            errorBox.setText("Email already registered");
        }else{
            Map<String, Object> data = new HashMap<>();
            data.put("role", "STUDENT");
            data.put("firstname", newStudent.getFirstName());
            data.put("lastname", newStudent.getLastName());
            data.put("emailAddressUsername", newStudent.getEmailAddressUsername());
            data.put("accountPassword", newStudent.getAccountPassword());
            data.put("phoneNumber", newStudent.getPhoneNumber());
            data.put("programOfStudy", newStudent.getProgramOfStudy());
            data.put("registrationStatus", newStudent.getRegistrationStatus());

            db.collection("users")
                    .add(data)
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(this,"New Student Created: " + newStudent.getFirstName() + " " + newStudent.getLastName(), Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(StudentSignUpPage.this, LoginPage.class);
                        startActivity(intent1);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        errorBox.setText("Database connection lost due to " + e.getMessage());
                    });
        }
    }
}
