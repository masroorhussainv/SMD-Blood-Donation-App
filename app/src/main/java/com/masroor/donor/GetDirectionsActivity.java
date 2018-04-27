package com.masroor.donor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.masroor.R;

public class GetDirectionsActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_directions);


        if (checkPlayServices(this)) {
            initMapObject();
        } else {
            Toast.makeText(this, "Google Play Services not supported.", Toast.LENGTH_SHORT).show();
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
    }
}
