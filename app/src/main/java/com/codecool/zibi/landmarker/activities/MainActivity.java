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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codecool.zibi.landmarker.adapters.LandmarkAdapter;
import com.codecool.zibi.landmarker.R;
import com.codecool.zibi.landmarker.model.Landmark;
import com.codecool.zibi.landmarker.utilities.GmapsJsonUtils;
import com.codecool.zibi.landmarker.utilities.NetworkUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


import java.net.URL;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity implements LandmarkAdapter.LandmarkAdapterOnClickHandler,
        LandmarkAdapter.LandmarkAdapterOnLongClickHandler,
        android.support.v4.app.LoaderManager.LoaderCallbacks<Landmark[]>
{

    private RecyclerView mRecyclerView;
    private LandmarkAdapter mLandmarkAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private FusedLocationProviderClient mFusedLocationClient;
    private MenuItem mPlanTourItem;
    private double[] locationArray = new double[2];
    private static final int LANDMARK_LOADER_ID = 21;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mRecyclerView = findViewById(R.id.recyclerview_landmark);
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mLandmarkAdapter = new LandmarkAdapter(this, this);
        mRecyclerView.setAdapter(mLandmarkAdapter);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        loadLandmarkData();
    }




    private void loadLandmarkData() {
        showLandmarkDataView();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    42);
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.d("Location data", "success");
                            locationArray[0] = location.getLatitude();
                            locationArray[1] = location.getLongitude();

                            Bundle locationBundle = new Bundle();
                            locationBundle.putDoubleArray("location", locationArray);
                            getSupportLoaderManager().initLoader(LANDMARK_LOADER_ID, locationBundle, MainActivity.this);
                        } else {
                            Log.e("Location data", "location is null");
                            //showErrorMessage();
                        }
                    }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Onfailure location", e.getClass().getSimpleName() + e.getMessage());
                //showErrorMessage();
            }
        });



    }



    private void showLandmarkDataView() {

        mErrorMessageDisplay.setVisibility(View.INVISIBLE);

        mRecyclerView.setVisibility(View.VISIBLE);
    }


    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showCheckBoxesForLandmarks(){
    }


    @Override
    public void onClick(Landmark landmark, View view) {
        Context context = this;
        if (mPlanTourItem.isVisible()){
            //TODO: logic for checkboxes: make method
            //view.getBackground().setColorFilter(Color.parseColor("#00ff00"), PorterDuff.Mode.LIGHTEN);
            landmark.setSelected(true);
            view.refreshDrawableState();
        } else {
            Class destinationClass = LandmarkDetailsActivity.class;
            Intent intentToStartDetailActivity = new Intent(context, destinationClass);
            intentToStartDetailActivity.putExtra("name", landmark.getName());
            intentToStartDetailActivity.putExtra("location", landmark.getLocation());
            startActivity(intentToStartDetailActivity);
        }



    }

    @Override
    public boolean onLongClick(Landmark landmark, View view) {
        for (Landmark lm : mLandmarkAdapter.getLandmarkData()) {
            if (lm.selected){
                lm.setSelected(false);
            }
        }

        mPlanTourItem.setVisible(true);
        return false;
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Landmark[]> onCreateLoader(int i, @Nullable final Bundle bundle) {
        return new AsyncTaskLoader<Landmark[]>(this) {
            Landmark[] mLandmarkData = null;

            @Override
            protected void onStartLoading() {
                if (mLandmarkData != null) {
                    deliverResult(mLandmarkData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }
            @Nullable
            @Override
            public Landmark[] loadInBackground() {
                double[] location = bundle.getDoubleArray("location");
                if (location == null) {
                    return null;
                }

                double lat = location[0];
                double lon = location[1];
                URL landmarkRequestUrl = NetworkUtils.buildUrlFromCoordinatesForGmapsAPI(lat, lon);

                try {
                    String jsonLandmarkResponse = NetworkUtils
                            .getResponseFromHttpUrl(landmarkRequestUrl);

                    Landmark[] landmarkData = GmapsJsonUtils.getLandmarkObjectsFromJson(jsonLandmarkResponse, lat, lon);

                    return landmarkData;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Landmark[] data) {
                mLandmarkData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Landmark[]> loader, Landmark[] landmarks) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (landmarks != null) {
            showLandmarkDataView();
            mLandmarkAdapter.setLandmarkData(landmarks);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Landmark[]> loader) {

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.landmark, menu);
        mPlanTourItem = menu.findItem(R.id.action_plan_tour);
        mPlanTourItem.setVisible(false);

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

        if (id == R.id.action_plan_tour) {
            //TODO: api call
            String mapsUrl = "http://maps.google.com/maps?daddr=";
            String currentLocation = String.valueOf(locationArray[0]) + "," + String.valueOf(locationArray[1]);
            String baseUrl = mapsUrl + currentLocation;
            StringBuilder geolocationsQueryString = new StringBuilder(baseUrl);

            for (Landmark landmark : mLandmarkAdapter.getLandmarkData()) {
                if (landmark.selected){
                    Location location = landmark.getLocation();
                    geolocationsQueryString.append("+to:");
                    geolocationsQueryString.append(String.valueOf(location.getLatitude()));
                    geolocationsQueryString.append(',');
                    geolocationsQueryString.append(String.valueOf(location.getLongitude()));
                    landmark.setSelected(false);
                }
            }

            Uri addressUri = Uri.parse(geolocationsQueryString.toString());
            System.out.println("GEOURIMULTIPLEPOINT: " + addressUri.toString());

            Intent intent = new Intent(Intent.ACTION_VIEW);


            intent.setData(addressUri);

            if (intent.resolveActivity(getPackageManager()) != null) {
                mPlanTourItem.setVisible(false);
                startActivity(intent);
            }
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