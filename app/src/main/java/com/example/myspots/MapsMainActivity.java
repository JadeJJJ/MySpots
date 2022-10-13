package com.example.myspots;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myspots.databinding.ActivityMapsMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MapsMainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsMainBinding binding;
    private Spinner spnLandmarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /*ArrayAdapter<String> spnAdapter = new ArrayAdapter<String>(MapsMainActivity.this, android.R.layout.simple_spinner_item, lstLandmarks);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnLandmarks.setAdapter(spnAdapter);*/

        binding = ActivityMapsMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        spnLandmarks = findViewById(R.id.spnLandmarks);
        List<String> lstLandmarks = new ArrayList<>();
        lstLandmarks.add("Church");
        lstLandmarks.add("Police");
        lstLandmarks.add("Hospital");

        ArrayAdapter<String> spnAdapter = new ArrayAdapter<String>(MapsMainActivity.this, android.R.layout.simple_spinner_item, lstLandmarks);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnLandmarks.setAdapter(spnAdapter);
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}