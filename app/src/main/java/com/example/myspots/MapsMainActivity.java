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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    private Button btnFind;
    //handling landmarks from the database
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference landMarkRef = database.getReference("Landmarks");
    private List<Landmarks> landmarksList = new ArrayList<>();

    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userID = getIntent().getStringExtra("userID");
        binding = ActivityMapsMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = new DatabaseHandler();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        spnLandmarks = findViewById(R.id.spnLandmarks);
        List<String> lstLandmarks = new ArrayList<>();
        lstLandmarks.add("Historical");
        lstLandmarks.add("Modern");
        lstLandmarks.add("Popular");

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
                mMap.clear();
                displayMarkers();
                Object[] myObject = new Object[2];
                String url = getDirectionsUrl();
                GetDirectionsData getDirectionsData = new GetDirectionsData();
                myObject[0] = mMap;
                myObject[1] = url;
                getDirectionsData.execute(myObject);
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

        getDeviceLocation();
        updateUI();

        mMap.addMarker(new MarkerOptions().position(CapeTown).title("Marker in Cape Town"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CapeTown, cameraZoom));
        //Add all the markers for the user
        List<Landmarks> landmarksList = GetLandmarksList();



        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                LinearLayout myLayout = new LinearLayout(MapsMainActivity.this);
                myLayout.setOrientation(LinearLayout.VERTICAL);
                EditText inputName = new EditText(MapsMainActivity.this);
                inputName.setHint("Marker name");
                EditText inputDes = new EditText(MapsMainActivity.this);
                inputDes.setHint("Marker Description");
                Spinner spnLandmarkType = new Spinner(MapsMainActivity.this);
                List<String> lstLandmarks = new ArrayList<>();
                lstLandmarks.add("Historical");
                lstLandmarks.add("Modern");
                lstLandmarks.add("Popular");

                ArrayAdapter<String> spnAdapter = new ArrayAdapter<String>(MapsMainActivity.this, android.R.layout.simple_spinner_item, lstLandmarks);
                spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnLandmarkType.setAdapter(spnAdapter);
                myLayout.addView(inputName);
                myLayout.addView(inputDes);
                myLayout.addView(spnLandmarkType);
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
    }

    private void displayMarkers() {
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