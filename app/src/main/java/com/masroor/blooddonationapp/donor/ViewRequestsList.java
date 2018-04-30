package com.masroor.blooddonationapp.donor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.masroor.blooddonationapp.R;
import com.masroor.blooddonationapp.Strs;
import com.masroor.blooddonationapp.app.AnalyticsApplication;
import com.masroor.blooddonationapp.model.DonationRequestModel;
import com.masroor.blooddonationapp.viewholder.DonationRequestViewHolder;

public class ViewRequestsList extends AppCompatActivity {

    Tracker mTracker;
    private String activityName="Donor View Requests Activity";

    //for fetching and displaying requests list
    FirebaseRecyclerAdapter adapter;
    RecyclerView recyclerView_requests;
    String city_name_for_requests;
    Query query_dbRef_city_reqs;


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests_list);

        setGA_Tracker();

        recyclerView_requests=findViewById(R.id.recyclerview_requests_list);
        city_name_for_requests=getIntent().getExtras().getString(Strs.DONOR_CITY);

        query_dbRef_city_reqs=
                FirebaseDatabase.getInstance().getReference()
                .child(Strs.CITY_REQUESTS_ROOT)
                .child(city_name_for_requests);

        FirebaseRecyclerOptions<DonationRequestModel> options =
                new FirebaseRecyclerOptions.Builder<DonationRequestModel>()
                        .setQuery(query_dbRef_city_reqs, DonationRequestModel.class)
                        .build();

        adapter=new FirebaseRecyclerAdapter<DonationRequestModel,DonationRequestViewHolder>(options) {

            @NonNull
            @Override
            public DonationRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.itemview_donation_request,parent,false);
                return new DonationRequestViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull DonationRequestViewHolder holder, int position, @NonNull final DonationRequestModel model) {
                holder.populateDonationRequestViewholder(
                        model.getRequest_location().getLocation_name(),
                        model.getBlood_type(),
                        model.isUrgent(),
                        model.getMessage()
                );

                //attach a listener to itemview_donation_request
                //which will open the individual donation request screen

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //launch manage request activity in case of admin
                        Intent i=new Intent(getApplicationContext(),ViewSingleDonationRequest.class);

                        //pass req info to be displayed
                        i.putExtra(Strs.ADMIN_LOCATION_NAME,model.getRequest_location().getLocation_name());
                        i.putExtra(Strs.REQUEST_BLOOD_TYPE,model.getBlood_type());
                        i.putExtra(Strs.REQUEST_URGENT,model.isUrgent());
                        i.putExtra(Strs.REQUEST_MESSAGE,model.getMessage());

                        //pass the longitude and latitude info so that user can get directions to the admin_Location
                        i.putExtra(
                                Strs.ADMIN_LOCATION_LONGITUDE,
                                model.getRequest_location().getLocation_longitude());

                        i.putExtra(Strs.ADMIN_LOCATION_LATITUDE,
                                model.getRequest_location().getLocation_latitude());

                        startActivity(i);
                    }
                });
            }
        };
        recyclerView_requests.setAdapter(adapter);
        recyclerView_requests.setLayoutManager(new LinearLayoutManager(this));

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
                .setAction("Donor Viewed List of Donation Requests").build());
    }
}
