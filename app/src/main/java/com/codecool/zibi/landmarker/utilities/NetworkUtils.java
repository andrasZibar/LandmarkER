/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codecool.zibi.landmarker.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the weather servers.
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String HERE_BASE_URL = "https://reverse.geocoder.api.here.com/6.2";
    private static final String WIKIPEDIA_BASE_URL = "https://en.wikipedia.org/api/rest_v1/page/summary/";


    private static final String format = "json";
    private static final String units = "metric";
    private static final int numDays = 14;

    final static String QUERY_PARAM = "q";
    final static String FORMAT_PARAM = "mode";
    final static String APP_ID_PARAM = "app_id";
    final static String APP_CODE_PARAM = "app_code";
    final static String RETRIEVAL_MODE = "retrieveLandmarks";
    final static String LOCATION_PARAM = "prox";
    final static String HERE_APP_ID = "9ZPA1cpoeK0TylWh48zU";
    final static String HERE_APP_CODE = "N8yPHCnNZEazezfYD1E6DA";


    public static URL buildUrl(String searchPhrase) {

        String wikiCompatibleSearchPhrase = searchPhrase.replace(" ", "_");

        Uri builtUri = Uri.parse(WIKIPEDIA_BASE_URL).buildUpon()
                .appendPath(wikiCompatibleSearchPhrase)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }


    public static URL buildUrl(Double lat, Double lon) {
        String locationData = String.valueOf(lat) + "," + String.valueOf(lon) + "," + "2000";
        Uri builtUri = Uri.parse(HERE_BASE_URL).buildUpon()
/*                .appendQueryParameter(APP_CODE_PARAM, HERE_APP_CODE)
                .appendQueryParameter(APP_ID_PARAM, HERE_APP_ID)
                .appendQueryParameter(FORMAT_PARAM, RETRIEVAL_MODE)*/
                .appendEncodedPath("reversegeocode.json?" + APP_CODE_PARAM + "=" + HERE_APP_CODE + "&" + APP_ID_PARAM + "=" + HERE_APP_ID + "&" + FORMAT_PARAM + "=" + RETRIEVAL_MODE + "&" + LOCATION_PARAM + "=" + locationData)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}