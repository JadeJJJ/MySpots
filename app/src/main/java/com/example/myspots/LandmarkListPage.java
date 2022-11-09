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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.ConnectionCallbacks;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.material.navigation.NavigationView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark_list_page);
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