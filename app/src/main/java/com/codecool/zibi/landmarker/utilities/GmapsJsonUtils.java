package com.codecool.zibi.landmarker.utilities;

import android.util.Log;
import com.codecool.zibi.landmarker.model.Landmark;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public final class GmapsJsonUtils {

    private static final String GMAPS_KEY_PARAM = "key";
    private static final String GMAPS_RESULTS_PARAM = "results";
    private static final String GMAPS_LATITUDE_PARAM = "lat";
    private static final String GMAPS_LONGITUDE_PARAM = "lng";
    private static final String GMAPS_GEODATA_PARAM = "geometry";
    private static final String GMAPS_LOCATION_PARAM = "location";
    private static final String GMAPS_LOCATION_NAME_PARAM = "name";
    private static final String GMAPS_LOCATION_PHOTODATA_PARAM = "photos";
    private static final String GMAPS_LOCATION_PHOTOID_PARAM = "photo_reference";
    private static final String GMAPS_LOCATION_ID_PARAM = "place_id";
    private static final String GMAPS_RADIUS_PARAM = "radius";
    private static final String GMAPS_RANKBY_PARAM = "rankby";
    private static final String GMAPS_KEYWORD_PARAM = "keyword";
    private static final String GMAPS_LANGUAGE_PARAM = "language";
    private static final String GMAPS_RANKBY_DISTANCE_PARAM = "distance";
    private static final String GMAPS_RANKBY_IMPORTANCE_PARAM = "prominance";

    public static Landmark[] getLandmarkObjectsFromJson(String jsonString, double originLat, double originLon){
        Landmark[] landmarks;
        JSONArray result;

        if (jsonString == null){
            return null;
        }

        try {
            result = new JSONObject(jsonString).getJSONArray(GMAPS_RESULTS_PARAM);
            landmarks = new Landmark[result.length()];
            if (result.length() != 0){
                for (int i = 0; i < result.length(); i++) {
                    JSONObject currentLandmark = (JSONObject) result.get(i);
                    Double lat = currentLandmark.getJSONObject(GMAPS_GEODATA_PARAM).getJSONObject(GMAPS_LOCATION_PARAM).getDouble(GMAPS_LATITUDE_PARAM);
                    Double lon = currentLandmark.getJSONObject(GMAPS_GEODATA_PARAM).getJSONObject(GMAPS_LOCATION_PARAM).getDouble(GMAPS_LONGITUDE_PARAM);
                    String locationID = currentLandmark.getString(GMAPS_LOCATION_ID_PARAM);
                    String name = currentLandmark.getString(GMAPS_LOCATION_NAME_PARAM);
                    String photoID;
                    try {
                        photoID = currentLandmark.getJSONArray(GMAPS_LOCATION_PHOTODATA_PARAM).getJSONObject(0).getString(GMAPS_LOCATION_PHOTOID_PARAM);

                    } catch (JSONException noPhoto){
                        Log.e("GmapsJSONReader", noPhoto.getClass().getSimpleName() + noPhoto.getMessage());
                        photoID = null;
                    }

                    landmarks[i] = new Landmark(lat, lon, locationID, name, photoID, originLat, originLon);
                }
            } else {
                Log.e("GmapsJsonutils", "empty resultset");
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }





        return landmarks;
    }
}
