package com.segfault.homelessshelter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ShelterDetailActivity extends AppCompatActivity {

    private TextView reserveTextView;

    private int reservedBeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter_detail);

        // Set view variables
        TextView uniqueKeyTextView = findViewById(R.id.shelterDetailUniqueKeyTextView);
        TextView shelterNameTextView = findViewById(R.id.shelterDetailNameTextView);
        TextView capacityTextView = findViewById(R.id.shelterDetailCapacityTextView);
        TextView restrictionsTextView = findViewById(R.id.shelterDetailRestrictionsTextView);
        TextView longitudeTextView = findViewById(R.id.shelterDetailLongitudeTextView);
        TextView latitudeTextView = findViewById(R.id.shelterDetailLatitudeTextView);
        TextView addressTextView = findViewById(R.id.shelterDetailAddressTextView);
        TextView specialNotesTextView = findViewById(R.id.shelterDetailSpecialNotesTextView);
        TextView phoneNumberTextView = findViewById(R.id.shelterDetailPhoneNumberTextView);
        TextView vacancyTextView = findViewById(R.id.shelterDetailVacancyTextView);
        Button minusReserveButton = findViewById(R.id.minusReserveButton);
        Button plusReserveButton = findViewById(R.id.plusReserveButton);
        reserveTextView = findViewById(R.id.reserveTextView);
        Button reserveButton = findViewById(R.id.reserveButton);

        // Get shelter from intent's extras
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        final Shelter shelter = extras.getParcelable("SHELTER");

        // Get whether user can reserve beds
        boolean canReserve = extras.getBoolean("CANRESERVE", false);

        // Populate detail view with shelter's information
        uniqueKeyTextView.setText(String.format(getString(R.string.unique_key_concat), shelter.getUniqueKey()));
        shelterNameTextView.setText(String.format("Name: %s", shelter.getShelterName()));
        capacityTextView.setText(String.format("Capacity: %s", shelter.getCapacity()));
        restrictionsTextView.setText(String.format("Restrictions: %s", shelter.getRestrictions()));
        longitudeTextView.setText(String.format("Longitude: %s", shelter.getLongitude()));
        latitudeTextView.setText(String.format("Latitude: %s", shelter.getLatitude()));
        addressTextView.setText(String.format("Address: %s", shelter.getAddress()));
        specialNotesTextView.setText(String.format("Special Notes: %s", shelter.getSpecialNotes()));
        phoneNumberTextView.setText(String.format("Phone Number: %s", shelter.getPhoneNumber()));
        vacancyTextView.setText(String.format(getString(R.string.vacancy_concat), shelter.getVacancy()));

        // Hide vacancy if capacity is incompatible (vacancy is -1)
        if(shelter.getVacancy() == -1) {
            vacancyTextView.setVisibility(View.INVISIBLE);
        }

        // Hide reservation controls if capacity is incompatible (vacancy is -1) or if user can't reserve new beds
        if((shelter.getVacancy() == -1) || !canReserve) {
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
                if((reservedBeds <= shelter.getVacancy()) && (reservedBeds >= 1)) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("KEY", shelter.getUniqueKey());
                    resultIntent.putExtra("RESERVEDBEDS", reservedBeds);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast toast = Toast.makeText(ShelterDetailActivity.this, "Please select a valid amount of beds!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
}
