package com.example.andrewbagwell.myapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MovieDetailsActivity extends AppCompatActivity {

    private String id = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Intent intent = getIntent();
        id = intent.getStringExtra(Intent.EXTRA_TEXT);


        getDetailPageMovieData();


    }

    public void getDetailPageMovieData() {

       String baseUrlDB = "https://api.themoviedb.org/3/movie/";
       String apiKey = "?api_key=API_KEY";

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

            URL url = new URL(baseUrlDB+id+apiKey);

            new MovieDetailQueryTask().execute(url);


        }

        catch (MalformedURLException e) {

            e.printStackTrace();
        }

    }

    public class MovieDetailQueryTask extends AsyncTask<URL, Void, String> {

        JSONObject movieDetailSearchResults;

        @Override
        protected String doInBackground(URL... urls) {

            movieDetailSearchResults = null;

            try {

                movieDetailSearchResults =  queryData(urls[0]);
            }

            catch (IOException e) {

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if (movieDetailSearchResults != null && !movieDetailSearchResults.equals("")) {

                try {

                    ((TextView)findViewById(R.id.movie_title)).setText(movieDetailSearchResults.getString("title"));

                    Picasso.with(MovieDetailsActivity.this).load("http://image.tmdb.org/t/p/w342/" + movieDetailSearchResults.getString("poster_path")).into((ImageView)findViewById(R.id.movie_poster_detailActivity));

                    ((TextView)findViewById(R.id.movie_release_date)).setText(movieDetailSearchResults.getString("release_date"));

                    ((TextView)findViewById(R.id.movie_vote_average)).setText(movieDetailSearchResults.getString("vote_average"));

                    ((TextView)findViewById(R.id.movie_plot_summary)).setText(movieDetailSearchResults.getString("overview"));

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

            return new JSONObject(response.body().string());

        }

        catch (JSONException e) {

            e.printStackTrace();
        }

        return null;
    }

}
