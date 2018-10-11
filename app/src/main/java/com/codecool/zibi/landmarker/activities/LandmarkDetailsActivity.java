package com.codecool.zibi.landmarker.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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

public class LandmarkDetailsActivity extends AppCompatActivity {

    private ImageView mLandmarkThumbnail;
    private ProgressBar mLoadingIndicator;
    private TextView mLandmarkView;
    private Button mWikiButton;
    private Button mGoogleButton;

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
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {

                landmarkName = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
                mLandmarkView.setText(landmarkName);

                new FetchWikiContentTask().execute(landmarkName);
                new FetchWikiThumbnailTask().execute(landmarkName);
            }
        }
    }

    public class FetchWikiContentTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            URL wikiURL = NetworkUtils.buildUrlFromSearchphraseForWikipediaAPI(strings[0]);

            try {
                String jsonWikiResponse = NetworkUtils
                        .getResponseFromHttpUrl(wikiURL);

                String wikiContent = WikipediaJsonUtils.getWikipediaContentFromJson(LandmarkDetailsActivity.this, jsonWikiResponse);
                return wikiContent;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String wikiData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (wikiData != null) {
                showWikiDataView();
                mWikiButton.setText(wikiData);
            } else {
                showGoogleButtonWithThumbnail();
            }
        }
    }

    public class FetchWikiThumbnailTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            URL wikiURL = NetworkUtils.buildUrlFromSearchphraseForWikipediaAPI(strings[0]);
            URL thumbnailURLFromJson = null;

            try {
                String jsonWikiResponse = NetworkUtils
                        .getResponseFromHttpUrl(wikiURL);

                thumbnailURLFromJson = WikipediaJsonUtils.getWikipediaThumbnailURLFromJson(LandmarkDetailsActivity.this, jsonWikiResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Bitmap mIcon11 = null;
            try {
                InputStream in = thumbnailURLFromJson != null ? thumbnailURLFromJson.openStream() : null;
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap thumbnail) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (thumbnail != null) {
                showWikiDataView();
                mLandmarkThumbnail.setImageBitmap(thumbnail);
            } else {
                showGoogleButtonOnly();
            }
        }
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
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {

                addressString = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT).substring(0, intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT).indexOf('?'));
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
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {

                addressString = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT).substring(0, intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT).indexOf('?'));
            }
        }

        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);

        intent.putExtra(SearchManager.QUERY, addressString);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }


}
