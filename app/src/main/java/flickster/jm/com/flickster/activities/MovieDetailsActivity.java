package flickster.jm.com.flickster.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import flickster.jm.com.flickster.R;
import flickster.jm.com.flickster.models.Movie;

public class MovieDetailsActivity extends AppCompatActivity {
    @BindView(R.id.rbMovieRating) RatingBar rbMovieRating;
    @BindView(R.id.ivMovieBackdrop) ImageView ivMovieBackdrop;
    @BindView(R.id.tvMovieTitle) TextView tvMovieTitle;
    @BindView(R.id.tvOverview) TextView tvOverview;
    @BindView(R.id.tvPopularity) TextView tvPopularity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        Movie currentMovie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        tvMovieTitle.setText(currentMovie.getOriginalTitle());
        tvOverview.setText(currentMovie.getOverview());

        // Set Popularity
        int popularity = getIntent().getIntExtra("popularity", 1);
        String popularityString = "Popularity #" + String.valueOf(popularity);
        tvPopularity.setText(popularityString);

        // Set Rating
        float adjustedRating = currentMovie.getVoteAverage()/2;
        rbMovieRating.setRating(adjustedRating);

        // Set Image
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        Picasso.with(this).load(currentMovie.getBackdropPath())
                .resize(metrics.widthPixels, 0)
                .into(ivMovieBackdrop);
    }
}
