package com.masroor.blooddonationapp.donor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.masroor.blooddonationapp.R;
import com.masroor.blooddonationapp.Strs;
import com.masroor.blooddonationapp.model.DonorModel;

import java.util.ArrayList;
import java.util.Map;

public class GetDonorDetailsActivity extends AppCompatActivity {

    String[] blood_types= {
            "O-","O+",
            "A-","A+",
            "B-","B+",
            "AB-","AB+"
    };
    Map<String,String> cities_map;      //data retrievd in map
    ArrayList<String> cities_list=new ArrayList<>();      //then populated in arraylist for adapter
    ArrayAdapter<String> cities_adapter;

    Spinner spinnerBloodType,spinnerCity;
    TextView textViewName;
    Button btnSubmitInfo;

    DatabaseReference getDbRef_cities=FirebaseDatabase.getInstance().getReference()
            .child(Strs.CITIES_ROOT);

    DatabaseReference dbRef_donors= FirebaseDatabase.getInstance().getReference()
            .child(Strs.DONORS_ROOT);

    //for donor data model
    String donor_uid,name,blood_type,city;
    DonorModel donor_model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_donor_details);

        referViewElements();
        textViewName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        populateBloodTypesSpinner();
        populateCitiesSpinner();
    }

    public void uploadDonorInfo(){
        dbRef_donors
                .child(donor_uid)
                .setValue(donor_model)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(
                                    getApplicationContext(),
                                    "Donor Info Updated Successfully!",
                                    Toast.LENGTH_LONG).show();

                            //direct to main donor activity
                            Intent i=new Intent(getApplicationContext(),DonorMainActivity.class);
                            //put the data needed
                            Log.i(Strs.DONOR_CITY,donor_model.getDonor_city());
                            Log.i(Strs.DONOR_BLOOD_TYPE,donor_model.getDonor_blood_type());

                            i.putExtra(Strs.DONOR_CITY,donor_model.getDonor_city());
                            i.putExtra(Strs.DONOR_BLOOD_TYPE,donor_model.getDonor_blood_type());

                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();

                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            finish();

                        }else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "error in updating donor info!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private boolean validateInfo() {
        return !TextUtils.isEmpty(spinnerCity.getSelectedItem().toString()) &&
                !TextUtils.isEmpty(spinnerBloodType.getSelectedItem().toString());
    }

    private void initializeInfoModdel() {
        donor_uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        name=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        blood_type=spinnerBloodType.getSelectedItem().toString();
        city=spinnerCity.getSelectedItem().toString();
        donor_model=new DonorModel(donor_uid,name,blood_type,city);
    }


    public void referViewElements(){
        textViewName=findViewById(R.id.textview_donor_name);
        spinnerBloodType=findViewById(R.id.spinner_blood_group);
        spinnerCity=findViewById(R.id.spinner_city);
        btnSubmitInfo=findViewById(R.id.button_submit);
    }

    public void populateBloodTypesSpinner(){
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,blood_types);
        spinnerBloodType.setAdapter(adapter);
    }

    public void populateCitiesSpinner(){
        getDbRef_cities
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            //get data from cities node
                            cities_map=(Map<String, String>) dataSnapshot.getValue();
                            //put it in arraylist so that adapter can use it
                            cities_list.addAll(cities_map.keySet());
                            ArrayAdapter<String> adapter=new ArrayAdapter<>(
                                    getApplicationContext(),
                                    android.R.layout.simple_spinner_item,cities_list
                            );
                            spinnerCity.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            //now make the button clickable by setting the event on it
                            btnSubmitInfo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(validateInfo()){
                                        initializeInfoModdel();
                                        uploadDonorInfo();
                                    }
                                }
                            });
                            Log.i("cities",cities_map.toString());
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
