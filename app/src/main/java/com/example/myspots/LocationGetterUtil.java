package com.example.myspots;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

public class LocationGetterUtil {
    private static final String BASE_URL="https://maps.googleapis.com/maps/api/place/"; // This finds the nearest starbucks https://maps.googleapis.com/maps/api/place/findplacefromtext/json?key={KEY HERE}&inputtype=textquery&input=Starbucks
    private static String RequestType = "findplacefromtext";
    private static final String RETURN_TYPE= "json";
    private static final String LOGGING_TAG= "URLWECREATED";

    public static URL BuildURLForLocation(String input)
    {
        //This gets the
        Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(RequestType)
                .appendPath("json")
                .appendQueryParameter("key", BuildConfig.Map_API)
                .appendQueryParameter("inputtype","textquery")
                .appendQueryParameter("text",input)
                .build();
        URL url = null;

        try
        {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        Log.i(LOGGING_TAG, "buildURLForWeather: " + url);
        return url;
    }
}
