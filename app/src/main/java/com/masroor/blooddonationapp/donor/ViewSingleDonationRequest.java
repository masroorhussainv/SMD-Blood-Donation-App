package com.masroor.blooddonationapp.donor;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mapzen.speakerbox.Speakerbox;
import com.masroor.blooddonationapp.R;
import com.masroor.blooddonationapp.Strs;
import com.masroor.blooddonationapp.app.AnalyticsApplication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ViewSingleDonationRequest extends AppCompatActivity implements View.OnClickListener {

    Tracker mTracker;
    private String activityName="Individual Donation Request Activity";

    CallbackManager callbackManager;
    ShareDialog shareDialog;
    Activity activity=this;

    double longitude,latitude;      //to be passed to 'Get Directions' activity
    String  location_name,blood_type,request_message,request_city;

    boolean urg;
    TextView textViewUrgent,textViewLocationName,textViewBloodType,textViewMessage;
    Button btnGetDirections;
    ImageView speakMessage, facebookShare;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void generateHashKey(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.masroor.blooddonationapp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("nope","nope");

        } catch (NoSuchAlgorithmException e) {
            Log.d("nope","nope");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_donation_request);

        setGA_Tracker();

        referViewElements();
        extractIntentData();
        populateViewElements();

//        ShareLinkContent content = new ShareLinkContent.Builder()
//                .setContentUrl(Uri.parse("https://developers.facebook.com"))
//                .build();
//        ShareApi.share(content,null);



        generateHashKey();

        //attach listeners to appropriate views

        //text to speech
        speakMessage.setOnClickListener(this);

        //facebook share button
        facebookShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(),"share btn clicked",Toast.LENGTH_SHORT).show();

                callbackManager = CallbackManager.Factory.create();
                shareDialog = new ShareDialog(activity);


                // this part is optional
//                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
//
//                    @Override
//                    public void onSuccess(Sharer.Result result) {
//                        Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        Toast.makeText(getApplicationContext(),"cancelled",Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onError(FacebookException error) {
//                        Toast.makeText(getApplicationContext(),"error"+error.getMessage(),Toast.LENGTH_SHORT).show();
//                    }
//                });

                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                            .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                            .setQuote(blood_type+" blood donation needed at "+location_name+", "+request_city)
                            .build();
                    shareDialog.show(linkContent);
                }
            }
        });


        logGetDirectionsEvent();


        //launch get directions activity
        //pass admin location data to it
        btnGetDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),GetDirectionsActivity.class);
                    intent.putExtra(Strs.ADMIN_LOCATION_LATITUDE,latitude);
                    intent.putExtra(Strs.ADMIN_LOCATION_LONGITUDE,longitude);
                    intent.putExtra(Strs.ADMIN_LOCATION_NAME,location_name);
                    //launch get directions activity
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
        facebookShare =findViewById(R.id.button_facebook_share);
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
        request_city=getIntent().getExtras().getString(Strs.ADMIN_LOCATION_CITY);
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
