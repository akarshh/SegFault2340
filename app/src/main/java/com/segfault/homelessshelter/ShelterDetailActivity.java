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

    Button reserveButton;
    EditText reserveText;

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

        // Get shelter from intent's extras
        final Shelter shelter = getIntent().getExtras().getParcelable("SHELTER");

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

        reserveButton = findViewById(R.id.reserveButton);
        reserveText = findViewById(R.id.reserveText);

        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int amount = Integer.parseInt(reserveText.getText().toString());
                if(amount <= shelter.getVacancy() && amount >= 1) {
                    shelter.setVacancy(shelter.getVacancy() - amount);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("KEY", shelter.getUniqueKey());
                    resultIntent.putExtra("VACANCY", shelter.getVacancy());
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(ShelterDetailActivity.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
