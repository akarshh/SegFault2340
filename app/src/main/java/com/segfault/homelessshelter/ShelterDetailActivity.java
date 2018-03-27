package com.segfault.homelessshelter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    TextView vacancyTextView;
    Button minusReserveButton;
    Button plusReserveButton;
    TextView reserveTextView;
    Button reserveButton;

    int reservedBeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter_detail);

        // Set view variables
        shelterDetailLinearLayout = findViewById(R.id.shelterDetailLinearLayout);
        uniqueKeyTextView = findViewById(R.id.shelterDetailUniqueKeyTextView);
        shelterNameTextView = findViewById(R.id.shelterDetailNameTextView);
        capacityTextView = findViewById(R.id.shelterDetailCapacityTextView);
        restrictionsTextView = findViewById(R.id.shelterDetailRestrictionsTextView);
        longitudeTextView = findViewById(R.id.shelterDetailLongitudeTextView);
        latitudeTextView = findViewById(R.id.shelterDetailLatitudeTextView);
        addressTextView = findViewById(R.id.shelterDetailAddressTextView);
        specialNotesTextView = findViewById(R.id.shelterDetailSpecialNotesTextView);
        phoneNumberTextView = findViewById(R.id.shelterDetailPhoneNumberTextView);
        vacancyTextView = findViewById(R.id.shelterDetailVacancyTextView);
        minusReserveButton = findViewById(R.id.minusReserveButton);
        plusReserveButton = findViewById(R.id.plusReserveButton);
        reserveTextView = findViewById(R.id.reserveTextView);
        reserveButton = findViewById(R.id.reserveButton);

        // Get shelter from intent's extras
        final Shelter shelter = getIntent().getExtras().getParcelable("SHELTER");

        // Get whether user can reserve beds
        boolean canReserve = getIntent().getExtras().getBoolean("CANRESERVE", false);

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
        vacancyTextView.setText("Vacancy: " + shelter.getVacancy());

        // Hide vacancy if capacity is incompatible (vacancy is -1)
        if(shelter.getVacancy() == -1) {
            vacancyTextView.setVisibility(View.INVISIBLE);
        }

        // Hide reservation controls if capacity is incompatible (vacancy is -1) or if user can't reserve new beds
        if(shelter.getVacancy() == -1 || !canReserve) {
            minusReserveButton.setVisibility(View.INVISIBLE);
            plusReserveButton.setVisibility(View.INVISIBLE);
            reserveTextView.setVisibility(View.INVISIBLE);
            reserveButton.setVisibility(View.INVISIBLE);
        }

        // Set buttons' behaviours
        minusReserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(reservedBeds > 0) {
                    reservedBeds--;
                    reserveTextView.setText(String.valueOf(reservedBeds));
                }
            }
        });

        plusReserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(reservedBeds < shelter.getVacancy()) {
                    reservedBeds++;
                    reserveTextView.setText(String.valueOf(reservedBeds));
                }
            }
        });

        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(reservedBeds <= shelter.getVacancy() && reservedBeds >= 1) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("KEY", shelter.getUniqueKey());
                    resultIntent.putExtra("RESERVEDBEDS", reservedBeds);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(ShelterDetailActivity.this, "Please select a valid amount of beds!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
