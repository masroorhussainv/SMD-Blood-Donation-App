package com.masroor.blooddonationapp.admin;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapzen.speakerbox.Speakerbox;
import com.masroor.blooddonationapp.R;
import com.masroor.blooddonationapp.Strs;

public class ManageSingleDonationRequest extends AppCompatActivity {

    DatabaseReference dbRef_city_reqs,dbRef_reqs;
    String  location_name,blood_type,request_message,
            req_id,req_city,req_loc_id;

    boolean urg;
    TextView textViewUrgent,textViewLocationName,textViewBloodType,textViewMessage;
    Button btnDeleteRequest;
    ImageView speakMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_single_donation_request);

        referViewElements();
        extractIntentData();
        populateViewElements();

        //set listeners on the views
        speakMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Speakerbox speakerbox = new Speakerbox(getApplication());
                speakerbox.play(request_message);
            }
        });

        btnDeleteRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDonationRequeust();
            }
        });
    }

    private void populateViewElements() {
        textViewLocationName.setText(location_name);
        textViewBloodType.setText(blood_type);
        textViewMessage.setText(request_message);
        if(!urg){
            textViewUrgent.setVisibility(View.INVISIBLE);
        }
    }

    private void referViewElements() {
        textViewLocationName=findViewById(R.id.textview_request_location_name);
        textViewBloodType=findViewById(R.id.textview_blood_group);
        textViewMessage=findViewById(R.id.textview_message);
        textViewUrgent=findViewById(R.id.textview_urgent);
        speakMessage=findViewById(R.id.button_speak_message);
        btnDeleteRequest=findViewById(R.id.button_delete_request);
    }

    public void extractIntentData(){
        //extract this req's path
        req_id=getIntent().getExtras().getString(ManagePostedDonationRequests.REQUEST_ID);
        req_city=getIntent().getExtras().getString(ManagePostedDonationRequests.REQUEST_CITY);
        req_loc_id=getIntent().getExtras().getString(ManagePostedDonationRequests.REQUEST_LOC_ID);

        location_name=getIntent().getExtras().getString(Strs.ADMIN_LOCATION_NAME);
        blood_type=getIntent().getExtras().getString(Strs.REQUEST_BLOOD_TYPE);
        request_message=getIntent().getExtras().getString(Strs.REQUEST_MESSAGE);
        urg=getIntent().getExtras().getBoolean(Strs.REQUEST_URGENT);
    }

    public void deleteDonationRequeust(){

        //if following func is successful
        //it will call deleteFromCityRequestsPath() in it
        deleteFromRequestsPath();
    }

    public void deleteFromRequestsPath(){
        //make reference to
        //  Requests
        dbRef_reqs=FirebaseDatabase.getInstance().getReference()
                .child(Strs.REQUESTS_ROOT)
                .child(req_city)
                .child(req_loc_id)
                .child(req_id);

        dbRef_reqs
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //deleted successfully from dbRef_reqs
                            deleteFromCityRequestsPath();
                        }
                    }
                });
    }

    public void deleteFromCityRequestsPath (){
        //make reference to
        //  City_Requests
        dbRef_city_reqs=FirebaseDatabase.getInstance().getReference()
                .child(Strs.CITY_REQUESTS_ROOT)
                .child(req_city)
                .child(req_id);
        dbRef_city_reqs
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
    }
}
