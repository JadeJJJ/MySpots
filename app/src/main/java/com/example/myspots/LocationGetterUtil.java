package com.example.myspots;

import static com.example.myspots.BuildConfig.Map_API;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class LocationGetterUtil {

    private Landmarks output;

    public LocationGetterUtil() {
    }

    public Landmarks getOutput() {
        return output;
    }

    public void setOutput(Landmarks output) {
        this.output = output;
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

    //this is used to get the closes place to where a person has put their finger so that they can create a marker
    public void GetClosestPlace(LatLng latLng)
    {
        Uri buildUri = Uri.parse("https://maps.googleapis.com/maps/api/place/nearbysearch/json?").buildUpon()
                .appendQueryParameter("location", latLng.latitude + "," + latLng.longitude)
                .appendQueryParameter("radius", "1500")
                .appendQueryParameter("key", Map_API)
                .build();
        URL urlNearby = null;
        try {
            urlNearby = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        new FetchLandmarkData().execute(urlNearby);

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
        { /*
            if (locationData!=null)
            {
                try {
                    JSONObject rawJSON = new JSONObject(locationData);
                    JSONObject closeLocation = rawJSON.getJSONArray("results").getJSONObject(0);
                    //get the attributes
                    String locName = closeLocation.getString("name");
                    String Address = closeLocation.getString("formatted_address");
                    //Get the position object
                    JSONObject loc =
                            closeLocation.getJSONObject("geometry").getJSONObject("location");
                    Double Lat = loc.getDouble("lat");
                    Double Lng = loc.getDouble("lng");
                    //output = new Landmarks(MainActivity.UserID, locName, Address, Lat, Lng, "SET THIS IN THE DIALOG BOX");

                    final AlertDialog.Builder builder = new AlertDialog.Builder(MapsMainActivity.this);
                    //Sets the message for the dialog box
                    builder.setMessage("Do you wish to add this marker?").setCancelable(true)
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
            } */
        }
    }


}
