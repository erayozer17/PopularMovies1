package com.erayo.popularmovies1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    @BindView(R.id.imageView) ImageView poster;
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.tv_release_date) TextView tv_date;
    @BindView(R.id.tv_overview) TextView tv_overview;
    @BindView(R.id.tv_rate) TextView tv_rating;

    private String poster_url, title, date, overview;
    private double rating;

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ButterKnife.bind(this);

        movie = getIntent().getParcelableExtra("movie");

        initValues();

        Picasso.with(this)
                .load(poster_url)
                .placeholder(R.drawable.searching)
                .error(R.drawable.not_found)
                .into(poster);

        tv_title.setText(title);
        tv_date.setText(date);
        tv_overview.setText(overview);

        tv_rating.setText(String.valueOf(rating));
        tv_rating.append("/10");

    }

    private void initValues(){
        try {
            poster_url = String.valueOf(ApiUtilities.imageUrl(movie.getPoster_path()));
            title = movie.getTitle();
            date = movie.getRelease_date();
            overview = movie.getOverview();
            rating = movie.getVote_average();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
