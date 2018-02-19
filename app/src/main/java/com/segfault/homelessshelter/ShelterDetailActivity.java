package com.segfault.homelessshelter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShelterDetailActivity extends AppCompatActivity {

    LinearLayout shelterDetailLinearLayout;
    TextView uniqueKeyTextView;
    TextView shelterNameTextView;
    TextView capacityTextView;
    TextView restrictionsTextView;
    TextView longitudeTextView;
    TextView latitudeTextView;
    TextView addressTextView;
    TextView specialNotesTextView;
    TextView phoneNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter_detail);

        // Set view variables
        shelterDetailLinearLayout = findViewById(R.id.shelterDetailLinearLayout);
        uniqueKeyTextView = findViewById(R.id.shelterUniqueKeyTextView);
        shelterNameTextView = findViewById(R.id.shelterNameTextView);
        capacityTextView = findViewById(R.id.shelterCapacityTextView);
        restrictionsTextView = findViewById(R.id.shelterRestrictionsTextView);
        longitudeTextView = findViewById(R.id.shelterLongitudeTextView);
        latitudeTextView = findViewById(R.id.shelterLatitudeTextView);
        addressTextView = findViewById(R.id.shelterAddressTextView);
        specialNotesTextView = findViewById(R.id.shelterSpecialNotesTextView);
        phoneNumberTextView = findViewById(R.id.shelterPhoneNumberTextView);

        // Get shelter from intent's extras
        Shelter shelter = getIntent().getExtras().getParcelable("SHELTER");

        // Populate detail view with shelter's information
        uniqueKeyTextView.setText("Unique Key: " + shelter.getUniqueKey());
        shelterNameTextView.setText("Name: " + shelter.getShelterName());
        capacityTextView.setText("Capacity: " + shelter.getCapacity());
        restrictionsTextView.setText("Restrictions: " + shelter.getRestrictions());
        longitudeTextView.setText("Longitude: " + shelter.getLongitude());
        latitudeTextView.setText("Latitude: " + shelter.getLatitude());
        addressTextView.setText("Address: " + shelter.getAddress());
        specialNotesTextView.setText("Special Notes: " + shelter.getSpecialNotes());
        phoneNumberTextView.setText("Phone Number: " + shelter.getPhoneNumber());
    }
}
