package com.erayo.popularmovies1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView poster;
    private TextView tv_title, tv_date, tv_overview, tv_rating;

    private String poster_url, title, date, overview;
    private double rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        init();
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

    private void init() {
        poster = findViewById(R.id.imageView);
        tv_title = findViewById(R.id.tv_title);
        tv_date = findViewById(R.id.tv_release_date);
        tv_overview = findViewById(R.id.tv_overview);
        tv_rating = findViewById(R.id.tv_rate);
    }

    private void initValues(){
        poster_url = getIntent().getExtras().getString("poster_url");
        title = getIntent().getExtras().getString("title");
        date = getIntent().getExtras().getString("release_date");
        overview = getIntent().getExtras().getString("overview");
        rating = getIntent().getExtras().getDouble("rating");
    }
}
