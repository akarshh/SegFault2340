package com.segfault.homelessshelter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private CheckBox adminCheckBox;
    private EditText adminKeyTextBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Set view variables
        nameEditText = findViewById(R.id.registrationNameEditText);
        emailEditText = findViewById(R.id.registrationEmailEditText);
        passwordEditText = findViewById(R.id.registrationPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.registrationConfirmPasswordEditText);
        adminCheckBox = findViewById(R.id.registrationAdminCheckBox);
        adminKeyTextBox = findViewById(R.id.registrationAdminKeyEditText);
        Button okButton = findViewById(R.id.registrationOKButton);

        // OK button behaviour
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable adminKeyTextBoxEditable = adminKeyTextBox.getText();
                String adminKeyTextBoxString = adminKeyTextBoxEditable.toString();
                if(adminCheckBox.isChecked() && !"tempkey".equals(adminKeyTextBoxString)) {
                    // User has selected admin, but has incorrect admin key
                    Toast.makeText(RegistrationActivity.this, "Admin key is incorrect",
                                    Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!passwordEditText.getText().toString()
                                            .equals(confirmPasswordEditText.getText().toString())) {
                    // User's password and confirmation aren't matching
                    Toast.makeText(RegistrationActivity.this,
                                    "Password and confirm password do not match",
                                    Toast.LENGTH_SHORT).show();
                    return;
                }
                // User is past all potential problems and has inputted correct information
                Intent resultIntent = new Intent();
                resultIntent.putExtra("NAME", nameEditText.getText().toString());
                resultIntent.putExtra("EMAIL", emailEditText.getText().toString());
                resultIntent.putExtra("PASS", passwordEditText.getText().toString());
                resultIntent.putExtra("ADMIN", adminCheckBox.isChecked());
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
