package com.segfault.homelessshelter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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

    private Storage storage;
    private Logger logger;
    private Map<String, User> users; // Key is email
    private long lockOutEnd;
    private int attempts;

    private static final int LOCKOUT_PERIOD = 300000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set view variables
        emailEditText = findViewById(R.id.loginEmailEditText);
        passwordEditText = findViewById(R.id.loginPasswordEditText);
        Button loginButton = findViewById(R.id.loginLoginButton);
        Button registerButton = findViewById(R.id.loginRegisterButton);

        // Get Storage and Logger instances
        Context context = getApplicationContext();
        storage = Storage.getInstance(context);
        logger = Logger.getInstance(context);

        // Load users from storage
        users = new HashMap<>();
        Set<String> userStorageEntries = storage.loadStringSet("users");
        for(String userStorageEntry : userStorageEntries) {
            User user = UserFactory.createFromStorageEntry(userStorageEntry);
            users.put(user.getEmail(), user);
        }

        // Load attempts and lockOutEnd from storage
        attempts = storage.loadInt("attempts", 3);
        lockOutEnd = storage.loadLong("lockoutend");

        // Continue lock out if needed
        if(attempts == 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    attempts = 3;
                }
            }, lockOutEnd - System.currentTimeMillis());
        } else if(System.currentTimeMillis() > lockOutEnd) {
            attempts = 3;
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
                if (attempts == 0) {
                    // Not enough remaining attempts
                    logger.printWarning("Login attempt while locked out with email " + email);
                    Toast.makeText(LoginActivity.this,
                            "Locked out, please try again shortly", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (user == null) {
                    // Incorrect email
                    logger.printMessage("Login attempt with incorrect email " + email);
                    Toast.makeText(LoginActivity.this,
                            "Incorrect email", Toast.LENGTH_SHORT).show();
                    return;
                }
                String userPassword = user.getPassword();
                if (!userPassword.equals(password)) {
                    // Incorrect password...
                    attempts--;
                    if (attempts > 0) {
                        // ...but still has enough attempts
                        logger.printMessage("Login attempt with correct email " + email +
                                " and incorrect password, with " +
                                (attempts + 1) + " attempt(s) remaining");
                        Toast.makeText(LoginActivity.this, "Incorrect password, "
                                        + attempts + " attempt(s) remaining",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // ...and has just run out of attempts
                        logger.printWarning("Login attempt with correct email " + email +
                                " and incorrect password, locking out");
                        Toast.makeText(LoginActivity.this, "Too many incorrect" +
                                " attempts, locking out", Toast.LENGTH_SHORT).show();
                        lockOutEnd = System.currentTimeMillis() + LOCKOUT_PERIOD;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                attempts = 3;
                            }
                        }, LOCKOUT_PERIOD);
                    }
                    return;
                }
                // Successful login
                storage.saveInt("attempts", 3);
                logger.printMessage("Successful login with email " + email);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });

        // Register button behaviour
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(LoginActivity.this,
                                                    RegistrationActivity.class), 0);
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
                /* An account with this email already exists, so tell the user and don't create
                a new account */
                logger.printMessage("Account creation attempt with existing email " + email);
                Toast.makeText(LoginActivity.this,
                        "An account with this email already exists", Toast.LENGTH_SHORT).show();
                return;
            }
            logger.printMessage("Successful account creation with email " + email);
            String name = data.getStringExtra("NAME");
            String password = data.getStringExtra("PASS");
            boolean admin = data.getBooleanExtra("ADMIN", false);
            if(admin) {
                users.put(email, UserFactory.create(UserFactory.UserType.ADMIN, name, email, password));
            } else {
                users.put(email, UserFactory.create(UserFactory.UserType.CLIENT, name, email, password));
            }
            // Convert HashMap to Set of strings and save
            Set<String> userSet = new HashSet<>();
            for(String key : users.keySet()) {
                userSet.add(users.get(key).toEntry());
            }
            Context context = getApplicationContext();
            storage.saveStringSet("users", userSet);
            // Go to main activity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        storage.saveInt("attempts", attempts);
        storage.saveLong("lockoutend", lockOutEnd);
    }
}
