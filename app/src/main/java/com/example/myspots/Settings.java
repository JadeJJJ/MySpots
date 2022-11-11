package com.example.myspots;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;
//navBar
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.MenuItem;

public class Settings extends AppCompatActivity {
    private GoogleMap mMap;
    private Switch switch1;
    public static String mapMode;
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference settingsRef = database.getReference("Settings");
    private Button btnSaveSettings;
    private String unitType;
    private RadioButton rdbKilo;
    private RadioButton rdbMiles;
    private Spinner spnLandmarkType;
    private Boolean flag = false;
    //navbar
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        switch1 = findViewById(R.id.switch1);
        rdbKilo = findViewById(R.id.rdbKilo);
        rdbMiles = findViewById(R.id.rdbMiles);
        spnLandmarkType = findViewById(R.id.spnLandmarkTypeSettings);
        List<String> lstLandmarks = new ArrayList<>();
        lstLandmarks.add("Historical");
        lstLandmarks.add("Modern");
        lstLandmarks.add("Popular");

        ArrayAdapter<String> spnAdapter = new ArrayAdapter<String>(Settings.this, android.R.layout.simple_spinner_item, lstLandmarks);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnLandmarkType.setAdapter(spnAdapter);

        settingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot pulledData : snapshot.getChildren())
                {
                    SettingsClass mySettings = pulledData.getValue(SettingsClass.class);
                    if (mySettings.getUserID().equals(MainActivity.UserID))
                    {
                        flag = true;
                        if (mySettings.getUnitType().equals("Kilometers"))
                        {
                            rdbKilo.setChecked(true);
                            rdbMiles.setChecked(false);
                        }
                        else
                        {
                            rdbKilo.setChecked(false);
                            rdbMiles.setChecked(true);
                        }

                        if (mySettings.getMapMode().equals("Night"))
                        {
                            switch1.setChecked(true);
                        }
                        else
                        {
                            switch1.setChecked(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(switch1.isChecked()){
                    mapMode = "Night";
                }else{
                    mapMode = "Day";
                }
            }
        });

        rdbKilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unitType = "Kilometers";
                rdbMiles.setChecked(false);
            }
        });

        rdbMiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unitType = "Miles";
                rdbKilo.setChecked(false);
            }
        });
        btnSaveSettings = findViewById(R.id.btnSaveSettings);
        btnSaveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = MainActivity.UserID;
                String landmark = spnLandmarkType.getSelectedItem().toString();
                SettingsClass newSettings = new SettingsClass(userID, unitType, mapMode, landmark);
                if (flag.equals(false))
                {
                    settingsRef.push().setValue(newSettings);
                }
                else
                {
                    settingsRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot pulledData : snapshot.getChildren())
                            {
                                SettingsClass mySettings = pulledData.getValue(SettingsClass.class);
                                if (mySettings.getUserID() == userID)
                                {
                                    String key = pulledData.getKey();
                                    settingsRef.child(key).setValue(newSettings);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                startActivity(new Intent(Settings.this, MapsMainActivity.class));
            }

        });

        // NAV DRAWER---------------------------------------------------------------------------------------
        // enable ActionBar app icon to behave as action to toggle nav drawer
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);//DylanA
        //getSupportActionBar().setHomeButtonEnabled(true);
/*
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);//DylanA EVERY PAGE NEEDS A DRAWERLAYOUT ID
        mDrawerLayout.addDrawerListener(mToggle);//DylanA

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close); //DylanA
        mToggle.syncState();//DylanA

        navView = findViewById(R.id.nav_side_menu) ;
        navView.setNavigationItemSelectedListener(this);*/
// -------------------------------------------------------------------------------------------------
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//DylanA

        if(mToggle.onOptionsItemSelected(item)){//DylanA
            return true;//DylanA
        }

        return super.onOptionsItemSelected(item);//DylanA
    }

    /*@Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_HomePage:
                startActivity(new Intent(Settings.this, HomePage.class));
                break;
            case R.id.nav_LandmarkListPage:

                startActivity(new Intent(Settings.this, LandmarkListPage.class));
                break;
            case R.id.nav_Settings:

                startActivity(new Intent(Settings.this, Settings.class));
                break;
            case R.id.nav_SignOut:

                startActivity(new Intent(Settings.this, MainActivity.class));
                break;
        }
        return true;
    }*/
}


