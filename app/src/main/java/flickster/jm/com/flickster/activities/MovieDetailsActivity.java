package flickster.jm.com.flickster.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import flickster.jm.com.flickster.R;
import flickster.jm.com.flickster.models.Movie;

public class MovieDetailsActivity extends AppCompatActivity {
    @BindView(R.id.rbMovieRating) RatingBar rbMovieRating;
    @BindView(R.id.ivMovieBackdrop) ImageView ivMovieBackdrop;
    @BindView(R.id.tvPopularity) TextView tvPopularity;
    @BindView(R.id.pbImageLoad) ProgressBar pbImageLoad;
    @BindView(R.id.tvMovieTitle) TextView tvMovieTitle;
    @Nullable @BindView(R.id.tvOverview) TextView tvOverview;

    Movie currentMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        pbImageLoad.setVisibility(View.VISIBLE);
        currentMovie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        tvMovieTitle.setText(currentMovie.getOriginalTitle());

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            tvOverview.setText(currentMovie.getOverview());
        }

        // Set Popularity
        float maxPopularity = getIntent().getFloatExtra("maxPopularity", 0);
        int popularity = (int)(currentMovie.getPopularity()/maxPopularity*100);
        String popularityString = "Popularity " + String.valueOf(popularity) + "%";
        tvPopularity.setText(popularityString);

        // Set Rating
        float adjustedRating = currentMovie.getVoteAverage()/2;
        rbMovieRating.setRating(adjustedRating);

        // Set Image
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        Picasso.with(this).load(currentMovie.getBackdropPath())
                .resize(metrics.widthPixels, 0)
                .into(ivMovieBackdrop, new Callback() {
                    @Override
                    public void onSuccess() {
                        pbImageLoad.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        pbImageLoad.setVisibility(View.GONE);
                    }
                });
    }

    public void showTrailer(View v) {
        Intent intent = new Intent(this, YouTubeActivity.class);
        intent.putExtra("video_url", currentMovie.getVideoURL());
        startActivity(intent);
    }
}
