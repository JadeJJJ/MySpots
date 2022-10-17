package com.example.myspots;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;


import java.io.IOException;
import java.util.Objects;

public class GetDirectionsData extends AsyncTask<Object, String, String> {
    GoogleMap mMap;
    String url;
    String googleDirectionData;

    @Override
    protected String doInBackground(Object... objects)
    {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try
        {
            googleDirectionData = downloadUrl.readUrl(url);
        }catch (IOException e){
            e.printStackTrace();
        }
        return googleDirectionData;
    }
    @Override
    protected void onPostExecute(String s)
    {
        String[] directionsList;
        DataParser parser = new DataParser();
        directionsList = parser.parseDirections(s);
        displayDirections(directionsList);
    }

    public void displayDirections(String[] directionsList) {
        int count = directionsList.length;
        for (int i = 0; i < count; i++)
        {
            PolylineOptions options = new PolylineOptions();
            options.color(Color.BLUE);
            options.width(10);
            options.addAll(PolyUtil.decode(directionsList[i]));

            mMap.addPolyline(options);
        }
    }
}
