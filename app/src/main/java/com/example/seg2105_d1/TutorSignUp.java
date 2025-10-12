package com.example.seg2105_d1;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TutorSignUp extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //degrees array for dropdown spinner
    private String[] degrees = {"High School Diploma","College Diploma","Bachelor's","Master's","PhD"};

    //initializing new tutor
    Tutor newTutor =  new Tutor();

    //editText variables
    EditText tutorFirstName, tutorLastName, tutorEmail, tutorPassword, tutorPhone, tutorCourse;

    TextView errorBox;


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

        errorBox = findViewById(R.id.errorText);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        newTutor.setHighestDegree((String) parent.getItemAtPosition(position));
    }

    public void onNothingSelected(AdapterView<?> parent) {
        //dummy interface callback?
    }

    public void addCourse(View v) {
        String course = tutorCourse.getText().toString();
        newTutor.addCourses(course);
        tutorCourse.getText().clear();

    }

    //onClick for sign up button; fully creates the tutor
    public void signUp(View v) {
        //getting strings from editText fields
        String firstName = tutorFirstName.getText().toString();
        String lastName = tutorLastName.getText().toString();
        String email = tutorEmail.getText().toString();
        String password = tutorPassword.getText().toString();
        String phoneNumber = tutorPhone.getText().toString();

        //setting tutor instance variables
        newTutor.setFirstName(firstName);
        newTutor.setLastName(lastName);

        try {
            newTutor.setEmailAddressUsername(email);
        } catch(IllegalArgumentException e) {

        }

        newTutor.setAccountPassword(password);

        newTutor.setPhoneNumber(phoneNumber);

        //register tutor (check if email already exists in user list)

        //confirmation toast
        Toast.makeText(this,"New Tutor Created: " + firstName + " " + lastName, Toast.LENGTH_SHORT).show();
    }
}