package com.segfault.homelessshelter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ShelterListActivity extends AppCompatActivity {

    LinearLayout shelterListLinearLayout;

    ArrayList<Shelter> shelters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter_list);

        // Set view variables
        shelterListLinearLayout = findViewById(R.id.shelterListLinearLayout);

        // Set other variables
        shelters = new ArrayList<>();

        // Open database.csv file
        int csvId = getResources().getIdentifier("database", "raw", getPackageName());
        InputStream inputStream = getResources().openRawResource(csvId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        // Read file and populate ArrayList
        try {
            reader.readLine(); // Read line once to skip first line in database.csv
            String line = reader.readLine();
            while(line != null) {
                Shelter shelter = new Shelter(line);
                shelters.add(shelter.getUniqueKey(), shelter);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {}

        // Populate shelter list view with shelters from ArrayList
        for(final Shelter shelter : shelters) {
            TextView shelterNameTextView = new TextView(this);
            shelterNameTextView.setText(shelter.getShelterName());
            shelterNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            // Define behaviour for going to shelter detail view after clicking shelter
            shelterNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ShelterListActivity.this, ShelterDetailActivity.class);
                    intent.putExtra("SHELTER", shelter);
                    startActivity(intent);
                }
            });
            shelterListLinearLayout.addView(shelterNameTextView);
        }
    }
}
