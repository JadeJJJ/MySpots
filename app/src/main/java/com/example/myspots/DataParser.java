package com.example.myspots;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

//Reference: Priyanka: https://github.com/priyankapakhale/GoogleMaps-Directions/blob/5452e456b9e074c4641bb5e492749081351d65c4/mapsnearbyplaces/DataParser.java
public class DataParser {
    private HashMap<String,String> getDuration(JSONArray googleDirectionsJson)
    {
        HashMap<String, String> myDirectionsMap = new HashMap<>();
        String duration = "";
        String distance = "";
        try
        {
            duration = googleDirectionsJson.getJSONObject(0).getJSONObject("duration").getString("text");
            distance = googleDirectionsJson.getJSONObject(0).getJSONObject("distance").getString("text");
            myDirectionsMap.put("duration", duration);
            myDirectionsMap.put("distance", distance);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return myDirectionsMap;
    }
    public String[] parseDirections(String jsonData)
    {
        JSONArray myJsonArray = null;
        JSONObject myJsonObject;

        try
        {
            myJsonObject = new JSONObject(jsonData);
            myJsonArray = myJsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPaths(myJsonArray);
    }

    public String[] getPaths(JSONArray myJsonSteps)
    {
        int count = myJsonSteps.length();
        String[] myPolylines = new String[count];

        for (int i = 0; i < count; i++)
        {
            try {
                myPolylines[i] = getPath(myJsonSteps.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return myPolylines;
    }

    public String getPath(JSONObject myJsonPath)
    {
        String myPolyline = "";
        try {
            myPolyline = myJsonPath.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return myPolyline;
    }
}
