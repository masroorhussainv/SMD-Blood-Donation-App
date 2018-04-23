package com.masroor.admin;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.masroor.R;
import com.masroor.blooddonationapp.Strs;
import com.masroor.model.AdminLocationModel;

public class AdminMainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String LOCATION_DATA_BUNDLE = "LOCATION_BUNDLE";
    Bundle bundleForPostAReq;
    Button btnMakeReq,btnManageReq;

    private AdminLocationModel loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        btnMakeReq=findViewById(R.id.button_make_donation_request);
        btnManageReq=findViewById(R.id.button_manage_requests);

        //receive the location data as a bundle and fwd it to post doantion req activity
        bundleForPostAReq=getIntent().getExtras();
        Log.i("admin main bundle",bundleForPostAReq.getString(Strs.ADMIN_LOCATION_NAME));

        //attach listeners to the buttons
        btnMakeReq.setOnClickListener(this);
        btnManageReq.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View v) {
        int clicked_id=v.getId();

        switch (clicked_id){
            case R.id.button_make_donation_request:{

                //pass the data of hospital location
                // i.e
                //the site where this admin is located
                Intent i=new Intent(this,MakeDonationRequest.class);
                //put individual values into this intent
                i.putExtra(Strs.ADMIN_LOCATION_NAME,bundleForPostAReq.getString(Strs.ADMIN_LOCATION_NAME));
                i.putExtra(Strs.ADMIN_LOCATION_LONGITUDE,bundleForPostAReq.getDouble(Strs.ADMIN_LOCATION_LONGITUDE));
                i.putExtra(Strs.ADMIN_LOCATION_LATITUDE,bundleForPostAReq.getDouble(Strs.ADMIN_LOCATION_LONGITUDE));
                startActivity(i);
            }break;

            case R.id.button_manage_requests:{

            }break;
        }
    }
}
