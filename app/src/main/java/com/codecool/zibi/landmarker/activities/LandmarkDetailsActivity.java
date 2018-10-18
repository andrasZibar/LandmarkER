package com.codecool.zibi.landmarker.activities;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codecool.zibi.landmarker.R;
import com.codecool.zibi.landmarker.utilities.NetworkUtils;
import com.codecool.zibi.landmarker.utilities.WikipediaJsonUtils;

import java.io.InputStream;
import java.net.URL;

public class LandmarkDetailsActivity extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Object[]>{

    private ImageView mLandmarkThumbnail;
    private ProgressBar mLoadingIndicator;
    private TextView mLandmarkView;
    private Button mWikiButton;
    private Button mGoogleButton;
    private static final int DETAILS_LOADER_ID = 22;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark_details);


        mWikiButton = findViewById(R.id.btn_wiki_search);

        mGoogleButton = findViewById(R.id.btn_google_search);

        mLandmarkView = findViewById(R.id.tv_location_name);

        mLandmarkThumbnail = findViewById(R.id.wiki_thumbnail);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        loadWikiData();


    }

    private void loadWikiData() {
        Intent intentThatStartedThisActivity = getIntent();

        String landmarkName;

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("name")) {

                landmarkName = intentThatStartedThisActivity.getStringExtra("name");
                mLandmarkView.setText(landmarkName);

                /*new FetchWikiContentTask().execute(landmarkName);
                new FetchWikiThumbnailTask().execute(landmarkName);*/
                Bundle queryBundle = new Bundle();
                queryBundle.putString("landmarkName", landmarkName);
                getSupportLoaderManager().initLoader(DETAILS_LOADER_ID, queryBundle, LandmarkDetailsActivity.this);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Object[]> onCreateLoader(int i, @Nullable final Bundle bundle) {
        return new AsyncTaskLoader<Object[]>(this) {
            Object[] mDetailsData = null;

            @Override
            protected void onStartLoading() {
                if (mDetailsData != null) {
                    deliverResult(mDetailsData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public Object[] loadInBackground() {

                URL wikiURL = NetworkUtils.buildUrlFromSearchphraseForWikipediaAPI(bundle.getString("landmarkName"));
                URL thumbnailURLFromJson = null;
                Object[] objects = new Object[2];

                try {
                    String jsonWikiResponse = NetworkUtils
                            .getResponseFromHttpUrl(wikiURL);

                    String wikiContent = WikipediaJsonUtils.getWikipediaContentFromJson(LandmarkDetailsActivity.this, jsonWikiResponse);
                    thumbnailURLFromJson = WikipediaJsonUtils.getWikipediaThumbnailURLFromJson(LandmarkDetailsActivity.this, jsonWikiResponse);
                    objects[0] = wikiContent;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                Bitmap mIcon11 = null;
                try {
                    InputStream in = thumbnailURLFromJson != null ? thumbnailURLFromJson.openStream() : null;
                    mIcon11 = BitmapFactory.decodeStream(in);
                    objects[1] = mIcon11;
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                return objects;
            }

            public void deliverResult(Object[] data) {
                mDetailsData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object[]> loader, Object[] objects) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (objects[0] != null && objects[1] != null) {
            showWikiDataView();
            mWikiButton.setText((CharSequence) objects[0]);
            mLandmarkThumbnail.setImageBitmap((Bitmap) objects[1]);
        } else if (objects[1] != null){
            showGoogleButtonWithThumbnail();
        } else {
            showGoogleButtonOnly();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object[]> loader) {

    }



    void showWikiDataView(){
        mLandmarkThumbnail.setVisibility(View.VISIBLE);
        mWikiButton.setVisibility(View.VISIBLE);
        mGoogleButton.setVisibility(View.INVISIBLE);
    }

    void showGoogleButtonOnly(){
        mLandmarkThumbnail.setVisibility(View.INVISIBLE);
        mWikiButton.setVisibility(View.INVISIBLE);
        mGoogleButton.setVisibility(View.VISIBLE);
    }

    void showGoogleButtonWithThumbnail(){
        mLandmarkThumbnail.setVisibility(View.VISIBLE);
        mWikiButton.setVisibility(View.INVISIBLE);
        mGoogleButton.setVisibility(View.VISIBLE);
    }

    void launchMap(View view){
        String addressString = "";

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("name")) {

                addressString = intentThatStartedThisActivity.getStringExtra("name");
            }
        }

        String queryString = addressString.replace(" ", "+");

        Uri addressUri = Uri.parse("geo:0,0?q=" + queryString);
        System.out.println("GEOURI: " + addressUri.toString());

        Intent intent = new Intent(Intent.ACTION_VIEW);


        intent.setData(addressUri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    void launchGoogleSearch(View view){
        String addressString = "";

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra("name")) {

                addressString = intentThatStartedThisActivity.getStringExtra("name");
            }
        }

        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);

        intent.putExtra(SearchManager.QUERY, addressString);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }


}
