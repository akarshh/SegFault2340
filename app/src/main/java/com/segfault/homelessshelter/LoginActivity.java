package com.segfault.homelessshelter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;

    private Map<String, User> users; // Key is email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set view variables
        emailEditText = findViewById(R.id.loginEmailEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);
        Button loginButton = findViewById(R.id.loginLoginButton);
        Button registerButton = findViewById(R.id.loginRegisterButton);

        // Load users from storage
        users = new HashMap<>();
        Context context = getApplicationContext();
        Storage storage = Storage.getInstance(context);
        Set<String> userStorageEntries = storage.loadStringSet("users");
        for(String userStorageEntry : userStorageEntries) {
            User user = User.createFromStorageEntry(userStorageEntry);
            users.put(user.getEmail(), user);
        }

        // Login button behaviour
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable emailEditTextText = emailEditText.getText();
                String email = emailEditTextText.toString();
                Editable passwordEditTextText = passwordEditText.getText();
                String password = passwordEditTextText.toString();
                User user = users.get(email);
                String userPassword = user.getPassword();
                if(users.containsKey(email) && userPassword.equals(password)) {
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
        if((requestCode == 0) && (resultCode == Activity.RESULT_OK)) {
            String email = data.getStringExtra("EMAIL");
            if(users.containsKey(email)) {
                // An account with this email already exists, so tell the user and don't create a new account
                Toast.makeText(LoginActivity.this, "An account with this email already exists", Toast.LENGTH_SHORT).show();
                return;
            }
            String name = data.getStringExtra("NAME");
            String password = data.getStringExtra("PASS");
            boolean admin = data.getBooleanExtra("ADMIN", false);
            users.put(email, new User(name, email, password, admin));
            // Convert HashMap to Set of strings and save
            Set<String> userSet = new HashSet<>();
            for(String key : users.keySet()) {
                userSet.add(users.get(key).toEntry());
            }
            Context context = getApplicationContext();
            Storage.getInstance(context).saveStringSet("users", userSet);
            // Go to main activity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }
}
