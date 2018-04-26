package com.masroor.donor;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mapzen.speakerbox.Speakerbox;
import com.masroor.R;
import com.masroor.blooddonationapp.Strs;

public class ViewSingleDonationRequest extends AppCompatActivity implements View.OnClickListener {

    Activity activity=this;

    double longitude,latitude;
    String  location_name,blood_type,request_message;

    boolean urg;
    TextView textViewUrgent,textViewLocationName,textViewBloodType,textViewMessage;
    Button btnSpeakMessage,btnGetDirections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_donation_request);

        referViewElements();
        extractIntentData();
        populateViewElements();

        //attach listeners to appropriate views
        btnSpeakMessage.setOnClickListener(this);

    }

    private void referViewElements() {
        textViewLocationName = findViewById(R.id.textview_request_location_name);
        textViewBloodType = findViewById(R.id.textview_blood_group);
        textViewMessage = findViewById(R.id.textview_message);
        textViewUrgent = findViewById(R.id.textview_urgent);
        btnSpeakMessage = findViewById(R.id.button_speak_message);
        btnGetDirections=findViewById(R.id.button_get_directions);
    }

    public void extractIntentData(){
        //admin's location
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
            }break;
        }
    }
}
