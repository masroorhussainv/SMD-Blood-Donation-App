package com.masroor.blooddonationapp.donor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.masroor.blooddonationapp.R;
import com.masroor.blooddonationapp.Strs;

public class DonorMainActivity extends AppCompatActivity {

    TextView textViewName,textViewBloodType,textViewCity;
    Button btnViewRequests;

    String donor_name,donor_blood_type,donor_city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_main);

        referViewElements();

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

    private void populateViewElements() {
        textViewName.setText(donor_name);
        textViewBloodType.setText(donor_blood_type);
        textViewCity.setText(donor_city);
    }

    private void referViewElements() {
        textViewName=findViewById(R.id.textview_donor_name);
        textViewBloodType=findViewById(R.id.textview_blood_type);
        textViewCity=findViewById(R.id.textview_city);
        btnViewRequests=findViewById(R.id.button_view_requests);
    }
}
