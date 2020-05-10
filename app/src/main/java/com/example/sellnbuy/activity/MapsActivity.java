package com.example.sellnbuy.activity;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.example.sellnbuy.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng gaziUniversity = new LatLng(39.9396396, 32.8201172);
        mMap.addMarker(new MarkerOptions().position(gaziUniversity).title("Gazi Üniversitesi"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(gaziUniversity));

        mMap.animateCamera(CameraUpdateFactory.zoomIn());
    }
}
//,