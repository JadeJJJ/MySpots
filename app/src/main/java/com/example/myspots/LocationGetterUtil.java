package com.example.myspots;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class LocationGetterUtil {
    private static final String BASE_URL="https://maps.googleapis.com/maps/api/place/"; // This finds the nearest starbucks https://maps.googleapis.com/maps/api/place/findplacefromtext/json?key={KEY HERE}&inputtype=textquery&input=Starbucks
    private static String RequestType;
    private static final String RETURN_TYPE= "json";
    private static final String LOGGING_TAG= "URLWECREATED";

    //Builds the URL to be executed to fetch the JSON Info
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

        //TODO: Potentially have a for loop that runs here to return a bunch of values
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

    public static void setRequestType(int requestType) {
        /*
        0 - Finds the place ID
        1 - Gets the name of a place using the place id
         */
        switch (requestType)
        {
            case 0:
                RequestType = "findplacefromtext";
                break;
            case 1:
                RequestType = "Do that thing";
                break;
        }

    }

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
}
