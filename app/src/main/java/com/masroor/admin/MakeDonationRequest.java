package com.masroor.admin;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.masroor.R;
import com.masroor.blooddonationapp.Strs;
import com.masroor.model.AdminLocationModel;
import com.masroor.model.DonationRequestModel;

public class MakeDonationRequest extends AppCompatActivity {

    final DatabaseReference dbRef_Requests= FirebaseDatabase
            .getInstance()
            .getReference()
            .child(Strs.REQUESTS_ROOT);

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_make_donation_request);

        spinnerBloodGroup=findViewById(R.id.spinner_blood_groups);
        switchUrgent=findViewById(R.id.switch_urgent);
        editTextNeededByDate=findViewById(R.id.edittext_needed_by_date);
        editTextRequestMessage=findViewById(R.id.edittext_request_message);
        btnPostRequest=findViewById(R.id.button_post_request);
        progressBar=findViewById(R.id.progressbar);

        //populate spinner view
        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,blood_types);
        spinnerBloodGroup.setAdapter(arrayAdapter);

        //extract values for admin location model
        Bundle locationBundle=getIntent().getExtras();
        assert locationBundle != null;
        AdminLocationModel adminLocationModel=new AdminLocationModel(
                locationBundle.getDouble(Strs.ADMIN_LOCATION_LONGITUDE),
                locationBundle.getDouble(Strs.ADMIN_LOCATION_LATITUDE),
                locationBundle.getString(Strs.ADMIN_LOCATION_NAME),
                locationBundle.getString(Strs.ADMIN_LOCATION_CITY)
        );


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
                    dbRef_Requests
                        .child(donation_request.getRequest_location().getLocation_city())
                        .push()         //this generates a unique key for this request
                        .setValue(donation_request)     //pushes complete donation req object to db
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                setResult(Activity.RESULT_OK);
                                progressBar.setVisibility(View.GONE);
                                finish();
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
    };
}
