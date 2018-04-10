package com.segfault.homelessshelter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Uses Google's default Google Maps Activity template
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private SparseArray<Shelter> shelters; // Android HashMap that uses ints as keys
    private HashMap<Marker, Integer> markersToKeys; // HashMap to associate Shelter keys and markers
    private int uniqueKeyOfReservedBeds = -1; // Unique key of the shelter the user has claimed beds
    private int reservedBeds;

    private boolean isAdvancedSearch;
    private String shelterNameFilter;
    private String genderFilter;
    private String ageRangeFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Set view variables
        Toolbar toolbar = findViewById(R.id.mapsToolbar);

        // Set other variables
        markersToKeys = new HashMap<>();
        Context context = getApplicationContext();
        uniqueKeyOfReservedBeds = Storage.getInstance(context).loadInt("uniqueKeyOfReservedBeds");
        reservedBeds = Storage.getInstance(context).loadInt("reservedBeds");
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            isAdvancedSearch = true;
            shelterNameFilter = extras.getString("SHELTERNAME");
            genderFilter = extras.getString("GENDER");
            ageRangeFilter = extras.getString("AGERANGE");
        }

        // Set the toolbar
        setSupportActionBar(toolbar);

        // Populate shelter ArrayList either from CSV file or storage
        Set<String> shelterStorageEntries = Storage.getInstance(context).loadStringSet("shelters");
        if(shelterStorageEntries.isEmpty()) {
            populateArrayListFromCSV();
        } else {
            populateArrayListFromStorage(shelterStorageEntries);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Add the shelter markers
        for(int i = 0; i < shelters.size(); i++) {
            final Shelter shelter = shelters.get(i);
            // Check filters first
            if(!matchesFilter(shelter)) {
                continue;
            }
            LatLng shelterPosition = new LatLng(shelter.getLatitude(), shelter.getLongitude());
            Marker marker = googleMap.addMarker(new MarkerOptions().position(shelterPosition)
                                                                .title(shelter.getShelterName()));
            markersToKeys.put(marker, shelter.getUniqueKey());
        }

        // Move the camera to Atlanta with zoom
        final double ATL_LAT = 33.7490;
        final double ATL_LNG = -84.3880;
        LatLng atlantaPosition = new LatLng(ATL_LAT, ATL_LNG);
        final float ZOOM = 11.0f;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(atlantaPosition, ZOOM));

        // Set marker click listener (below) to handle marker clicks
        googleMap.setOnMarkerClickListener(new MarkerClickListener());
    }

    private class MarkerClickListener implements GoogleMap.OnMarkerClickListener {

        @Override
        public boolean onMarkerClick(Marker marker) {
            Shelter shelter = shelters.get(markersToKeys.get(marker));
            Intent intent = new Intent(MapsActivity.this, ShelterDetailActivity.class);
            intent.putExtra("SHELTER", shelter);
            intent.putExtra("CANRESERVE", uniqueKeyOfReservedBeds == -1);
            startActivityForResult(intent, 0);
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the toolbar and add buttons
        getMenuInflater().inflate(R.menu.maps_menu, menu);
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
                intent.putExtra("ORIGIN", "MapsActivity");
                startActivity(intent);
                return true;
            case R.id.action_cancel:
                // User has clicked the cancel reservation toolbar button
                resetReservations();
                return false;
            case R.id.action_list:
                finish();
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

    // Helper methods

    private void resetReservations() {
        if(uniqueKeyOfReservedBeds != -1) {
            Shelter shelter = shelters.get(uniqueKeyOfReservedBeds);
            shelter.setVacancy(shelter.getVacancy() + reservedBeds); // Reset beds
            uniqueKeyOfReservedBeds = -1;
            reservedBeds = 0;
            Context context = getApplicationContext();
            Storage.getInstance(context).saveInt("uniqueKeyOfReservedBeds",
                                                    uniqueKeyOfReservedBeds);
            Storage.getInstance(context).saveInt("reservedBeds", reservedBeds);
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
        Storage.getInstance(context).saveInt("uniqueKeyOfReservedBeds", uniqueKeyOfReservedBeds);
        Storage.getInstance(context).saveInt("reservedBeds", reservedBeds);
        saveShelters();
    }

    private void saveShelters() {
        Set<String> shelterSet = new HashSet<>();
        for(int i = 0; i < shelters.size(); i++) {
            Shelter shelter = shelters.get(i);
            shelterSet.add(shelter.toEntry());
        }
        Context context = getApplicationContext();
        Storage.getInstance(context).saveStringSet("shelters", shelterSet);
    }

    private void populateArrayListFromCSV() {
        shelters = new SparseArray<>();
        // Open database.csv file
        int csvId = getResources().getIdentifier("database", "raw", getPackageName());
        InputStream inputStream = getResources().openRawResource(csvId);
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
        return shelterName.toLowerCase().contains(shelterNameFilter.toLowerCase());
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
