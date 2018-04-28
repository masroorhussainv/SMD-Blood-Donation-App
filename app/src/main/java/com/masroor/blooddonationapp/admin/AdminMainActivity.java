package com.masroor.blooddonationapp.admin;

import android.app.Activity;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.masroor.blooddonationapp.R;
import com.masroor.blooddonationapp.Strs;
import com.masroor.blooddonationapp.model.AdminLocationModel;

public class AdminMainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int RC_POST_DONATION_REQUEST = 123;
    AdView adView;
    Bundle bundleForPostAReq;
    Button btnMakeReq,btnManageReq;

    String location_name,location_city;
    double location_longitude,location_latitude;

    private AdminLocationModel loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        referViewElements();
        initializeAdMob();
        loadAd();

        //receive the location data as a bundle and fwd it to post doantion req activity
        bundleForPostAReq=getIntent().getExtras();
        Log.i("admin main bundle",bundleForPostAReq.getString(Strs.ADMIN_LOCATION_NAME));
        location_name=bundleForPostAReq.getString(Strs.ADMIN_LOCATION_NAME);
        location_longitude=bundleForPostAReq.getDouble(Strs.ADMIN_LOCATION_LONGITUDE);
        location_latitude=bundleForPostAReq.getDouble(Strs.ADMIN_LOCATION_LATITUDE);
        location_city=bundleForPostAReq.getString(Strs.ADMIN_LOCATION_CITY);

        //attach listeners to the buttons
        btnMakeReq.setOnClickListener(this);
        btnManageReq.setOnClickListener(this);
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

    private void referViewElements() {
        btnMakeReq=findViewById(R.id.button_make_donation_request);
        btnManageReq=findViewById(R.id.button_manage_requests);
        adView=findViewById(R.id.adView);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        int clicked_id=v.getId();

        switch (clicked_id){
            case R.id.button_make_donation_request:{

                //pass the data of hospital location
                // i.e
                //the site where this admin is located
                Intent i=new Intent(this,MakeDonationRequest.class);
                //put individual values into this intent
                i.putExtra(Strs.ADMIN_LOCATION_NAME,location_name);
                i.putExtra(Strs.ADMIN_LOCATION_LONGITUDE,location_longitude);
                i.putExtra(Strs.ADMIN_LOCATION_LATITUDE,location_latitude);
                i.putExtra(Strs.ADMIN_LOCATION_CITY,location_city);

                startActivityForResult(i, RC_POST_DONATION_REQUEST);
            }break;

            case R.id.button_manage_requests:{
                Intent i=new Intent(this,ManagePostedDonationRequests.class);
                i.putExtra(Strs.ADMIN_LOCATION_NAME,location_name);
                i.putExtra(Strs.ADMIN_LOCATION_LONGITUDE,location_longitude);
                i.putExtra(Strs.ADMIN_LOCATION_LATITUDE,location_latitude);
                i.putExtra(Strs.ADMIN_LOCATION_CITY,location_city);
                startActivity(i);
            }break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        ConstraintLayout constraintLayout=findViewById(R.id.cl);

        switch (requestCode){
            case RC_POST_DONATION_REQUEST:{
                if(resultCode== Activity.RESULT_OK){
                    //request posted to db
                    Snackbar.make(constraintLayout,
                            "Request posted successfully!",
                            Snackbar.LENGTH_SHORT).show();
                }
            }break;
        }
    }
}
