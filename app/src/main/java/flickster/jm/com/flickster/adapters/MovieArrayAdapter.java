package flickster.jm.com.flickster.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import flickster.jm.com.flickster.R;
import flickster.jm.com.flickster.models.Movie;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by Jared12 on 3/7/17.
 */

public class MovieArrayAdapter extends ArrayAdapter<Movie> {
    public MovieArrayAdapter(Context context,List<Movie> movies) {
        super(context, android.R.layout.simple_list_item_1, movies);
    }

    @Override
    public int getViewTypeCount() {
        return Movie.MovieType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getMovieType().ordinal();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (convertView == null) {
            convertView = getInflatedLayoutForType(viewType, parent);
        }

        Movie movie = getItem(position);
        if (movie != null) {
            if (viewType == Movie.MovieType.NORMAL.ordinal()) {
                final NormalViewHolder holder = (NormalViewHolder)convertView.getTag();

                // Reset cell
                holder.ivImage.setImageResource(0);
                holder.pbImageLoad.setVisibility(View.VISIBLE);
                Picasso.with(getContext()).cancelRequest(holder.ivImage); // Without this callback never gets called for first cell

                // Set cell values
                holder.tvTitle.setText(movie.getOriginalTitle());
                holder.tvOverview.setText(movie.getOverview());

                // Set image URL based on orientation
                String imageURL = movie.getPosterPath();
                int orientation = getContext().getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    imageURL = movie.getBackdropPath();
                }

                Picasso.with(getContext()).load(imageURL)
                        .fit()
                        .noFade()
                        .transform(new RoundedCornersTransformation(12, 0))
                        .into(holder.ivImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.pbImageLoad.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                holder.pbImageLoad.setVisibility(View.GONE);
                            }
                        });
            } else if (viewType == Movie.MovieType.POPULAR.ordinal()) {
                final PopularViewHolder holder = (PopularViewHolder)convertView.getTag();

                // Reset cell
                holder.ivImage.setImageResource(0);
                holder.ivPlayImage.setVisibility(View.GONE);
                holder.pbImageLoad.setVisibility(View.VISIBLE);
                Picasso.with(getContext()).cancelRequest(holder.ivImage); // Without this callback never gets called for first cell

                // Set Image
                DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
                Picasso.with(getContext()).load(movie.getBackdropPath())
                        .resize(metrics.widthPixels - 16, 0)
                        .transform(new RoundedCornersTransformation(12, 0))
                        .into(holder.ivImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.pbImageLoad.setVisibility(View.GONE);
                                holder.ivPlayImage.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError() {
                                holder.pbImageLoad.setVisibility(View.GONE);
                                holder.ivPlayImage.setVisibility(View.GONE);
                            }
                        });
            }
        }

        return convertView;
    }

    private View getInflatedLayoutForType(int type, ViewGroup parent) {
        if (type == Movie.MovieType.NORMAL.ordinal()) {
            View newView = LayoutInflater.from(getContext()).inflate(R.layout.item_movie, parent, false);
            newView.setTag(new NormalViewHolder(newView));
            return newView;
        } else if (type == Movie.MovieType.POPULAR.ordinal()) {
            View newView = LayoutInflater.from(getContext()).inflate(R.layout.item_movie_popular, parent, false);
            newView.setTag(new PopularViewHolder(newView));
            return newView;
        } else {
            return null;
        }
    }

    static class NormalViewHolder {
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvOverview) TextView tvOverview;
        @BindView(R.id.ivMovieImage) ImageView ivImage;
        @BindView(R.id.pbImageLoad) ProgressBar pbImageLoad;

        public NormalViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class PopularViewHolder {
        @BindView(R.id.ivMovieImage) ImageView ivImage;
        @BindView(R.id.ivPlayImage) ImageView ivPlayImage;
        @BindView(R.id.pbImageLoad) ProgressBar pbImageLoad;

        public PopularViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
