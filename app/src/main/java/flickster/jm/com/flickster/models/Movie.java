package flickster.jm.com.flickster.models;

import org.parceler.Parcel;

/**
 * Created by Jared12 on 3/7/17.
 */

@Parcel
public class Movie {
    public enum MovieType {
        NORMAL,
        POPULAR
    }

    int id;
    String posterPath;
    String backdropPath;
    String originalTitle;
    String overview;
    float popularity;
    float voteAverage;
    MovieType movieType;

    public Movie() {
    }

    public int getId() {
        return id;
    }

    public String getPosterPath() {
        // https://image.tmdb.org/t/p/w342/45Y1G5FEgttPAwjTYic6czC9xCn.jpg
        return String.format("https://image.tmdb.org/t/p/w342/%s", posterPath);
    }

    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/w780/%s", backdropPath);
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public float getPopularity() {
        return popularity;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public MovieType getMovieType() {
        if (voteAverage >= 5.0) {
            return MovieType.POPULAR;
        } else {
            return MovieType.NORMAL;
        }
    }
}