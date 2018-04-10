package com.segfault.homelessshelter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.List;

public class AdvancedSearchActivity extends AppCompatActivity {

    private EditText nameEditText;
    private Spinner genderSpinner;
    private Spinner ageRangeSpinner;

    private final List<String> genders = Arrays.asList("Anyone", "Men", "Women", "Trans men",
                                                        "Trans women");
    private final List<String> ageRanges = Arrays.asList("Anyone", "Newborns", "Children",
                                                        "Young adults");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_search);

        // Set view variables
        nameEditText = findViewById(R.id.advancedSearchNameEditText);
        genderSpinner = findViewById(R.id.advancedSearchGenderSpinner);
        ageRangeSpinner = findViewById(R.id.advancedSearchAgeRangeSpinner);
        Button searchButton = findViewById(R.id.advancedSearchButton);

        // Populate gender spinner
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this,
                                                    android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Populate age range spinner
        ArrayAdapter<String> ageRangeAdapter = new ArrayAdapter<>(this,
                                                android.R.layout.simple_spinner_item, ageRanges);
        ageRangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageRangeSpinner.setAdapter(ageRangeAdapter);

        // Set search button behaviour
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                Bundle extras = intent.getExtras();
                String origin = extras.getString("ORIGIN", "ShelterListActivity");
                if("ShelterListActivity".equals(origin)) {
                    intent = new Intent(AdvancedSearchActivity.this,
                                        ShelterListActivity.class);
                } else {
                    intent = new Intent(AdvancedSearchActivity.this,
                                        MapsActivity.class);
                }
                Editable nameEditTextText = nameEditText.getText();
                intent.putExtra("SHELTERNAME", nameEditTextText.toString());
                Object genderSelectedItem = genderSpinner.getSelectedItem();
                intent.putExtra("GENDER", genderSelectedItem.toString());
                Object ageSelectedItem = ageRangeSpinner.getSelectedItem();
                intent.putExtra("AGERANGE", ageSelectedItem.toString());
                startActivity(intent);
            }
        });
    }
}
