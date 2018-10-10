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
package com.codecool.zibi.landmarker.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codecool.zibi.landmarker.adapters.LandmarkAdapter;
import com.codecool.zibi.landmarker.R;
import com.codecool.zibi.landmarker.utilities.HereJsonUtils;
import com.codecool.zibi.landmarker.utilities.NetworkUtils;


import java.net.URL;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity implements LandmarkAdapter.LandmarkAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private LandmarkAdapter mLandmarkAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    private final double SAN_FRAN_LAT = 37.8075;
    private final double SAN_FRAN_LON = -122.4725;

    private final double NEW_YORK_LAT = 40.7128;
    private final double NEW_YORK_LON = -74.0059;

    private final double WASHINGTON_LAT = 38.9073;
    private final double WASHINGTON_LON = -77.0369;

    private final double LA_LAT = 34.0524;
    private final double LA_LON = -118.2439;

    private final double SEATTLE_LAT = 47.6061;
    private final double SEATTLE_LON = -122.3324;

    private final double CHICAGO_LAT = 41.8782;
    private final double CHICAGO_LON = -87.6301;

    double lat;
    double lon;
    int locationID;

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        System.out.println("TAG, onSavedInstanceState");

        double[] location = {lat, lon};
        outState.putDoubleArray("savedLocation", location);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState)
    {
        System.out.println("TAG, onRestoreInstanceState");
        double[] location = savedState.getDoubleArray("savedLocation");
        if (location != null){
            lat = location[0];
            lon = location[1];
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mRecyclerView = findViewById(R.id.recyclerview_landmark);

        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);


        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);


        mRecyclerView.setHasFixedSize(true);


        mLandmarkAdapter = new LandmarkAdapter(this);

        mRecyclerView.setAdapter(mLandmarkAdapter);


        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        loadLandmarkData();
    }

    private void loadLandmarkData(Bundle savedInstanceState) {

        double[] location = savedInstanceState.getDoubleArray("savedLocation");

        new FetchLandmarkTask().execute(location);
    }


    private void loadLandmarkData() {
        showLandmarkDataView();

        Intent intentThatStartedActivity = getIntent();

        if (intentThatStartedActivity.hasExtra(Intent.ACTION_CALL_BUTTON)){
            locationID = intentThatStartedActivity.getIntExtra(Intent.ACTION_CALL_BUTTON, 666);

        }

        switch (locationID) {
            case 666:  lat = 36.6666;
                lon = -112.6666;
                break;
            case R.id.btn_los_angeles:  lat = LA_LAT;
                lon = LA_LON;
                break;
            case R.id.btn_chicago:  lat = CHICAGO_LAT;
                lon = CHICAGO_LON;
                break;
            case R.id.btn_new_york:  lat = NEW_YORK_LAT;
                lon = NEW_YORK_LON;
                break;
            case R.id.btn_san_francisco:  lat = SAN_FRAN_LAT;
                lon = SAN_FRAN_LON;
                break;
            case R.id.btn_seattle:  lat = SEATTLE_LAT;
                lon = SEATTLE_LON;
                break;
            case R.id.btn_washington_dc:  lat = WASHINGTON_LAT;
                lon = WASHINGTON_LON;
                break;
            default: lat = 36.1088;
                lon = -112.1171;
                break;
        }

        double[] location = {lat, lon};
        new FetchLandmarkTask().execute(location);
    }



    private void showLandmarkDataView() {

        mErrorMessageDisplay.setVisibility(View.INVISIBLE);

        mRecyclerView.setVisibility(View.VISIBLE);
    }


    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(String landmarkName) {
        Context context = this;
        Class destinationClass = LandmarkDetailsActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, landmarkName);
        startActivity(intentToStartDetailActivity);
    }

    public class FetchLandmarkTask extends AsyncTask<double[], Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(double[]... params) {

            if (params.length == 0) {
                return null;
            }

            double[] location = params[0];
            double lat = location[0];
            double lon = location[1];
            URL landmarkRequestUrl = NetworkUtils.buildUrl(lat, lon);

            try {
                String jsonLandmarkResponse = NetworkUtils
                        .getResponseFromHttpUrl(landmarkRequestUrl);

                String[] simpleJsonLandmarkData = HereJsonUtils
                        .getSimpleLandmarkStringsFromJson(MainActivity.this, jsonLandmarkResponse);

                return simpleJsonLandmarkData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] landmarkData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (landmarkData != null) {
                showLandmarkDataView();
                mLandmarkAdapter.setLandmarkData(landmarkData);
            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.landmark, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            mLandmarkAdapter.setLandmarkData(null);
            loadLandmarkData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDetails(View view) {
        Intent intent = new Intent(this, LandmarkDetailsActivity.class);
        String locationName = "";
        intent.putExtra(EXTRA_MESSAGE, locationName);
        startActivity(intent);
    }
}