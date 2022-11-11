package com.example.myspots;

import static android.content.ContentValues.TAG;

import static com.example.myspots.BuildConfig.Map_API;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myspots.databinding.ActivityMapsMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

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
    private Button btnFind;
    //handling landmarks from the database
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference landMarkRef = database.getReference("Landmarks");
    private List<Landmarks> landmarksList = new ArrayList<>();
    //Getting the granular landmarks here
    private PlacesClient placesClient;
    private String[] likelyPlaceNames;
    private String[] likelyPlaceAddresses;
    private List[] likelyPlaceAttributions;
    private LatLng[] likelyPlaceLatLngs;
    //Settings
    private String unitType;
    private String sLandmarkType;
    private boolean flagLandmarks;
    String mapMode = null;
    private DatabaseReference settingsRef = database.getReference("Settings");
    private Button btnSettings;


    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userID = getIntent().getStringExtra("userID");
        mapMode = getIntent().getStringExtra("mapMode");
        binding = ActivityMapsMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new DatabaseHandler();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // For the alert dialog box
        spnLandmarks = findViewById(R.id.spnLandmarks);
        List<String> lstLandmarks = new ArrayList<>();
        lstLandmarks.add("Historical");
        lstLandmarks.add("Modern");
        lstLandmarks.add("Popular");
        ArrayAdapter<String> spnAdapter = new ArrayAdapter<String>(MapsMainActivity.this, android.R.layout.simple_spinner_item, lstLandmarks);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnLandmarks.setAdapter(spnAdapter);

        //For the place adapter
        Places.initialize(getApplicationContext(), Map_API);
        placesClient = Places.createClient(this);

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
                mMap.clear();
                displayMarkers();
                Object[] myObject = new Object[2];
                String url = getDirectionsUrl();
                GetDirectionsData getDirectionsData = new GetDirectionsData();
                myObject[0] = mMap;
                myObject[1] = url;
                getDirectionsData.execute(myObject);

                Uri buildUri = Uri.parse("https://maps.googleapis.com/maps/api/distancematrix/json").buildUpon()
                        .appendQueryParameter("origin",startPos.latitude + "%2C" + startPos.longitude)
                        .appendQueryParameter("destination", endPos.latitude + "%2C" + endPos.longitude)
                        .appendQueryParameter("key", Map_API)
                        .build();

                URL urlNearby = null;
                try {
                    urlNearby = new URL(buildUri.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                new FetchDistanceData().execute(urlNearby);

            }
        });

        btnFind = findViewById(R.id.btnFindLocation);
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedType = spnLandmarks.getSelectedItem().toString();
                mMap.clear();
                    landMarkRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot pulledData : snapshot.getChildren())
                            {
                                Landmarks landmark = pulledData.getValue(Landmarks.class);
                                if (landmark.getLandmarkType().equals("Historical") && selectedType.equals("Historical") && landmark.getUserId().equals(userID))
                                {

                                    double lat = pulledData.child("latitude").getValue(Double.class);
                                    double lng = pulledData.child("longitude").getValue(Double.class);
                                    LatLng pos = new LatLng(lat,lng);
                                    String name = landmark.getLandMarkName();
                                    String address = landmark.getLandMarkAddress();
                                    mMap.addMarker(new MarkerOptions()
                                            .position(pos)
                                            .title(name)
                                            .snippet(address));
                                }
                                else if (landmark.getLandmarkType().equals("Modern") && selectedType.equals("Modern") && landmark.getUserId().equals(userID))
                                {
                                    double lat = pulledData.child("latitude").getValue(Double.class);
                                    double lng = pulledData.child("longitude").getValue(Double.class);
                                    LatLng pos = new LatLng(lat,lng);
                                    String name = landmark.getLandMarkName();
                                    String address = landmark.getLandMarkAddress();
                                    mMap.addMarker(new MarkerOptions()
                                            .position(pos)
                                            .title(name)
                                            .snippet(address)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                }
                                else if (landmark.getLandmarkType().equals("Popular") && selectedType.equals("Popular") && landmark.getUserId().equals(userID))
                                {
                                    double lat = pulledData.child("latitude").getValue(Double.class);
                                    double lng = pulledData.child("longitude").getValue(Double.class);
                                    LatLng pos = new LatLng(lat,lng);
                                    String name = landmark.getLandMarkName();
                                    String address = landmark.getLandMarkAddress();
                                    mMap.addMarker(new MarkerOptions()
                                            .position(pos)
                                            .title(name)
                                            .snippet(address)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            }
        });

        settingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot pulledData : snapshot.getChildren())
                {
                    SettingsClass mySettings = pulledData.getValue(SettingsClass.class);
                    if (mySettings.getUserID().equals(userID))
                    {
                        mapMode = mySettings.getMapMode();
                        unitType = mySettings.getUnitType();
                        sLandmarkType = mySettings.getLandmarkType();
                        if(mapMode.equals("Night")){
                            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsMainActivity.this,R.raw.night_mode_json));
                        }else{
                            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsMainActivity.this,R.raw.day_mode_json));
                        }
                        flagLandmarks = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsMainActivity.this, Settings.class));
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

        //mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapsMainActivity.this,R.raw.day_mode_json));
        getDeviceLocation();
        updateUI();

        mMap.addMarker(new MarkerOptions().position(CapeTown).title("Marker in Cape Town"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CapeTown, cameraZoom));
        //Add all the markers for the user
        List<Landmarks> landmarksList = GetLandmarksList();



        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {

                //----------------------------------------------------------------------------------
                //TODO Put the code for the snapping the marker here
/*
                //Code Gotten from: http://theoryapp.com/parse-json-in-java/
                StringBuilder urlBuild = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                urlBuild.append("location="+ startPos.latitude + "," + startPos.longitude);
                urlBuild.append("&radius=1500");
                urlBuild.append("&key=" + Map_API);
                String urlNearby = urlBuild.toString(); // gets the url using nearby places Maybe put this in a class
                try {

                    //Building URL
                    urlNearby = URLEncoder.encode(getNearbyPlace(), "UTF-8");
                    URL url = new URL(urlNearby);
                    // read from the URL
                    Scanner scan = new Scanner(url.openStream());
                    String str = new String();
                    while (scan.hasNext())
                        str += scan.nextLine();
                    scan.close();
                    // build a JSON object
                    JSONObject obj = new JSONObject(str);
                    if (! obj.getString("status").equals("OK"))
                        return;

                    // get the first result
                    JSONObject res = obj.getJSONArray("results").getJSONObject(0);
                    //get the address
                    String Address = res.getString("formatted_address");
                    //get the name
                    String locName = res.getString("name");
                    //Get the position
                    JSONObject loc =
                            res.getJSONObject("geometry").getJSONObject("location");
                    Double Lat = loc.getDouble("lat");
                    Double Lng = loc.getDouble("lng");
                    /*System.out.println("lat: " + loc.getDouble("lat") +
                            ", lng: " + loc.getDouble("lng"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                */

                //----------------------------------------------------------------------------------
                /*
                final AlertDialog.Builder builder = new AlertDialog.Builder(MapsMainActivity.this);
                //Sets the message for the dialog box
                builder.setMessage("Do you wish to add this marker?").setCancelable(true)
                        .setView(myLayout)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                String markerName = inputName.getText().toString();
                                String markerDes = inputDes.getText().toString();
                                String selectedType = spnLandmarkType.getSelectedItem().toString();
                                // This is where it will be stored in the database. We have the position(latlng)
                                Landmarks newLandmark = new Landmarks(MainActivity.UserID, markerName,markerDes,latLng.latitude,latLng.longitude, selectedType);
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
            */
                GetClosestPlace(latLng);
            }
        });
        displayMarkers();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                endPos = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                return false;
            }
        });

        mMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(@NonNull PointOfInterest poi) {

                //Setting up the layout of the alert
                LinearLayout myLayout = new LinearLayout(MapsMainActivity.this);
                myLayout.setOrientation(LinearLayout.VERTICAL);

                EditText edtDescription = new EditText(MapsMainActivity.this);
                edtDescription.setHint("Marker Description");

                Spinner spnLandmarkType = new Spinner(MapsMainActivity.this);
                List<String> lstLandmarks = new ArrayList<>();
                lstLandmarks.add("Historical");
                lstLandmarks.add("Modern");
                lstLandmarks.add("Popular");
                ArrayAdapter<String> spnAdapter = new ArrayAdapter<String>(MapsMainActivity.this, android.R.layout.simple_spinner_item, lstLandmarks);
                spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnLandmarkType.setAdapter(spnAdapter);

                myLayout.addView(edtDescription);
                myLayout.addView(spnLandmarkType);
                /* THIS Causes the marker to be added and showing the info window is not good
                Marker poiMarker = mMap.addMarker(new MarkerOptions()
                        .position(poi.latLng)
                        .title(poi.name));
                poiMarker.showInfoWindow();*/
                final AlertDialog.Builder builder = new AlertDialog.Builder(MapsMainActivity.this);
                //Sets the message for the dialog box
                builder.setMessage("Do you wish to add a marker at "+poi.name+"?").setCancelable(true)
                        .setView(myLayout)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                String selectedType = spnLandmarkType.getSelectedItem().toString();
                                String description = edtDescription.getText().toString();
                                LatLng poiLL = poi.latLng;
                                // This is where it will be stored in the database. We have the position(latlng)
                                Landmarks newLandmark = new Landmarks(MainActivity.UserID, poi.name,description,poiLL.latitude,poiLL.longitude, selectedType);
                                db.PostLandmark(newLandmark); // posting to db
                                Marker poiMarker = mMap.addMarker(new MarkerOptions()
                                        .position(poiLL)
                                        .title(poi.name)
                                        .snippet(description));
                                poiMarker.showInfoWindow();
                                //mMap.addMarker(new MarkerOptions().position(poiLL).title(poi.name).snippet(description)); //creating marker on map
                                endPos = poiLL;
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

    private void displayMarkers() {
        if ( flagLandmarks == true)
        {
            populateMarkersOffChoice();
        }
        else
        {
            landMarkRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot pulledData : snapshot.getChildren())
                    {
                        Landmarks landmark = pulledData.getValue(Landmarks.class);
                        if (landmark.getLandmarkType().equals("Historical") && landmark.getUserId().equals(userID))
                        {
                            double lat = pulledData.child("latitude").getValue(Double.class);
                            double lng = pulledData.child("longitude").getValue(Double.class);
                            LatLng pos = new LatLng(lat,lng);
                            String name = landmark.getLandMarkName();
                            String address = landmark.getLandMarkAddress();
                            mMap.addMarker(new MarkerOptions()
                                    .position(pos)
                                    .title(name)
                                    .snippet(address));
                        }
                        if (landmark.getLandmarkType().equals("Modern") && landmark.getUserId().equals(userID))
                        {
                            double lat = pulledData.child("latitude").getValue(Double.class);
                            double lng = pulledData.child("longitude").getValue(Double.class);
                            LatLng pos = new LatLng(lat,lng);
                            String name = landmark.getLandMarkName();
                            String address = landmark.getLandMarkAddress();
                            mMap.addMarker(new MarkerOptions()
                                    .position(pos)
                                    .title(name)
                                    .snippet(address)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        }
                        if (landmark.getLandmarkType().equals("Popular") && landmark.getUserId().equals(userID))
                        {
                            double lat = pulledData.child("latitude").getValue(Double.class);
                            double lng = pulledData.child("longitude").getValue(Double.class);
                            LatLng pos = new LatLng(lat,lng);
                            String name = landmark.getLandMarkName();
                            String address = landmark.getLandMarkAddress();
                            mMap.addMarker(new MarkerOptions()
                                    .position(pos)
                                    .title(name)
                                    .snippet(address)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

    private void populateMarkersOffChoice()
    {
        landMarkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot pulledData : snapshot.getChildren())
                {
                    Landmarks landmark = pulledData.getValue(Landmarks.class);
                    if (landmark.getLandmarkType().equals("Historical") && sLandmarkType.equals("Historical") && landmark.getUserId().equals(userID))
                    {

                        double lat = pulledData.child("latitude").getValue(Double.class);
                        double lng = pulledData.child("longitude").getValue(Double.class);
                        LatLng pos = new LatLng(lat,lng);
                        String name = landmark.getLandMarkName();
                        String address = landmark.getLandMarkAddress();
                        mMap.addMarker(new MarkerOptions()
                                .position(pos)
                                .title(name)
                                .snippet(address));
                    }
                    else if (landmark.getLandmarkType().equals("Modern") && sLandmarkType.equals("Modern") && landmark.getUserId().equals(userID))
                    {
                        double lat = pulledData.child("latitude").getValue(Double.class);
                        double lng = pulledData.child("longitude").getValue(Double.class);
                        LatLng pos = new LatLng(lat,lng);
                        String name = landmark.getLandMarkName();
                        String address = landmark.getLandMarkAddress();
                        mMap.addMarker(new MarkerOptions()
                                .position(pos)
                                .title(name)
                                .snippet(address)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    }
                    else if (landmark.getLandmarkType().equals("Popular") && sLandmarkType.equals("Popular") && landmark.getUserId().equals(userID))
                    {
                        double lat = pulledData.child("latitude").getValue(Double.class);
                        double lng = pulledData.child("longitude").getValue(Double.class);
                        LatLng pos = new LatLng(lat,lng);
                        String name = landmark.getLandMarkName();
                        String address = landmark.getLandMarkAddress();
                        mMap.addMarker(new MarkerOptions()
                                .position(pos)
                                .title(name)
                                .snippet(address)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
    // TODO On this update UI function maybe re-add the markers? IE Call Display markers
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
                @SuppressLint("MissingPermission") Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
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
    //TESTING CODE----------------------------------------------------------------------------------
    // Gets the response from Http
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection =(HttpURLConnection) url.openConnection();
        try
        {
            InputStream in  = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("//A");
            boolean hasInput = scanner.hasNext();
            if (hasInput)
            {
                return scanner.next();
            } else
            {
                return null;
            }
        } finally
        {
            urlConnection.disconnect();
        }
    }

    //this is used to get the closes place to where a person has put their finger so that they can create a marker
    public void GetClosestPlace(LatLng latLng)
    {
        /*
        Uri buildUri = Uri.parse("https://maps.googleapis.com/maps/api/place/nearbysearch/json").buildUpon()
                .appendQueryParameter("location", latLng.latitude + "," + latLng.longitude)
                .appendQueryParameter("radius", "1500")
                .appendQueryParameter("key", Map_API)
                .build();
        // TESTER https://maps.googleapis.com/maps/api/place/nearbysearch/json?location:-33.98273266107884%2C18.459802430187832&radius=1500&key=AIzaSyCIodK972eupbLoQy_0084qjnhO20dFMJc
        //https://maps.googleapis.com/maps/api/place/nearbysearch/json
        //  ?keyword=cruise
        //  &location=-33.8670522%2C151.1957362
        //  &radius=1500
        //  &type=restaurant
        //  &key=YOUR_API_KEY
        URL urlNearby = null;
        try {
            urlNearby = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new FetchLandmarkData().execute(urlNearby); */
//FROM GOOGLE---------------------------------------------------------------------------------------
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                Place.Field.LAT_LNG);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.newInstance(placeFields);


        // Get the likely places - that is, the businesses and other points of interest that
        // are the best match for the device's current location.
        @SuppressWarnings("MissingPermission") final Task<FindCurrentPlaceResponse> placeResult =
                placesClient.findCurrentPlace(request);
        placeResult.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
            @Override
            public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    FindCurrentPlaceResponse likelyPlaces = task.getResult();

                    // Set the count, handling cases where less than 5 entries are returned.
                    int count;
                    if (likelyPlaces.getPlaceLikelihoods().size() < 5) {
                        count = likelyPlaces.getPlaceLikelihoods().size();
                    } else {
                        count = 5;
                    }

                    int i = 0;
                    likelyPlaceNames = new String[count];
                    likelyPlaceAddresses = new String[count];
                    likelyPlaceAttributions = new List[count];
                    likelyPlaceLatLngs = new LatLng[count];

                    for (PlaceLikelihood placeLikelihood : likelyPlaces.getPlaceLikelihoods()) {
                        // Build a list of likely places to show the user.
                        likelyPlaceNames[i] = placeLikelihood.getPlace().getName();
                        likelyPlaceAddresses[i] = placeLikelihood.getPlace().getAddress();
                        likelyPlaceAttributions[i] = placeLikelihood.getPlace()
                                .getAttributions();
                        likelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                        i++;
                        if (i > (count - 1)) {
                            break;
                        }
                    }

                    // Show a dialog offering the user the list of likely places, and add a
                    // marker at the selected place.
                    MapsMainActivity.this.openPlacesDialog();
                } else {
                    Log.e(TAG, "Exception: %s", task.getException());
                }
            }
        });

/* OLD CODE
        try {

            //Building URL
            urlNearby = URLEncoder.encode(getNearbyPlace(), "UTF-8");
            URL url = new URL(urlNearby);
            // read from the URL
            Scanner scan = new Scanner(url.openStream());
            String str = new String();
            while (scan.hasNext())
                str += scan.nextLine();
            scan.close();
            // build a JSON object
            JSONObject obj = new JSONObject(str);
            if (! obj.getString("status").equals("OK"))
                return;

            // get the first result
            JSONObject res = obj.getJSONArray("results").getJSONObject(0);
            //get the attributes
            Address = res.getString("formatted_address");
            locName = res.getString("name");
            //Get the position object
            JSONObject loc =
                    res.getJSONObject("geometry").getJSONObject("location");
            Lat = loc.getDouble("lat");
            Lng = loc.getDouble("lng");

        } catch (Exception e) {
            e.printStackTrace();
        }
*/
    }

    /*** Asynchronous task that requests weather data.*/
    class FetchLandmarkData extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            URL LocationURL = urls[0];
            String locationData = null;
            try {
                locationData = getResponseFromHttpUrl(LocationURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return locationData;
        }

        @Override
        protected void onPostExecute(String locationData) {
            if (locationData != null) {
                //Output Location data here
                //tvWeather.setText(locationData);
                ConsumeJSON(locationData);
            }
            super.onPostExecute(locationData);
        }

        // converts the given raw JSON Data and coverts it
        protected void ConsumeJSON(String locationData)
        {
            //Setting up the layout of the alert
            LinearLayout myLayout = new LinearLayout(MapsMainActivity.this);
            myLayout.setOrientation(LinearLayout.VERTICAL);
            //EditText inputName = new EditText(MapsMainActivity.this);
            //inputName.setHint("Marker name");
            //EditText inputDes = new EditText(MapsMainActivity.this);
            //inputDes.setHint("Marker Description");
            Spinner spnLandmarkType = new Spinner(MapsMainActivity.this);
            List<String> lstLandmarks = new ArrayList<>();
            lstLandmarks.add("Historical");
            lstLandmarks.add("Modern");
            lstLandmarks.add("Popular");

            ArrayAdapter<String> spnAdapter = new ArrayAdapter<String>(MapsMainActivity.this, android.R.layout.simple_spinner_item, lstLandmarks);
            spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnLandmarkType.setAdapter(spnAdapter);
            //myLayout.addView(inputName);
            //myLayout.addView(inputDes);
            myLayout.addView(spnLandmarkType);

            if (locationData!=null)
            {
                try {
                    JSONObject rawJSON = new JSONObject(locationData);
                    JSONObject closeLocation = rawJSON.getJSONArray("results").getJSONObject(0);
                    //get the attributes
                    String locName = closeLocation.getString("name");
                    String Address = closeLocation.getString("place_id");
                    //Get the position object
                    JSONObject loc =
                            closeLocation.getJSONObject("geometry").getJSONObject("location");
                    Double Lat = loc.getDouble("lat");
                    Double Lng = loc.getDouble("lng");
                    //output = new Landmarks(MainActivity.UserID, locName, Address, Lat, Lng, "SET THIS IN THE DIALOG BOX");

                    final AlertDialog.Builder builder = new AlertDialog.Builder(MapsMainActivity.this);
                    //Sets the message for the dialog box
                    builder.setMessage("Do you wish to add a marker at "+locName+"?").setCancelable(true)
                            .setView(myLayout)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                    String selectedType = spnLandmarkType.getSelectedItem().toString();
                                    // This is where it will be stored in the database. We have the position(latlng)
                                    Landmarks newLandmark = new Landmarks(MainActivity.UserID, locName,Address,Lat,Lng, selectedType);
                                    db.PostLandmark(newLandmark); // posting to db
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(Lat,Lng)).title(locName).snippet(Address)); //creating marker on map
                                    endPos = new LatLng(Lat,Lng);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class FetchDistanceData extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            URL LocationURL = urls[0];
            String locationData = null;
            try {
                locationData = getResponseFromHttpUrl(LocationURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return locationData;
        }

        @Override
        protected void onPostExecute(String locationData) {
            if (locationData != null) {
                //Output Location data here
                //tvWeather.setText(locationData);
                ConsumeJSON(locationData);
            }
            super.onPostExecute(locationData);
        }

        // converts the given raw JSON Data and coverts it
        protected void ConsumeJSON(String locationData)
        {
            //Setting up the layout of the alert
            LinearLayout myLayout = new LinearLayout(MapsMainActivity.this);
            myLayout.setOrientation(LinearLayout.VERTICAL);

            TextView tvDistance = new TextView(MapsMainActivity.this);
            TextView tvTimeTakes = new TextView(MapsMainActivity.this);

            myLayout.addView(tvDistance);
            myLayout.addView(tvTimeTakes);

            if (locationData!=null)
            {
                try {
                    JSONObject rawJSON = new JSONObject(locationData);

                    JSONObject elements = rawJSON.getJSONArray("rows").getJSONArray(0).getJSONObject(0);
                    //get the attributes
                    JSONObject distence = elements.getJSONObject("distance");
                    JSONObject duration = elements.getJSONObject("duration");
                    String disText = distence.getString("text");
                    String durText = duration.getString("text");

                    final AlertDialog.Builder builder = new AlertDialog.Builder(MapsMainActivity.this);
                    //Sets the message for the dialog box
                    builder.setMessage("Distance Until Location: " + disText+"\nDuration Until Location: " + durText);
                    //Creates and shows the dialog box
                    final AlertDialog alert = builder.create();
                    alert.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
//FROM GOOGLE PLACES DIALOG-------------------------------------------------------------------------
    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        Spinner spnLandmarkType = new Spinner(MapsMainActivity.this);
        List<String> lstLandmarks = new ArrayList<>();
        lstLandmarks.add("Historical");
        lstLandmarks.add("Modern");
        lstLandmarks.add("Popular");
        ArrayAdapter<String> spnAdapter = new ArrayAdapter<String>(MapsMainActivity.this, android.R.layout.simple_spinner_item, lstLandmarks);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnLandmarkType.setAdapter(spnAdapter);


        //DialogInterface. .addView(spnLandmarkType);
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The "which" argument contains the position of the selected item.
                LatLng markerLatLng = likelyPlaceLatLngs[which];
                String markerSnippet = likelyPlaceAddresses[which];
                String selectedType = spnLandmarkType.getSelectedItem().toString();
                if (likelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + likelyPlaceAttributions[which];
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                mMap.addMarker(new MarkerOptions()
                        .title(likelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet));
                Landmarks loc = new Landmarks(MainActivity.UserID, likelyPlaceNames[which], markerSnippet,markerLatLng.latitude,markerLatLng.longitude,selectedType);
                db.PostLandmark(loc);
                // Position the map's camera at the location of the marker.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        15));
                // clearing the arrays
                likelyPlaceNames = null;
                likelyPlaceAddresses = null;
                likelyPlaceAttributions = null;
                likelyPlaceLatLngs = null;

            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Please pick a place")
                .setItems(likelyPlaceNames, listener)
                .setView(spnLandmarkType)
                .show();
    }

}