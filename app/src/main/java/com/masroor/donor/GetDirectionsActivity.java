package com.masroor.donor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.masroor.R;
import com.masroor.blooddonationapp.Strs;

public class GetDirectionsActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;

    double destination_latitude,destination_longitude;  //passed to this activity in intent
    String destination_location_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_directions);

        getDestinationData();    //extract destination data from intent

        //set location on map
        //when the map ready callback is executed

        if (checkPlayServices(this)) {
            initMapObject();
        } else {
            Toast.makeText(this, "Google Play Services not supported.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setLocationOnMap(double destination_latitude, double destination_longitude) {
        LatLng latLng=new LatLng(destination_latitude,destination_longitude);
        CameraUpdate update=
                CameraUpdateFactory.newLatLngZoom(latLng,15);
        mMap.addMarker(new MarkerOptions().position(latLng).title(destination_location_name));
        mMap.animateCamera(update);
    }

    public void getDestinationData(){
        Bundle b=getIntent().getExtras();
        assert b != null;
        destination_latitude=b.getDouble(Strs.ADMIN_LOCATION_LATITUDE);
        destination_longitude=b.getDouble(Strs.ADMIN_LOCATION_LONGITUDE);
        destination_location_name=b.getString(Strs.ADMIN_LOCATION_NAME);
    }


    public static boolean checkPlayServices(Activity activity) {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
                Toast.makeText(activity, "Device is supported for Google Play Services!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(activity, "Device not supported!", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return true;
    }

    public void initMapObject() {
        if (mMap == null) {
//            SupportMapFragment mapFragment =
//                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);

            MapFragment mapFragment=MapFragment.newInstance();

            FragmentTransaction trans=getFragmentManager()
                    .beginTransaction();
                    trans.add(R.id.rootConstraintLayout,mapFragment);
            trans.commit();

            mapFragment.getMapAsync(this);
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        //set location on map
        setLocationOnMap(destination_latitude,destination_longitude);
        Log.i("location", destination_latitude+","+destination_longitude);
    }
}
