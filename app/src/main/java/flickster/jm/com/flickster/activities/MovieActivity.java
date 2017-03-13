package flickster.jm.com.flickster.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import flickster.jm.com.flickster.R;
import flickster.jm.com.flickster.adapters.MovieArrayAdapter;
import flickster.jm.com.flickster.models.Movie;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieActivity extends AppCompatActivity {
    final static String nowPlayingURL = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    final static String popularURL = "https://api.themoviedb.org/3/movie/popular?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    private Gson gson;
    private List<Movie> movieList;
    private MovieArrayAdapter movieAdapter;
    @BindView(R.id.lvMovies) ListView lvItems;

    private OkHttpClient client = new OkHttpClient();
    private GsonBuilder gsonBuilder = new GsonBuilder();

    private float maxMoviePopularity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);

        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gson = gsonBuilder.create();

        movieList = new ArrayList<>();
        movieAdapter = new MovieArrayAdapter(this, movieList);
        lvItems.setAdapter(movieAdapter);

        loadMovies();
    }

    @OnItemClick(R.id.lvMovies)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Movie movie = movieList.get(position);
        if (movie.getMovieType() == Movie.MovieType.NORMAL) {
            Intent intent = new Intent(this, MovieDetailsActivity.class);
            intent.putExtra("movie", Parcels.wrap(movie));
            intent.putExtra("maxPopularity", maxMoviePopularity);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, YouTubeActivity.class);
            intent.putExtra("video_url", movie.getVideoURL());
            startActivity(intent);
        }
    }

    private void loadMovies() {
        Request request = new Request.Builder().url(nowPlayingURL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Flickster", "Http request for movies error");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObj = new JSONObject(response.body().string());
                        JSONArray responseArray = responseObj.getJSONArray("results");
                        final List<Movie> newList = gson.fromJson(responseArray.toString(),
                                new TypeToken<List<Movie>>(){}.getType());

                        // Set Max Popularity Value
                        for (Movie movie : newList) {
                            if (maxMoviePopularity < movie.getPopularity()) {
                                maxMoviePopularity = movie.getPopularity();
                            }
                        }

                        // Run on main thread to update UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                movieList.addAll(newList);
                                movieAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        Log.d("Flickster", "Failed to deserialize JSON");
                    }
                } else {
                    Log.d("Flickster", "HTTP response error");
                }
            }
        });
    }
}
