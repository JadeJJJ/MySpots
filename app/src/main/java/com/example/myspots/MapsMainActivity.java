package com.example.myspots;

import static android.content.ContentValues.TAG;

import static com.example.myspots.BuildConfig.Map_API;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myspots.databinding.ActivityMapsMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

public class MapsMainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsMainBinding binding;
    private Spinner spnLandmarks;
    private boolean LocationPermission;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private int cameraZoom = 15;
    private final LatLng CapeTown = new LatLng(-33.9803833, 18.4759092);
    private Button btnCurrentPosition;
    private DatabaseHandler db;
    private LatLng startPos;
    private LatLng endPos;
    private Button btnDirections;
    //handling landmarks from the database
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference landMarkRef = database.getReference("Landmarks");
    private List<Landmarks> landmarksList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new DatabaseHandler();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        spnLandmarks = findViewById(R.id.spnLandmarks);
        List<String> lstLandmarks = new ArrayList<>();
        lstLandmarks.add("Church");
        lstLandmarks.add("Police");
        lstLandmarks.add("Hospital");

        ArrayAdapter<String> spnAdapter = new ArrayAdapter<String>(MapsMainActivity.this, android.R.layout.simple_spinner_item, lstLandmarks);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnLandmarks.setAdapter(spnAdapter);

        btnCurrentPosition = findViewById(R.id.btnCurrentLocation);
        btnCurrentPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // getLocationPermission();
                final AlertDialog.Builder builder = new AlertDialog.Builder(MapsMainActivity.this);
                //Sets the message for the dialog box
                builder.setMessage("Do you wish to get your current location?").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                if (LocationPermission == true) {
                                    mMap.setMyLocationEnabled(true);
                                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                                    getDeviceLocation();
                                    //startPos = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
                                    //TODO Zoom into the current location
                                } else {
                                    mMap.setMyLocationEnabled(false);
                                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                                    lastKnownLocation = null;
                                    getLocationPermission();
                                    //mMap.addMarker(new MarkerOptions().position(CapeTown).title("Marker in Cape Town"));
                                }
                            }
                        })
                        //Negative button
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                //Cancels the dialog box
                                dialog.cancel();
                            }
                        });
                //Creates and shows the dialog box
                final AlertDialog alert = builder.create();
                alert.show();
            }
        });

        btnDirections = findViewById(R.id.btnDirections);
        btnDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Object[] myObject = new Object[2];
                String url = getDirectionsUrl();
                GetDirectionsData getDirectionsData = new GetDirectionsData();
                myObject[0] = mMap;
                myObject[1] = url;
                getDirectionsData.execute(myObject);
            }
        });


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

        getDeviceLocation();
        updateUI();

        mMap.addMarker(new MarkerOptions().position(CapeTown).title("Marker in Cape Town"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CapeTown, cameraZoom));
        //Add all the markers for the user
        List<Landmarks> landmarksList = GetLandmarksList();
        //Toast.makeText(this, landmarksList.get(0).getLandMarkName(), Toast.LENGTH_SHORT).show(); //
        if (!landmarksList.isEmpty()){
            for (Landmarks lm : landmarksList)
            {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lm.getLatitude(), lm.getLongitude()))
                        .title(lm.getLandMarkName())
                        .snippet(lm.getLandMarkAddress()));
            }
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                LinearLayout myLayout = new LinearLayout(MapsMainActivity.this);
                myLayout.setOrientation(LinearLayout.VERTICAL);
                EditText inputName = new EditText(MapsMainActivity.this);
                inputName.setHint("Marker name");
                EditText inputDes = new EditText(MapsMainActivity.this);
                inputDes.setHint("Marker Description");
                myLayout.addView(inputName);
                myLayout.addView(inputDes);
                //----------------------------------------------------------------------------------
                //TODO Put the code for the snapping the marker here
                /*Object[] myObject = new Object[2];
                String url = getNearbyPlace();
                GetDirectionsData getDirectionsData = new GetDirectionsData();
                myObject[0] = mMap;
                myObject[1] = url;
                getDirectionsData.execute(myObject);*/
                //----------------------------------------------------------------------------------
                final AlertDialog.Builder builder = new AlertDialog.Builder(MapsMainActivity.this);
                //Sets the message for the dialog box
                builder.setMessage("Do you wish to add this marker?").setCancelable(true)
                        .setView(myLayout)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                String markerName = inputName.getText().toString();
                                String markerDes = inputDes.getText().toString();
                                // This is where it will be stored in the database. We have the position(latlng)
                                Landmarks newLandmark = new Landmarks(MainActivity.UserID, markerName,markerDes,latLng.latitude,latLng.longitude);
                                db.PostLandmark(newLandmark);

                                mMap.addMarker(new MarkerOptions().position(latLng).title(markerName).snippet(markerDes));
                                endPos = latLng;
                            }
                        })
                        //Negative button
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                //Cancels the dialog box
                                dialog.cancel();
                            }
                        });
                //Creates and shows the dialog box
                final AlertDialog alert = builder.create();
                alert.show();

            }
        });
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            LocationPermission = true;
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    //--------------------------------------------------------------------------------------------//
    //
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LocationPermission  = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationPermission  = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateUI();
    }

    //--------------------------------------------------------------------------------------------//
    //
    private void updateUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (LocationPermission == true) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
                //mMap.addMarker(new MarkerOptions().position(CapeTown).title("Marker in Cape Town"));
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    //--------------------------------------------------------------------------------------------//
    //
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */

        try {
            if (LocationPermission) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), 15));
                                startPos = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(CapeTown, 15));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);

                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private String getDirectionsUrl()
    {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin="+ startPos.latitude + "," + startPos.longitude);
        googleDirectionsUrl.append("&destination=" + endPos.latitude + "," + endPos.longitude);
        googleDirectionsUrl.append("&key=" + Map_API);
        return googleDirectionsUrl.toString();
    }

    private List<Landmarks> GetLandmarksList(){
        landMarkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot pulled : snapshot.getChildren()){
                    Landmarks lm = pulled.getValue(Landmarks.class);
                    landmarksList.add(lm);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            }
        });
        return landmarksList;
    }

    private String getNearbyPlace()
    {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        url.append("location="+ startPos.latitude + "," + startPos.longitude);
        url.append("&radius=1500");
        url.append("&key=" + Map_API);
        return url.toString();
    }

}