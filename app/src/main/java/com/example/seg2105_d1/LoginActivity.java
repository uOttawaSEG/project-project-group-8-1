package com.example.seg2105_d1;

import static com.example.seg2105_d1.User.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    EditText editTextEmailAddress, editTextPassword;
    Button btnLogIn;
    TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Link UI elements
        editTextEmailAddress = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        btnLogIn = findViewById(R.id.logInButton);
        errorText = findViewById(R.id.error_text);

        btnLogIn.setOnClickListener(v ->{

            //Get User input
            String emailAddress = editTextEmailAddress.getText().toString();
            String password = editTextPassword.getText().toString();
            try {
                User user = login(emailAddress, password);

                Intent intent = new Intent(LoginActivity.this, WelcomeScreen.class);
                intent.putExtra("user_type", user.getClass());
            }
            catch (IncorrectLoginException e){

            }
        });

    }
}