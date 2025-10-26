package com.example.seg2105_d1.ViewController;

import android.content.Intent;
import  android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.seg2105_d1.Model.Tutor;
import com.example.seg2105_d1.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class TutorSignUpPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //degrees array for dropdown spinner
    private String[] degrees = {"High School Diploma","College Diploma","Bachelor's","Master's","PhD"};

    //initializing new tutor
    Tutor newTutor =  new Tutor();

    //editText variables
    EditText tutorFirstName, tutorLastName, tutorEmail, tutorPassword, tutorPhone, tutorCourse;

    TextView errorBox;

    Button btnSignup;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tutor_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //spinner dropdown setup
        Spinner degreeDropdown = findViewById(R.id.highestDegreeOptions);
        degreeDropdown.setOnItemSelectedListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,degrees);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        degreeDropdown.setAdapter(adapter);

        //linking ui elements
        tutorFirstName = findViewById(R.id.fNameTutor);
        tutorLastName = findViewById(R.id.lNameTutor);
        tutorEmail = findViewById(R.id.tutorEmail);
        tutorPassword = findViewById(R.id.tutorPassword);
        tutorPhone = findViewById(R.id.tutorPhone);
        tutorCourse = findViewById(R.id.tutorCourses);
        btnSignup = findViewById(R.id.tutorSignUpButton);
        errorBox = findViewById(R.id.tutorSignUpErrorText);

        db = FirebaseFirestore.getInstance();

        btnSignup.setOnClickListener(v -> signUp());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        newTutor.setHighestDegree((String) parent.getItemAtPosition(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        newTutor.setHighestDegree("");
    }

    public void addCourse(View v) {
        String course = tutorCourse.getText().toString();
        newTutor.addCourses(course);
        tutorCourse.getText().clear();
    }
    private void signUp() {

        try {
            newTutor.setFirstName(tutorFirstName.getText().toString());
            newTutor.setLastName(tutorLastName.getText().toString());
            newTutor.setEmailAddressUsername(tutorEmail.getText().toString());
            newTutor.setPhoneNumber(tutorPhone.getText().toString());
            newTutor.setAccountPassword(tutorPassword.getText().toString());
            if(newTutor.getCoursesOffered().isEmpty()){
                throw new IllegalArgumentException("empty courses offered");
            }
            submitResgistration();
        }catch(IllegalArgumentException e){
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
                case "empty courses offered":
                    errorBox.setText("No courses offered");
                    break;
            }

        }

        //register tutor (check if email already exists in user list)

        //confirmation toast
        //Toast.makeText(this,"New Tutor Created: " + firstName + " " + lastName, Toast.LENGTH_SHORT).show();
    }

    private void submitResgistration(){
        db.collection("users")
                .whereEqualTo("emailAddressUsername", tutorEmail.getText().toString())
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
            data.put("role", "TUTOR");
            data.put("firstName", newTutor.getFirstName());
            data.put("lastName", newTutor.getLastName());
            data.put("emailAddressUsername", newTutor.getEmailAddressUsername());
            data.put("accountPassword", newTutor.getAccountPassword());
            data.put("phoneNumber", newTutor.getPhoneNumber());
            data.put("highestDegree", newTutor.getHighestDegree());
            data.put("courseOffered", newTutor.getCoursesOffered());

            db.collection("users")
                    .add(data)
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(this,"New Tutor Created: " + newTutor.getFirstName() + " " + newTutor.getLastName(), Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(TutorSignUpPage.this, LoginPage.class);
                        startActivity(intent1);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        errorBox.setText("Database connection lost due to " + e.getMessage());
                    });
        }
    }
}