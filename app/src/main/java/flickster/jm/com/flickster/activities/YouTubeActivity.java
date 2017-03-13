package flickster.jm.com.flickster.activities;

import android.os.Bundle;
import android.util.Log;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import flickster.jm.com.flickster.R;
import flickster.jm.com.flickster.models.Trailer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class YouTubeActivity extends YouTubeBaseActivity {
    private Gson gson;
    private OkHttpClient client = new OkHttpClient();
    private GsonBuilder gsonBuilder = new GsonBuilder();

    @BindView(R.id.player) YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube);
        ButterKnife.bind(this);

        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gson = gsonBuilder.create();

        String videoURL = this.getIntent().getStringExtra("video_url");
        if (videoURL != null) {
            fetchMovieTrailer(videoURL);
        }
    }

    private void fetchMovieTrailer(String URL) {
        Request request = new Request.Builder().url(URL).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Flickster", "Http request for movie trailers error");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseObj = new JSONObject(response.body().string());
                        JSONArray responseArray = responseObj.getJSONArray("results");
                        final List<Trailer> trailerList = gson.fromJson(responseArray.toString(),
                                new TypeToken<List<Trailer>>(){}.getType());

                        // Run on main thread to update UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (trailerList.size() > 0) {
                                    String videoKey = null;
                                    for (Trailer trailer : trailerList) {
                                        if (trailer.getType().equals("Trailer")
                                                && trailer.getSite().equals("YouTube")) {
                                            videoKey = trailer.getKey();
                                            break;
                                        }
                                    }
                                    if (videoKey == null) {
                                        // No Trailers, use Teaser
                                        videoKey = trailerList.get(0).getKey();
                                    }
                                    startYoutubePlayer(videoKey);
                                }
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

    void startYoutubePlayer(final String videoKey) {
        if (videoKey != null) {
            youTubePlayerView.initialize("AIzaSyCgSto97SawNYh8M6I1vtEhyih_4kK0gEk",
                    new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                            YouTubePlayer youTubePlayer, boolean b) {
                            youTubePlayer.cueVideo(videoKey);
                            youTubePlayer.play();
                        }
                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                            YouTubeInitializationResult youTubeInitializationResult) {
                        }
                    });
        }
    }
}
