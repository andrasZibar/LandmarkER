package com.codecool.zibi.landmarker.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public final class HereJsonUtils {
    public static String[] getSimpleLandmarkStringsFromJson(Context context, String landmarkJsonStr)
            throws JSONException {

        final String HERE_LANDMARK_LIST = "View";
        final String HERE_JSON_RESPONSE = "Response";
        final String HERE_LANDMARK_DETAILS = "Result";
        final String HERE_LOCATION_DETAILS = "Location";
        final String HERE_LOCATION_COORDINATE_DETAILS = "DisplayPosition";
        final String HERE_LATITUDE = "Latitude";
        final String HERE_LONGITUDE = "Longitude";
        final String HERE_LOCATION_NAME = "Name";
        final String HERE_DISTANCE = "Distance";

        String[] parsedLandmarkData = null;

        if (landmarkJsonStr == null){
            return null;
        }

        JSONObject landmarkJson = new JSONObject(landmarkJsonStr);

        JSONObject response = landmarkJson.getJSONObject(HERE_JSON_RESPONSE);

        JSONArray resultViewArray = response.getJSONArray(HERE_LANDMARK_LIST);
        JSONObject resultView = resultViewArray.getJSONObject(0);
        JSONArray landmarkArray = resultView.getJSONArray(HERE_LANDMARK_DETAILS);

        parsedLandmarkData = new String[landmarkArray.length()];

        for (int i = 0; i < landmarkArray.length(); i++) {
            String nameOfLandmark;
            String distanceFromUser = "Distance not available";
            double lat;
            double lon;
            double[] location = new double[2];



            JSONObject landmark = landmarkArray.getJSONObject(i);

            JSONObject locationJSON = landmark.getJSONObject(HERE_LOCATION_DETAILS);

            Integer distance = landmark.getInt(HERE_DISTANCE);

            if (distance < 0){
                distanceFromUser = "You are here";
            } else {
                distanceFromUser = String.valueOf(distance) + "m";
            }

            nameOfLandmark = locationJSON.getString(HERE_LOCATION_NAME);

            JSONObject coordinateJSON = locationJSON.getJSONObject(HERE_LOCATION_COORDINATE_DETAILS);

            lat = Double.valueOf(coordinateJSON.getString(HERE_LATITUDE));
            lon = Double.valueOf(coordinateJSON.getString(HERE_LONGITUDE));
            location[0] = lat;
            location[1] = lon;


            parsedLandmarkData[i] = nameOfLandmark + "?" + distanceFromUser + "!" + lat + "," + lon;
        }

        return parsedLandmarkData;
    }
}
