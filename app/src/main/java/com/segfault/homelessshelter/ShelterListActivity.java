package com.segfault.homelessshelter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class ShelterListActivity extends AppCompatActivity {

    private SparseArray<Shelter> shelters; // Android HashMap that uses ints as keys
    private int uniqueKeyOfReservedBeds = -1; // Unique key of the shelter the user has claimed beds
    private int reservedBeds;

    private boolean isAdvancedSearch;
    private String shelterNameFilter;
    private String genderFilter;
    private String ageRangeFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter_list);

        // Set view variables
        LinearLayout shelterListLinearLayout = findViewById(R.id.shelterListLinearLayout);
        Toolbar toolbar = findViewById(R.id.shelterListToolbar);

        // Set other variables
        Context context = getApplicationContext();
        Storage storage = Storage.getInstance(context);
        uniqueKeyOfReservedBeds = storage.loadInt("uniqueKeyOfReservedBeds");
        reservedBeds = storage.loadInt("reservedBeds");
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null) {
            // If extras != null, we came from advanced search
            isAdvancedSearch = true;
            shelterNameFilter = extras.getString("SHELTERNAME");
            genderFilter = extras.getString("GENDER");
            ageRangeFilter = extras.getString("AGERANGE");
        }

        // Set the toolbar
        setSupportActionBar(toolbar);

        // Populate shelter ArrayList either from CSV file or storage
        Set<String> shelterStorageEntries = storage.loadStringSet("shelters");
        if(shelterStorageEntries.isEmpty()) {
            populateArrayListFromCSV();
        } else {
            populateArrayListFromStorage(shelterStorageEntries);
        }

        // Populate shelter list view with shelters from ArrayList, filtering if necessary
        for(int i = 0; i < shelters.size(); i++) {
            final Shelter shelter = shelters.get(i);
            // Check filters first
            if(!matchesFilter(shelter)) {
                continue;
            }
            // Passed filters, if any, so add to view
            TextView shelterNameTextView = new TextView(this);
            shelterNameTextView.setText(shelter.getShelterName());
            final int FONT_SIZE = 20;
            shelterNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, FONT_SIZE);
            // Define behaviour for going to shelter detail view after clicking shelter
            shelterNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ShelterListActivity.this,
                                                ShelterDetailActivity.class);
                    intent.putExtra("SHELTER", shelter);
                    intent.putExtra("CANRESERVE", uniqueKeyOfReservedBeds == -1);
                    startActivityForResult(intent, 0);
                }
            });
            shelterListLinearLayout.addView(shelterNameTextView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the toolbar and add buttons
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.shelter_list_menu, menu);
        // The return determines if the toolbar should appear (true if it should, false otherwise)
        // So if this is not an advanced search, we return true, otherwise false
        return !isAdvancedSearch;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_search:
                // User has clicked the search toolbar button
                Intent intent = new Intent(this, AdvancedSearchActivity.class);
                intent.putExtra("ORIGIN", "ShelterListActivity");
                startActivity(intent);
                return true;
            case R.id.action_cancel:
                // User has clicked the cancel reservation toolbar button
                resetReservations();
                return false;
            case R.id.action_map:
                startActivity(new Intent(this, MapsActivity.class));
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // We're coming back from the details activity after the user clicked reserve
        if((requestCode == 0) && (resultCode == Activity.RESULT_OK)) {
            setReservations(data);
        }
    }

    // Private helper methods

    private void resetReservations() {
        if(uniqueKeyOfReservedBeds != -1) {
            Shelter shelter = shelters.get(uniqueKeyOfReservedBeds);
            shelter.setVacancy(shelter.getVacancy() + reservedBeds); // Reset beds
            uniqueKeyOfReservedBeds = -1;
            reservedBeds = 0;
            Context context = getApplicationContext();
            Storage storage = Storage.getInstance(context);
            storage.saveInt("uniqueKeyOfReservedBeds", uniqueKeyOfReservedBeds);
            storage.saveInt("reservedBeds", reservedBeds);
            saveShelters();
        }
    }

    private void setReservations(Intent data) {
        uniqueKeyOfReservedBeds = data.getIntExtra("KEY", -1);
        reservedBeds = data.getIntExtra("RESERVEDBEDS", 0);
        if(uniqueKeyOfReservedBeds != -1) {
            Shelter shelter = shelters.get(uniqueKeyOfReservedBeds);
            shelter.setVacancy(shelter.getVacancy() - reservedBeds);
        }
        Context context = getApplicationContext();
        Storage storage = Storage.getInstance(context);
        storage.saveInt("uniqueKeyOfReservedBeds", uniqueKeyOfReservedBeds);
        storage.saveInt("reservedBeds", reservedBeds);
        saveShelters();
    }

    private void saveShelters() {
        Set<String> shelterSet = new HashSet<>();
        for(int i = 0; i < shelters.size(); i++) {
            Shelter shelter = shelters.get(i);
            shelterSet.add(shelter.toEntry());
        }
        Context context = getApplicationContext();
        Storage storage = Storage.getInstance(context);
        storage.saveStringSet("shelters", shelterSet);
    }

    private void populateArrayListFromCSV() {
        shelters = new SparseArray<>();
        // Open database.csv file
        Resources resources = getResources();
        int csvId = resources.getIdentifier("database", "raw", getPackageName());
        InputStream inputStream = resources.openRawResource(csvId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        // Read file and populate ArrayList
        try {
            reader.readLine(); // Read line once to skip first line in database.csv
            String line = reader.readLine();
            while(line != null) {
                Shelter shelter = Shelter.createFromCSVEntry(line);
                shelters.put(shelter.getUniqueKey(), shelter);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException ignored) {}
    }

    private void populateArrayListFromStorage(Iterable<String> shelterStorageEntries) {
        shelters = new SparseArray<>();
        for(String shelterStorageEntry : shelterStorageEntries) {
            Shelter shelter = Shelter.createFromStorageEntry(shelterStorageEntry);
            shelters.put(shelter.getUniqueKey(), shelter);
        }
    }

    private boolean matchesFilter(Shelter shelter) {
        // Return true if we're not searching, or if we match all three filters
        return !isAdvancedSearch || (matchesShelterName(shelter.getShelterName())
                                        && matchesGender(shelter.getRestrictions())
                                        && matchesAgeRange(shelter.getRestrictions()));
    }

    private boolean matchesShelterName(String shelterName) {
        String lowerCaseShelterName = shelterName.toLowerCase();
        return lowerCaseShelterName.contains(shelterNameFilter.toLowerCase());
    }

    private boolean matchesGender(String restrictions) {
        return "Anyone".equals(genderFilter) // Gender wasn't filtered
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
        return "Anyone".equals(ageRangeFilter) // Age range wasn't filtered
                || hasUnspecifiedAgeRange(restrictions) // Shelter has unspecified restriction
                || restrictions.contains(ageRangeFilter);  // Shelter matches restriction
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
