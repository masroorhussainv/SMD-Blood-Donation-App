package com.masroor.blooddonationapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.masroor.blooddonationapp.app.AnalyticsApplication;
import com.masroor.blooddonationapp.donor.DonorMainActivity;
import com.masroor.blooddonationapp.donor.GetDonorDetailsActivity;
import com.masroor.blooddonationapp.model.AdminLocationModel;

import java.util.Arrays;

//import admin.AdminMainActivity;

public class MainActivity extends AppCompatActivity {

    Tracker mTracker;

    public static final String ADMIN_LOGGED_IN = "ADMIN_LOGGED_IN";
    final int RC_FIREBASE_UI_FLOW=111;
    Button btnLoginSignup;
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLoginSignup=findViewById(R.id.button_signin);

        checkGA_TrackingID();
        setGA_Tracker();

        //if signed in
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            launchSignedInActivity();
        }


        //attach listener to the signin button
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
            btnLoginSignup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //if not signed in
                    launchFirebaseUIFlow();
                }
            });
    }

    private void launchFirebaseUIFlow() {

        startActivityForResult(
                AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build()
                )).build(),
                RC_FIREBASE_UI_FLOW
        );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case RC_FIREBASE_UI_FLOW:{

                if(resultCode== Activity.RESULT_OK){
                    //signed in
                    launchSignedInActivity();
                }
            }break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    public void setSharedPrefAdminLoggedIn(boolean b){
        SharedPreferences.Editor editor=getSharedPreferences(ADMIN_LOGGED_IN,MODE_PRIVATE).edit();
        editor.putBoolean(ADMIN_LOGGED_IN,b);
        editor.apply();
    }

    public boolean getSharedPrefAdminLoggedIn(){
        SharedPreferences sp=getSharedPreferences(ADMIN_LOGGED_IN,MODE_PRIVATE);
        return sp.getBoolean(ADMIN_LOGGED_IN,false);
    }


    protected void launchSignedInActivity() {

        dialog = ProgressDialog.show(this, "",
                "Signing in. Please wait...", true);

        //check if current signed in user is an admin
        DatabaseReference dbRef_Admin_Locations=FirebaseDatabase.getInstance().getReference()
                .child(Strs.ADMIN_LOCATIONS_ROOT)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        dbRef_Admin_Locations
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //check current user's role type

                        if(dataSnapshot.exists()){  //admin

                            // current user is an admin
                            Log.i("Admin data:",dataSnapshot.toString());

                            //extract details
                            AdminLocationModel loc=dataSnapshot.getValue(AdminLocationModel.class);

                            //launch the admin activity
                            Intent i=new Intent(getApplicationContext(), com.masroor.blooddonationapp.admin.AdminMainActivity.class);
                            //put data
                            i.putExtra(Strs.ADMIN_LOCATION_NAME,loc.getLocation_name());
                            i.putExtra(Strs.ADMIN_LOCATION_LONGITUDE,loc.getLocation_longitude());
                            i.putExtra(Strs.ADMIN_LOCATION_LATITUDE,loc.getLocation_latitude());
                            i.putExtra(Strs.ADMIN_LOCATION_CITY,loc.getLocation_city());

                            dialog.dismiss();

                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();
                        }else{
                            //current user is not and admin
                            //is a donor role user

                            launchDonorActivity();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void launchDonorActivity(){

        final ConstraintLayout cl=findViewById(R.id.constraintLayout);

        Snackbar.make(cl,"Donor Signin success!",Snackbar.LENGTH_SHORT).show();

        final DatabaseReference dbRef_donors=FirebaseDatabase.getInstance().getReference()
                .child(Strs.DONORS_ROOT)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        dbRef_donors.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //check if this user already exists in the Donors node
                Log.i("donor",dataSnapshot.toString());

                if(dataSnapshot.exists()){
                    //this uid exists
                    Toast.makeText(getApplicationContext(),
                            "This donor exists in database!", Toast.LENGTH_SHORT).show();

                    //direct to main donor activity
                    Intent i=new Intent(getApplicationContext(),DonorMainActivity.class);

                    Log.i("datax",dataSnapshot.child(Strs.DONOR_CITY).getValue(String.class));
                    Log.i("datax",dataSnapshot.child(Strs.DONOR_BLOOD_TYPE).getValue(String.class));

                    String city=dataSnapshot.child(Strs.DONOR_CITY).getValue(String.class);
                    String bloodtype=dataSnapshot.child(Strs.DONOR_BLOOD_TYPE).getValue(String.class);

                    i.putExtra(Strs.DONOR_CITY,city);
                    i.putExtra(Strs.DONOR_BLOOD_TYPE,bloodtype);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    dialog.dismiss();

                    logAdminSignInEvent();

                    startActivity(i);
                    finish();

                }else{
                    //add this uid to db
                    Intent i=new Intent(getApplicationContext(), GetDonorDetailsActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    logDonorSignInEvent();

                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

////    public void launchAdminMainActivity(){
////        //check if this admin's uid exists in db
////        //at
////        //Admins
////        //  -Hospital_admin_uid
////        DatabaseReference dbRef_Admins= FirebaseDatabase.getInstance().getReference()
////                .child(Strs.ADMIN_LOCATIONS_ROOT)
////                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
////
////        dbRef_Admins.addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                AdminLocationModel loc;
////
////                Log.i("location",dataSnapshot.getKey());
////                loc=dataSnapshot.getValue(AdminLocationModel.class);
//////                Log.i("location",
//////                        ""+loc.getLocation_name()+
//////                                loc.getLocation_longitude()+
//////                                loc.getLocation_latitude()
//////                );
////
////                //put this data into intent
////                Intent i=new Intent(getApplicationContext(), com.masroor.blooddonationapp.admin.AdminMainActivity.class);
////                //put data
////                i.putExtra(Strs.ADMIN_LOCATION_NAME,loc.getLocation_name());
////                i.putExtra(Strs.ADMIN_LOCATION_LONGITUDE,loc.getLocation_longitude());
////                i.putExtra(Strs.ADMIN_LOCATION_LATITUDE,loc.getLocation_latitude());
////                i.putExtra(Strs.ADMIN_LOCATION_CITY,loc.getLocation_city());
////                startActivity(i);
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////            }
////        });
//    }

    public void checkGA_TrackingID(){
        if (!checkConfiguration()) {
            View contentView = findViewById(android.R.id.content);
            Snackbar.make(contentView, R.string.bad_config, Snackbar.LENGTH_INDEFINITE).show();
        }
    }
    private boolean checkConfiguration() {
        XmlResourceParser parser = getResources().getXml(R.xml.global_tracker);

        boolean foundTag = false;
        try {
            while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlResourceParser.START_TAG) {
                    String tagName = parser.getName();
                    String nameAttr = parser.getAttributeValue(null, "name");

                    foundTag = "string".equals(tagName) && "ga_trackingId".equals(nameAttr);
                }

                if (parser.getEventType() == XmlResourceParser.TEXT) {
                    if (foundTag && parser.getText().contains("REPLACE_ME")) {
                        return false;
                    }
                }

                parser.next();
            }
        } catch (Exception e) {
            Log.w("checkConfiguration", "checkConfiguration", e);
            return false;
        }

        return true;
    }

    private void setGA_Tracker() {
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    public void logAdminSignInEvent(){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("SignIn")
                .setAction("Admin Signed In").build());
    }

    public void logDonorSignInEvent(){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("SignIn")
                .setAction("Donor Signed In").build());
    }

}
