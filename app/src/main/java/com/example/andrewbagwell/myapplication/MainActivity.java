package com.example.andrewbagwell.myapplication;



import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;


import android.preference.PreferenceManager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private JSONObject movieDBSearchResults;

    private ArrayList<String> imagePathArray;
    private HashMap<String, String> imageToIdMap;

    private SharedPreferences mSharedPreferences;
    private String sortOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        GridLayoutManager layoutManager;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        recyclerView = findViewById(R.id.rv_posters);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        imagePathArray = new ArrayList<>();
        imageToIdMap = new HashMap<>();


        getStartPageMovieData();

    }

    public void getStartPageMovieData() {

        String baseUrlDB = "https://api.themoviedb.org/3/";
        String searchTopRatedUrl = "movie/top_rated?";
        String searchMostPopularUrl = "movie/popular?";
        String searchOptionSelected = searchMostPopularUrl;
        String apiKey = "api_key=API_KEY";

        try {
            //check connectivity
            ConnectivityManager cm =
                    (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if (!isConnected) {

                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                return;
            }

            sortOrder = mSharedPreferences.getString("list_preference", "most_popular");


            if (sortOrder.equals("most_popular")) {

                searchOptionSelected = searchMostPopularUrl;
            }

            else {

                searchOptionSelected = searchTopRatedUrl;
            }



            URL url = new URL(baseUrlDB+searchOptionSelected+apiKey);
            new MovieDatabaseQueryTask().execute(url);


        }

        catch (MalformedURLException e) {

            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.app_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Fragment mFragment = new SettingsFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.activity_main, mFragment).addToBackStack(null).commit();

        return true;
    }



    @Override
    public void onBackPressed() {

        //check connectivity
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {

            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }


        super.onBackPressed();

        if (! (sortOrder.equals(mSharedPreferences.getString("list_preference", "foobar"))) ) {

            getStartPageMovieData();
            recreate();

        }


    }

    public class MovieDatabaseQueryTask extends AsyncTask<URL, Void, String> {


        @Override
        protected String doInBackground(URL... urls) {

            movieDBSearchResults = null;

            try {

                movieDBSearchResults =  queryData(urls[0]);
            }

            catch (IOException e) {

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            String moviePosterImgPath;
            PosterAdapter adapter;

            if (movieDBSearchResults != null && !movieDBSearchResults.equals("")) {

                try {
                    JSONArray resultsJSONArray = movieDBSearchResults.getJSONArray("results");

                    for (int i = 0; i < resultsJSONArray.length(); i++) {

                        JSONObject resultJSONMovie = resultsJSONArray.getJSONObject(i);

                        moviePosterImgPath = resultJSONMovie.getString("poster_path");

                        imagePathArray.add("http://image.tmdb.org/t/p/w780/" + moviePosterImgPath);

                        imageToIdMap.put(imagePathArray.get(i), resultJSONMovie.getString("id"));

                    }

                    adapter = new PosterAdapter(MainActivity.this, imagePathArray.size(), imagePathArray, imageToIdMap);

                    recyclerView.setAdapter(adapter);

                }

                catch (JSONException e) {

                    e.printStackTrace();
                }
            }

        }

    }



    //helper method used to make the request

    private JSONObject queryData(URL urlData) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlData)
                .build();

        Response response = client.newCall(request).execute();

        //need to include some error handling for bad responses...

        try {

            JSONObject jsonObjectResponse = new JSONObject(response.body().string());

            return jsonObjectResponse;
        }

        catch (JSONException e) {

            e.printStackTrace();
        }

        return null;
    }


}
