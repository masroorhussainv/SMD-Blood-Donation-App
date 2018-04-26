package com.masroor.donor;

import android.app.Activity;
import android.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.masroor.R;

public class GetDirectionsActivity extends FragmentActivity implements OnMapReadyCallback{

    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_get_directions);

        if(checkPlayServices(this)){
            setContentView(R.layout.map_layout);
            initMapObject();
        }else{
            setContentView(R.layout.activity_get_directions);
        }
    }

    public static boolean checkPlayServices(Activity activity) {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
                Toast.makeText(activity,"Device is supported for Google Play Services!",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(activity,"Device not supported!",Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    public void initMapObject(){
        if(mMap==null){
            SupportMapFragment mapFragment=
                    (SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map_fragment);
            if(mapFragment!=null){
                mapFragment.getMapAsync(this);
            }else{
                Toast.makeText(getApplicationContext(),"Fargment manager error",Toast.LENGTH_LONG).show();
            }

        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;

    }
}
