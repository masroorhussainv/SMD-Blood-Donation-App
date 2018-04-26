package com.masroor.admin;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.masroor.R;
import com.masroor.blooddonationapp.Strs;
import com.masroor.model.DonationRequestModel;
import com.masroor.viewholder.DonationRequestViewHolder;

public class ManagePostedDonationRequests extends AppCompatActivity {


    public static final String REQUEST_ID ="request_id";
    public static final String REQUEST_CITY = "request_city";
    public static final String REQUEST_LOC_ID = "request_loc_id";

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter adapter;

    private String admin_location_city,admin_location_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_posted_requests);

        recyclerView=findViewById(R.id.recyclerview);

        admin_location_city=getIntent().getExtras().getString(Strs.ADMIN_LOCATION_CITY);
        admin_location_id= FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query query= FirebaseDatabase.getInstance().getReference()
                .child(Strs.REQUESTS_ROOT)
                .child(admin_location_city)
                .child(admin_location_id);

        FirebaseRecyclerOptions<DonationRequestModel> options=
                new FirebaseRecyclerOptions.Builder<DonationRequestModel>()
                        .setQuery(query,DonationRequestModel.class)
                        .build();

        adapter=new FirebaseRecyclerAdapter<DonationRequestModel,DonationRequestViewHolder>(options){

            @Override
            protected void onBindViewHolder(@NonNull DonationRequestViewHolder holder, int position, @NonNull final DonationRequestModel model) {
//                Log.i("request model received",model.toString());     //might crash at this line
                Log.i("model position",""+position);

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
                    Intent i=new Intent(getApplicationContext(),ManageSingleDonationRequest.class);
                    //pass it the path of this request
//                    String req_id= FirebaseDatabase.getInstance().getReference()
//                            .child(Strs.REQUESTS_ROOT)
//                            .child(admin_location_city)
//                            .child(admin_location_id)
//                            .child(model.getDonation_request_id())
//                            .toString();
                    String req_id=model.getDonation_request_id();
                    String req_city=model.getRequest_location().getLocation_city();
                    String req_loc_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    //pass req path related info for deleting donation req
                    i.putExtra(REQUEST_ID,req_id);
                    i.putExtra(REQUEST_CITY,req_city);
                    i.putExtra(REQUEST_LOC_ID,req_loc_id);
                    //pass req info to be displayed
                    i.putExtra(Strs.ADMIN_LOCATION_NAME,model.getRequest_location().getLocation_name());
                    i.putExtra(Strs.REQUEST_BLOOD_TYPE,model.getBlood_type());
                    i.putExtra(Strs.REQUEST_URGENT,model.isUrgent());
                    i.putExtra(Strs.REQUEST_MESSAGE,model.getMessage());
                    startActivity(i);
                    }
                });
            }

            @NonNull
            @Override
            public DonationRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.itemview_donation_request,parent,false);
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