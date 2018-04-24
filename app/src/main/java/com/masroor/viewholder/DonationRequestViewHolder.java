package com.masroor.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.masroor.R;

public class DonationRequestViewHolder extends RecyclerView.ViewHolder {

    View single_itemview;
    TextView textViewLocationName,textViewBloodGroup,textViewUrgent,textViewMessage;

    public DonationRequestViewHolder(View itemView) {
        super(itemView);
        single_itemview=itemView;
        textViewLocationName=itemView.findViewById(R.id.textview_request_location_name);
        textViewBloodGroup=itemView.findViewById(R.id.textview_blood_group);
        textViewUrgent=itemView.findViewById(R.id.textview_urgent);
        textViewMessage=itemView.findViewById(R.id.textview_message);
    }

    public void populateDonationRequestViewholder(String name,String group,boolean urgent,String message){
        textViewLocationName.setText(name);
        textViewBloodGroup.setText(group);
        textViewMessage.setText(message);
        if(urgent){
            textViewUrgent.setVisibility(View.VISIBLE);
        }else {
            textViewUrgent.setVisibility(View.INVISIBLE);
        }
    }
}
