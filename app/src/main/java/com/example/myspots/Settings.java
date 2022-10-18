package com.example.myspots;

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
import android.view.View;
import android.widget.Toast;
import android.widget.Button;



public class Settings extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    //navBar
    private DrawerLayout mDrawerLayout; //DylanA
    private ActionBarDrawerToggle mToggle; //DylanA
    private NavigationView navView; //DylanA



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

// NAV DRAWER---------------------------------------------------------------------------------------
        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//DylanA
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);//DylanA EVERY PAGE NEEDS A DRAWERLAYOUT ID
        mDrawerLayout.addDrawerListener(mToggle);//DylanA

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close); //DylanA
        mToggle.syncState();//DylanA

        navView = findViewById(R.id.nav_side_menu) ;
        navView.setNavigationItemSelectedListener(this);
// -------------------------------------------------------------------------------------------------
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//DylanA

        if(mToggle.onOptionsItemSelected(item)){//DylanA
            return true;//DylanA
        }

        return super.onOptionsItemSelected(item);//DylanA
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_HomePage:
                // startActivity(new Intent(Settings.this, HomePage.class));
                break;
            case R.id.nav_LandmarkListPage:

                // startActivity(new Intent(Settings.this, LandmarkListPage.class));
                break;
            case R.id.nav_Settings:

                // startActivity(new Intent(Settings.this, LandmarkListPage.class));
                break;
            case R.id.nav_SignOut:

                startActivity(new Intent(Settings.this, MainActivity.class));
                break;
        }
        return true;
    }
}