package com.example.myspots;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.ConnectionCallbacks;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LandmarkListPage extends AppCompatActivity implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        NavigationView.OnNavigationItemSelectedListener
{
    //navBar
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navView;
//using code from https://www.youtube.com/watch?v=GUlEZIoQGeM
    // Constraints
    private static String TAG = LandmarkListPage.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private static final int PLACE_PICKER_REQUEST = 1;

    // Member variables
   // private LocationListAdapter mAdapter;// this needs to be a class created
    private RecyclerView mRecyclerView;
    private boolean mIsEnabled;
    private GoogleApiClient mClient;
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference landMarkRef = database.getReference("Landmarks");
    private GridView gridLandmark;
    private ArrayList<Landmarks> myArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark_list_page);
        mRecyclerView = findViewById(R.id.rv_SavedPlaces);

        gridLandmark = findViewById(R.id.gvLocationList);
        landMarkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot pulledData : snapshot.getChildren())
                {
                    Landmarks landmark = pulledData.getValue(Landmarks.class);
                    myArrayList.add(landmark);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        MyAdapter myAdapter = new MyAdapter(LandmarkListPage.this, myArrayList);
        gridLandmark.setAdapter(myAdapter);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)){
            return true;//DylanA
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_HomePage:
                 startActivity(new Intent(LandmarkListPage.this, HomePage.class));
                break;
            case R.id.nav_LandmarkListPage:

                 startActivity(new Intent(LandmarkListPage.this, LandmarkListPage.class));
                break;
            case R.id.nav_Settings:

                 startActivity(new Intent(LandmarkListPage.this, Settings.class));
                break;
            case R.id.nav_SignOut:

                startActivity(new Intent(LandmarkListPage.this, MainActivity.class));
                break;
        }
        return true;
    }

}