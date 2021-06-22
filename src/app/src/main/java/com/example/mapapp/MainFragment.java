package com.example.mapapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainFragment extends Fragment {

    private static final int REQUEST_CODE_DETAILS_ACTIVITY = 1;

    private static final String[] MAP_LOCATIONS = {"User Location", "Dublin", "Kerry", "Belfast", "Cork", "Galway", "Wexford"};
    ArrayList<String> listItems = new ArrayList<>();
    ListView mListView;
    Button mButton;
    FusedLocationProviderClient fusedLocationProviderClient;
    ArrayList<Double> latLong, currentLatLong;
    String selectedLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = view.findViewById(R.id.listView);
        mButton = view.findViewById(R.id.button_viewer);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        createListview();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = mListView.getItemAtPosition(position).toString();
                if(mListView.getItemAtPosition(position).toString().equals("User Location")){
                    declareCurrentLatLong();
                    latLong = currentLatLong;
                } else {
                    latLong = checkValueSelected(value);
                }
                selectedLocation = mListView.getItemAtPosition(position).toString();
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    MapFragment frag = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment2);
                    frag.getMapAsync((MapActivity)getActivity());
                } else {
                    // launch map as its own activity
                    if (selectedLocation != null && latLong != null){
                        Intent intent = new Intent(getActivity(), MapActivity.class);
                        intent.putExtra("LatLong", latLong);
                        intent.putExtra("Location", selectedLocation);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "Please select a city first!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void createListview() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_list_item_1, listItems);
        mListView.setAdapter(adapter);
        for(int i = 0; i < MAP_LOCATIONS.length; i++)
        adapter.add(MAP_LOCATIONS[i]);
    }

    public static ArrayList<Double> checkValueSelected(final String value) {
        switch (value) {
            case "Dublin":
                return new ArrayList<>(Arrays.asList(53.347860, -6.272487));
            case "Kerry":
                return new ArrayList<>(Arrays.asList(52.264007, -9.686990));
            case "Belfast":
                return new ArrayList<>(Arrays.asList(54.602755, -5.945180));
            case "Cork":
                return new ArrayList<>(Arrays.asList(51.892171, -8.475068));
            case "Galway":
                return new ArrayList<>(Arrays.asList(53.276533, -9.069362));
            case "Wexford":
                return new ArrayList<>(Arrays.asList(52.336521, -6.462855));
            default:
                return new ArrayList<>(Arrays.asList(0.0, 0.0));
        }
    }

    public void declareCurrentLatLong(){
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // If permission granted
            getLocation();
        } else {
            // If permission denied
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 &&
                (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            // When permission granted call method
            getLocation();
        } else {
            // When permission is denied, display toast
            Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                // Initializing location
                Location location = task.getResult();
                if (location != null) {
                    // Initializing Geocoder
                    Geocoder geocoder = new Geocoder(getActivity(),
                            Locale.getDefault());
                    // Initializing address list
                    try {
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );
                        // Declaring local lat and long
                        System.out.println(addresses.get(0).getLocality());
                        currentLatLong = new ArrayList<>(Arrays.asList(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // If location is null, opens settings.
                // Open Google Maps and try again
                else {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        });
    }
}
