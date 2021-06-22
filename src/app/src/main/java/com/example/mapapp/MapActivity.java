package com.example.mapapp;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ArrayList<Double> latLongResult;
    String selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = super.getIntent();
        latLongResult = (ArrayList<Double>) intent.getSerializableExtra("LatLong");
        selectedLocation = intent.getStringExtra("Location");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Adds a marker at the selected location and displays text
        LatLng latLongLocation = new LatLng(latLongResult.get(0), latLongResult.get(1));
        mMap.addMarker(new MarkerOptions().position(latLongLocation).title("Marker in " + selectedLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLongLocation));
        // When clicked on map initialize marker options
        MarkerOptions markerOptions = new MarkerOptions();
        // Sets position of marker
        markerOptions.position(latLongLocation);
        // Sets title of marker
        markerOptions.title(latLongLocation.latitude + " : " + latLongLocation.longitude);
        // Remove all markers
        mMap.clear();
        // Animates zooming to the marker
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLongLocation, 8));
        // Add marker on map
        mMap.addMarker(markerOptions);
    }
}