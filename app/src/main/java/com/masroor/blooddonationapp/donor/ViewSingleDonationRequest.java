package com.masroor.blooddonationapp.donor;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mapzen.speakerbox.Speakerbox;
import com.masroor.blooddonationapp.R;
import com.masroor.blooddonationapp.Strs;
import com.masroor.blooddonationapp.app.AnalyticsApplication;

public class ViewSingleDonationRequest extends AppCompatActivity implements View.OnClickListener {

    Tracker mTracker;
    private String activityName="Individual Donation Request Activity";

    Activity activity=this;

    double longitude,latitude;      //to be passed to 'Get Directions' activity
    String  location_name,blood_type,request_message;

    boolean urg;
    TextView textViewUrgent,textViewLocationName,textViewBloodType,textViewMessage;
    Button btnGetDirections;
    ImageView speakMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_donation_request);

        setGA_Tracker();

        referViewElements();
        extractIntentData();
        populateViewElements();

        //attach listeners to appropriate views

        //text to speech
        speakMessage.setOnClickListener(this);

        //launch get directions activity
        //pass admin location data to it
        btnGetDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),GetDirectionsActivity.class);
                    intent.putExtra(Strs.ADMIN_LOCATION_LATITUDE,latitude);
                    intent.putExtra(Strs.ADMIN_LOCATION_LONGITUDE,longitude);
                    intent.putExtra(Strs.ADMIN_LOCATION_NAME,location_name);

                    logGetDirectionsEvent();

                startActivity(intent);
            }
        });
    }

    private void referViewElements() {
        textViewLocationName = findViewById(R.id.textview_request_location_name);
        textViewBloodType = findViewById(R.id.textview_blood_group);
        textViewMessage = findViewById(R.id.textview_message);
        textViewUrgent = findViewById(R.id.textview_urgent);
        speakMessage = findViewById(R.id.button_speak_message);
        btnGetDirections=findViewById(R.id.button_get_directions);
    }

    public void extractIntentData(){
        //admin's location
        //this is to be passed to 'Get Directions' activity
        longitude=getIntent().getExtras().getDouble(Strs.ADMIN_LOCATION_LONGITUDE);
        latitude=getIntent().getExtras().getDouble(Strs.ADMIN_LOCATION_LATITUDE);
        //other details
        location_name=getIntent().getExtras().getString(Strs.ADMIN_LOCATION_NAME);
        blood_type=getIntent().getExtras().getString(Strs.REQUEST_BLOOD_TYPE);
        request_message=getIntent().getExtras().getString(Strs.REQUEST_MESSAGE);
        urg=getIntent().getExtras().getBoolean(Strs.REQUEST_URGENT);
    }

    private void populateViewElements() {
        textViewLocationName.setText(location_name);
        textViewBloodType.setText(blood_type);
        textViewMessage.setText(request_message);
        if(!urg){
            textViewUrgent.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int pressed_id=v.getId();

        switch (pressed_id){

            case R.id.button_speak_message:{
                Toast.makeText(
                        getApplicationContext(),
                        "Speaking!", Toast.LENGTH_SHORT).show();
                Speakerbox speakerbox = new Speakerbox(activity.getApplication());
                speakerbox.play(request_message);

                logTextToSpeechEvent();

            }break;
        }
    }

    private void setGA_Tracker() {
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("google analytics event", "Setting screen name: " + activityName);
        mTracker.setScreenName("activity: " + activityName);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Donor Viewed Individual Donation Request").build());
    }

    public void logGetDirectionsEvent(){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Get Directions Action")
                .setAction("Donor Pressed Get Direction Button").build());
    }

    public void logTextToSpeechEvent(){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Text to Speech")
                .setAction("Donor Pressed Text to Speech button").build());
    }

}
