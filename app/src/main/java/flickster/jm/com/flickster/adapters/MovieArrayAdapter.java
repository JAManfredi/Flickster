package flickster.jm.com.flickster.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder)convertView.getTag();
        } else {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_movie, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        Movie movie = getItem(position);

        // Reset cell
        holder.ivImage.setImageResource(0);
        holder.pbImage.setVisibility(View.VISIBLE);

        // Set cell values
        holder.tvTitle.setText(movie.getOriginalTitle());
        holder.tvOverview.setText(movie.getOverview());

        // Set image URL based on orientation
        String imageURL = movie.getPosterPath();
        int orientation = getContext().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imageURL = movie.getBackdropPath();
        }

        Picasso.with(getContext()).load(imageURL).fit().centerInside()
                .transform(new RoundedCornersTransformation(12, 0))
                .into(holder.ivImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.pbImage.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        holder.pbImage.setVisibility(View.INVISIBLE);
                    }
                });

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvOverview) TextView tvOverview;
        @BindView(R.id.ivMovieImage) ImageView ivImage;
        @BindView(R.id.pbImage) ProgressBar pbImage;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
