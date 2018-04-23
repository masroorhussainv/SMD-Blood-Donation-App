package com.masroor.admin;

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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.masroor.R;
import com.masroor.blooddonationapp.Strs;
import com.masroor.model.DonationRequestModel;
import com.masroor.viewholder.DonationRequestViewHolder;

public class ManagePostedDonationRequests extends AppCompatActivity {


    RecyclerView recyclerView;
    FirebaseRecyclerAdapter adapter;

    private String admin_location_city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_posted_requests);

        recyclerView=findViewById(R.id.recyclerview);

        admin_location_city=getIntent().getExtras().getString(Strs.ADMIN_LOCATION_CITY);

        Query query= FirebaseDatabase.getInstance().getReference()
                .child(Strs.REQUESTS_ROOT)
                .child(admin_location_city);

        FirebaseRecyclerOptions<DonationRequestModel> options=
                new FirebaseRecyclerOptions.Builder<DonationRequestModel>()
                        .setQuery(query,DonationRequestModel.class)
                        .build();

        adapter=new FirebaseRecyclerAdapter<DonationRequestModel,DonationRequestViewHolder>(options){

            @Override
            protected void onBindViewHolder(@NonNull DonationRequestViewHolder holder, int position, @NonNull DonationRequestModel model) {
//                Log.i("request model received",model.toString());     //might crash at this line
                Log.i("model position",""+position);

                holder.populateDonationRequestViewholder(
                        model.getRequest_location().getLocation_name(),
                        model.getBlood_type(),
                        model.isUrgent(),
                        model.getMessage());
            }

            @NonNull
            @Override
            public DonationRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.itemview_donation_request,parent,false);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //launch manage request activity in case of admin
                    }
                });

                return new DonationRequestViewHolder(v);
            }
        };

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}