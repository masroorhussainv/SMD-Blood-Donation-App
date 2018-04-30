package com.masroor.blooddonationapp.admin;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.masroor.blooddonationapp.R;
import com.masroor.blooddonationapp.Strs;
import com.masroor.blooddonationapp.app.AnalyticsApplication;
import com.masroor.blooddonationapp.model.AdminLocationModel;
import com.masroor.blooddonationapp.model.DonationRequestModel;

public class MakeDonationRequest extends AppCompatActivity {

    Tracker mTracker;
    private String activityName="";
    public static final String DONATION_REQUEST_ADD_EVENT = "DONATION_REQUEST_ADD_EVENT";
    FirebaseAnalytics firebaseAnalytics;        //for logging custom event of posting a donation request

    final DatabaseReference dbRef_Requests= FirebaseDatabase
            .getInstance()
            .getReference()
            .child(Strs.REQUESTS_ROOT);

    final DatabaseReference dbRef_City_Requests=FirebaseDatabase
            .getInstance()
            .getReference()
            .child(Strs.CITY_REQUESTS_ROOT);

    String[] blood_types= {
            "O-","O+",
            "A-","A+",
            "B-","B+",
            "AB-","AB+"
    };

    Spinner spinnerBloodGroup;
    Switch switchUrgent;
    EditText editTextNeededByDate,editTextRequestMessage;
    Button btnPostRequest;
    ProgressBar progressBar;

    boolean urgent;
    String neededByDate,requestMessage,bloodGroup;
    AdminLocationModel loc=new AdminLocationModel();
    //use all the above varibles to construct this Donation request model
    DonationRequestModel donation_request;


    private void setGA_Tracker() {
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    public void logDonationRequestPostedEvent(){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Admin Posted Donation request").build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_make_donation_request);

        setGA_Tracker();


        firebaseAnalytics=FirebaseAnalytics.getInstance(this);
        referViewElements();
        progressBar.setVisibility(View.INVISIBLE);
        //populate spinner view
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,blood_types);
        spinnerBloodGroup.setAdapter(arrayAdapter);

        //extract values for admin location model
        final Bundle locationBundle=getIntent().getExtras();
//        assert locationBundle != null;
//        AdminLocationModel adminLocationModel=new AdminLocationModel(
//                locationBundle.getDouble(Strs.ADMIN_LOCATION_LONGITUDE),
//                locationBundle.getDouble(Strs.ADMIN_LOCATION_LATITUDE),
//                locationBundle.getString(Strs.ADMIN_LOCATION_NAME),
//                locationBundle.getString(Strs.ADMIN_LOCATION_CITY)
//        );


        // listener for 'Post Donation Req' button
        btnPostRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  1- Validate the input
                //  2- Initialize the Request data model object by putting values into it
                //  3- push the request into firebase db

                if (validateInput()){
                    prepareDonationRequestDataModel();
                    Log.i("complete req data: ",donation_request.toString());

                    progressBar.setVisibility(View.VISIBLE);
                    //pushing the request model into firebase db

                    //Requests
                    //      -City
                    //          -location_uid
                    //                      -req_id         =====this one is being generated uniquely
                    //                            -DonationRequestModel

                    //generate a unique for each request entry
                    //by concatinating admin_loc_id + firebase_generated_key
                    final String concatenated_request_id=FirebaseAuth.getInstance().getCurrentUser().getUid()+
                            dbRef_Requests
                            .child(donation_request.getRequest_location().getLocation_city())
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .push().getKey();

                    Log.i("key",concatenated_request_id);

                    DatabaseReference ref=FirebaseDatabase.getInstance()
                            .getReference(
                                Strs.REQUESTS_ROOT+"/"+
                                donation_request.getRequest_location().getLocation_city()+"/"+
                                FirebaseAuth.getInstance().getCurrentUser().getUid()+"/"+
                                concatenated_request_id+"/"
                            );

//                    dbRef_Requests
//                        .child(donation_request.getRequest_location().getLocation_city())
//                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                        +concatenated_request_id
//                        .setValue(donation_request)     //pushes complete donation req object to db

                    //pushes complete donation req object to db

                    //have to write the req_id in donation model
                    //it is needed
                    donation_request.setDonation_request_id(concatenated_request_id);

                    ref.setValue(donation_request)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pushToCityWiseRequestsNode(concatenated_request_id);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Request could not be posted!",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }

        });

        Bundle bundle=getIntent().getExtras();
        //initialize the admin location object's data for using into 'Post a Donation Request'
        loc.setLocation_name(bundle.getString(Strs.ADMIN_LOCATION_NAME));
        loc.setLocation_longitude(bundle.getDouble(Strs.ADMIN_LOCATION_LONGITUDE));
        loc.setLocation_latitude(bundle.getDouble(Strs.ADMIN_LOCATION_LATITUDE));
        loc.setLocation_city(bundle.getString(Strs.ADMIN_LOCATION_CITY));
    }

    private void referViewElements() {
        spinnerBloodGroup=findViewById(R.id.spinner_blood_groups);
        switchUrgent=findViewById(R.id.switch_urgent);
        editTextNeededByDate=findViewById(R.id.edittext_needed_by_date);
        editTextRequestMessage=findViewById(R.id.edittext_request_message);
        btnPostRequest=findViewById(R.id.button_post_request);
        progressBar=findViewById(R.id.progressbar);
    }

    private void pushToCityWiseRequestsNode(String request_id) {

        dbRef_City_Requests
            .child(donation_request.getRequest_location().getLocation_city())
                .child(request_id)
                .setValue(donation_request)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            setResult(Activity.RESULT_OK);

                            //log a DONATION REQUEST ADDED EVENT to firebase analytics
                            Bundle params=new Bundle();
                            params.putString("REQUEST_MADE_FROM",donation_request.getRequest_location().getLocation_city());
                            params.putString("REQUEST_BLOOD_TYPE",donation_request.getBlood_type());
                            firebaseAnalytics.logEvent(DONATION_REQUEST_ADD_EVENT,params);
                            firebaseAnalytics.setAnalyticsCollectionEnabled(true);

                            logDonationRequestPostedEvent();

                            progressBar.setVisibility(View.INVISIBLE);
                            finish();
                        }else{
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Request could not be posted!",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void prepareDonationRequestDataModel() {
        bloodGroup=spinnerBloodGroup.getSelectedItem().toString();
        urgent = switchUrgent.isChecked();
        neededByDate=editTextNeededByDate.getText().toString();
        requestMessage=editTextRequestMessage.getText().toString();
        //construct the donation request model
        donation_request=new DonationRequestModel(loc,bloodGroup,neededByDate,requestMessage,urgent);
    }

    public boolean validateInput(){
        String str=spinnerBloodGroup.getSelectedItem().toString();
        if(str==null){
            return false;
        }
        //check date
        if(TextUtils.isEmpty(editTextNeededByDate.getText())){
            editTextNeededByDate.setError("Enter Date");
            return false;
        }else{
            editTextNeededByDate.setError(null);
        }
        //check request message
        if(TextUtils.isEmpty(editTextRequestMessage.getText())){
            editTextRequestMessage.setError("Enter Your Message for Request");
            return false;
        }else{
            editTextRequestMessage.setError(null);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_options_menu, menu);
        return true;
    }

}
