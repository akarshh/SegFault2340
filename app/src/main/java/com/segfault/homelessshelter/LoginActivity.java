package com.segfault.homelessshelter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText;
    EditText passwordEditText;
    Button loginButton;
    Button registerButton;

    HashMap<String, User> users; // Key is email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set view variables
        emailEditText = findViewById(R.id.loginEmailEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);
        loginButton = findViewById(R.id.loginLoginButton);
        registerButton = findViewById(R.id.loginRegisterButton);

        // Set other variables
        users = new HashMap<>();

        // Login button behaviour
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(emailEditText.getText().toString().equals("user") && passwordEditText.getText().toString().equals("pass")) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if(users.containsKey(email) && users.get(email).getPassword().equals(password)) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "Incorrect email / password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Register button behaviour
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(LoginActivity.this, RegistrationActivity.class), 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // We're coming back from the registration activity
        if(requestCode == 0 && resultCode == Activity.RESULT_OK) {
            String name = data.getStringExtra("NAME");
            String email = data.getStringExtra("EMAIL");
            if(users.containsKey(email)) {
                // An account with this email already exists, so tell the user and don't create a new account
                Toast.makeText(LoginActivity.this, "An account with this email already exists", Toast.LENGTH_SHORT).show();
                return;
            }
            String password = data.getStringExtra("PASS");
            boolean admin = data.getBooleanExtra("ADMIN", false);
            users.put(email, new User(name, email, password, admin));
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }
}
