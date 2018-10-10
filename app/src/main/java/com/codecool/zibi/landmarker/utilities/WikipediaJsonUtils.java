package com.codecool.zibi.landmarker.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public final class WikipediaJsonUtils {
    final static String WIKI_CONTENT = "extract";
    final static String WIKI_THUMBNAIL_DETAILS = "thumbnail";
    final static String WIKI_THUMBNAIL_LINK = "source";

    public static String getWikipediaContentFromJson(Context context, String wikiJsonStr)
            throws JSONException {

        String content = null;

        if (wikiJsonStr == null){
            return null;
        }

        JSONObject wikiJson = new JSONObject(wikiJsonStr);

        content = wikiJson.getString(WIKI_CONTENT);


        return content;
    }

    public static URL getWikipediaThumbnailURLFromJson(Context context, String wikiJsonStr)
            throws JSONException {

        URL thumbnailUrl = null;

        if (wikiJsonStr == null){
            return null;
        }

        JSONObject wikiJson = new JSONObject(wikiJsonStr);

        JSONObject thumbnailDetails = wikiJson.getJSONObject(WIKI_THUMBNAIL_DETAILS);

        try {
            thumbnailUrl = new URL(thumbnailDetails.getString(WIKI_THUMBNAIL_LINK));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return thumbnailUrl;
    }
}
