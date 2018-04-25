package com.segfault.homelessshelter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
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
    private Storage storage;
    private GoogleMap gMap;

    private int uniqueKeyOfReservedBeds = -1; // Unique key of the shelter the user has claimed beds
    private int reservedBeds;

    private boolean isAdvancedSearch;
    private String nameFilter;
    private String genderFilter;
    private String ageFilter;

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

        // Get Storage instance
        Context context = getApplicationContext();
        storage = Storage.getInstance(context);

        // Set other variables
        markersToKeys = new HashMap<>();
        uniqueKeyOfReservedBeds = storage.loadInt("uniqueKeyOfReservedBeds");
        reservedBeds = storage.loadInt("reservedBeds");
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            isAdvancedSearch = true;
            nameFilter = extras.getString("SHELTERNAME");
            genderFilter = extras.getString("GENDER");
            ageFilter = extras.getString("AGERANGE");
        }

        // Set the toolbar
        setSupportActionBar(toolbar);
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
    public void onMapReady(GoogleMap gMap) {
        this.gMap = gMap;

        // Populate shelter ArrayList either from CSV file or storage
        Set<String> shelterStorageEntries = storage.loadStringSet("shelters");
        if(shelterStorageEntries.isEmpty()) {
            populateArrayListFromCSV();
        } else {
            populateArrayListFromStorage(shelterStorageEntries);
        }

        // Add the shelter markers
        for(int i = 0; i < shelters.size(); i++) {
            final Shelter shelter = shelters.get(i);
            // Check filters first
            if(isAdvancedSearch && !shelter.matchesFilter(nameFilter, genderFilter, ageFilter)) {
                continue;
            }
            LatLng shelterLatLng = new LatLng(shelter.getLatitude(), shelter.getLongitude());
            Marker marker = gMap.addMarker(new MarkerOptions().position(shelterLatLng));
            markersToKeys.put(marker, shelter.getUniqueKey());
        }

        // Set padding of toolbar
        gMap.setPadding(0, 150, 0, 0);

        // Check if we have location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // If so enable location on map
            gMap.setMyLocationEnabled(true);
        } else {
            // If not ask for permissions
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, 0);
        }

        // Enable zoom controls
        UiSettings uiSettings = gMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        // Move the camera to Atlanta with zoom
        final double ATL_LAT = 33.7490;
        final double ATL_LNG = -84.3880;
        LatLng atlantaPosition = new LatLng(ATL_LAT, ATL_LNG);
        final float ZOOM = 11.0f;
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(atlantaPosition, ZOOM));

        // Set marker click listener (below) to handle marker clicks
        gMap.setOnMarkerClickListener(new MarkerClickListener());
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            gMap.setMyLocationEnabled(true);
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
        Intent intent;
        switch(item.getItemId()) {
            case R.id.action_search:
                // User has clicked the search toolbar button
                intent = new Intent(this, AdvancedSearchActivity.class);
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
            case R.id.action_auto_request:
                if(!gMap.isMyLocationEnabled()) {
                    return false;
                }
                Location myLocation = gMap.getMyLocation();
                LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                Shelter closestShelter = shelters.get(0);
                float closestDistance = Float.MAX_VALUE;
                for(int i = 0; i < shelters.size(); i++) {
                    Shelter shelter = shelters.get(i);
                    LatLng shelterLatLng = new LatLng(shelter.getLatitude(), shelter.getLongitude());
                    if(distance(myLatLng, shelterLatLng) < closestDistance) {
                        closestShelter = shelter;
                        closestDistance = distance(myLatLng, shelterLatLng);
                    }
                }
                intent = new Intent(MapsActivity.this, ShelterDetailActivity.class);
                intent.putExtra("SHELTER", closestShelter);
                intent.putExtra("CANRESERVE", uniqueKeyOfReservedBeds == -1);
                startActivityForResult(intent, 0);
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
            storage.saveInt("uniqueKeyOfReservedBeds",
                                                    uniqueKeyOfReservedBeds);
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
        storage.saveStringSet("shelters", shelterSet);
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

    private static float distance(LatLng ll1, LatLng ll2) {
        float[] result = new float[3];
        Location.distanceBetween(ll1.latitude, ll1.longitude, ll2.latitude, ll2.longitude, result);
        return result[0];
    }

    // Helper classes

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
}
