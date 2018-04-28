package com.masroor.blooddonationapp.donor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.masroor.blooddonationapp.R;
import com.masroor.blooddonationapp.Strs;

public class DonorMainActivity extends AppCompatActivity {

    AdView adView;

    TextView textViewName,textViewBloodType,textViewCity;
    Button btnViewRequests;

    String donor_name,donor_blood_type,donor_city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_main);

        referViewElements();
        initializeAdMob();
        loadAd();
        //data to populate view elements
        donor_name= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        Bundle bundle=getIntent().getExtras();
        donor_city=bundle.getString(Strs.DONOR_CITY);
        donor_blood_type=bundle.getString(Strs.DONOR_BLOOD_TYPE);

        populateViewElements();

        btnViewRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),ViewRequestsList.class);
                //put location in it so that next activity can fetch data fro that city
                i.putExtra(Strs.DONOR_CITY,donor_city);
                startActivity(i);
            }
        });
    }

    private void loadAd() {
        AdRequest adRequest=new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(getString(R.string.DEVICE_ID))
                .build();
        adView.loadAd(adRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
        adView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adView.resume();
    }

    @Override
    protected void onDestroy() {
        adView.destroy();
        super.onDestroy();
    }

    private void initializeAdMob() {
        MobileAds.initialize(this,getString(R.string.ADMOB_APP_ID));
    }

    private void populateViewElements() {
        textViewName.setText(donor_name);
        textViewBloodType.setText(donor_blood_type);
        textViewCity.setText(donor_city);
        adView=findViewById(R.id.adView);
    }

    private void referViewElements() {
        textViewName=findViewById(R.id.textview_donor_name);
        textViewBloodType=findViewById(R.id.textview_blood_type);
        textViewCity=findViewById(R.id.textview_city);
        btnViewRequests=findViewById(R.id.button_view_requests);
        adView=findViewById(R.id.adView);
    }
}
