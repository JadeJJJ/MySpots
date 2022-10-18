package com.example.myspots;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;

public class Settings extends FragmentActivity implements OnMapReadyCallback {
   private GoogleMap mMap;
   private Switch switch1;
   public static String mapMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        switch1 = findViewById(R.id.switch1);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(switch1.isChecked()){
               //mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(Settings.this,R.raw.night_mode_json));
               mapMode = "Night";
                //Toast.makeText(Settings.this,"Dark Mode",Toast.LENGTH_SHORT).show();
            }else{
            //mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(Settings.this,R.raw.day_mode_json));
               // Toast.makeText(Settings.this,"Day Mode",Toast.LENGTH_SHORT).show();
                mapMode = "Day";
            }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
    this.mMap = googleMap;
    this.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
}