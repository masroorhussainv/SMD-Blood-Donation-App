package com.masroor.blooddonationapp.donor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.auth.FirebaseAuth;
import com.masroor.blooddonationapp.R;
import com.masroor.blooddonationapp.Strs;
import com.masroor.blooddonationapp.app.AnalyticsApplication;

import static com.masroor.blooddonationapp.admin.AdminMainActivity.RC_POST_DONATION_REQUEST;
import static com.masroor.blooddonationapp.admin.AdminMainActivity.REQUEST_INVITE;

public class DonorMainActivity extends AppCompatActivity {

    Tracker mTracker;
    private String activityName="Donor Main Activity";
    AdView adView;

    TextView textViewName,textViewBloodType,textViewCity;
    Button btnViewRequests;

    String donor_name,donor_blood_type,donor_city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_main);

        setGA_Tracker();

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

    private void setGA_Tracker() {
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
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
        Log.i("google analytics event", "Setting screen name: " + activityName);
        mTracker.setScreenName("activity: " + activityName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Donor opened DonorMainActivity").build());

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.donor_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.backup:{

                return true;
            }
            case R.id.invite:{
                invite();
                return true;
            }
            case R.id.feedback:{
                Intent i=new Intent(this,DonorFeedbackActivity.class);
                startActivity(i);
            }break;
            case R.id.logout:{
                logOut();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void invite() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    public void logOut(){
        ProgressDialog dialog = ProgressDialog.show(this, "",
                "Logging out. Please wait...", true);

        com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
        Intent intent=new Intent(getApplicationContext(),com.masroor.blooddonationapp.MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        vibratePhone();

        logSignOutEvent();

        startActivity(intent);
        dialog.dismiss();
        finish();
    }

    private void logSignOutEvent() {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Sign out")
                .setAction("Donor Sign Out").build());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        ConstraintLayout cl=findViewById(R.id.cl);

        switch (requestCode){
            case REQUEST_INVITE:{
                if(resultCode==RESULT_OK){
                    String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                    for (String id : ids) {
                        Snackbar.make(cl,"Invite sent successfully!",Snackbar.LENGTH_SHORT).show();
                    }
                }else{
                    Snackbar.make(cl,"No invites sent.",Snackbar.LENGTH_SHORT).show();
                }
            }break;
        }
    }

    private void vibratePhone(){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assert v != null;
            v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            //deprecated in API 26
            assert v != null;
            v.vibrate(500);
        }
    }
}
