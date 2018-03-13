package com.segfault.homelessshelter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class ShelterListActivity extends AppCompatActivity {

    LinearLayout shelterListLinearLayout;
    Toolbar toolbar;

    ArrayList<Shelter> shelters;
    boolean isAdvancedSearch;
    String shelterNameFilter;
    String genderFilter;
    String ageRangeFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter_list);

        // Set view variables
        shelterListLinearLayout = findViewById(R.id.shelterListLinearLayout);
        toolbar = findViewById(R.id.shelterListToolbar);

        // Set other variables
        shelters = new ArrayList<>();
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            isAdvancedSearch = true;
            shelterNameFilter = extras.getString("SHELTERNAME");
            genderFilter = extras.getString("GENDER");
            ageRangeFilter = extras.getString("AGERANGE");
        }

        // Set the toolbar
        setSupportActionBar(toolbar);

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

        // Populate shelter list view with shelters from ArrayList, filtering if necessary
        for(final Shelter shelter : shelters) {
            // Check filters first
            if(!matchesFilter(shelter)) {
                continue;
            }
            // Passed filters, if any, so add to view
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the toolbar and add buttons
        getMenuInflater().inflate(R.menu.shelter_list_menu, menu);
        // The return determines if the toolbar should appear (true if it should, false otherwise)
        // So if this is not an advanced search, we return true, otherwise false
        return !isAdvancedSearch;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User has clicked the search toolbar button
        startActivity(new Intent(this, AdvancedSearchActivity.class));
        return true;
    }

    // Private helper methods

    private boolean matchesFilter(Shelter shelter) {
        // Return true if we're not searching, or if we match all three filters
        if (!isAdvancedSearch) return true;
        return matchesShelterName(shelter.getShelterName())
                && matchesGender(shelter.getRestrictions())
                && matchesAgeRange(shelter.getRestrictions());
    }

    private boolean matchesShelterName(String shelterName) {
        return shelterName.toLowerCase().contains(shelterNameFilter.toLowerCase());
    }

    private boolean matchesGender(String restrictions) {
        return genderFilter.equals("Anyone") // Gender wasn't filtered
                || hasUnspecifiedGender(restrictions) // Shelter has unspecified gender restriction
                || restrictions.contains(genderFilter); // Shelter matches gender restriction
    }

    private boolean hasUnspecifiedGender(String restrictions) {
        String[] genders = {"Men", "Women", "Trans men", "Trans women"};
        for(String gender : genders) {
            if(restrictions.contains(gender)) {
                return false; // Restriction specifies some gender
            }
        }
        return true; // Restriction didn't specify any gender
    }

    private boolean matchesAgeRange(String restrictions) {
        return ageRangeFilter.equals("Anyone") // Age range wasn't filtered
                || hasUnspecifiedAgeRange(restrictions) // Shelter has unspecified age range restriction
                || restrictions.contains(ageRangeFilter);  // Shelter matches age range restriction
    }

    private boolean hasUnspecifiedAgeRange(String restrictions) {
        String[] ageRanges = {"Newborns", "Children", "Young adults"};
        for(String ageRange : ageRanges) {
            if(restrictions.contains(ageRange)) {
                return false; // Restriction specifies some age range
            }
        }
        return true; // Restriction didn't specify any age range
    }
}
