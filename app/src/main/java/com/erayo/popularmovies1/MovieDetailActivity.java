package com.erayo.popularmovies1;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.erayo.popularmovies1.db.DbContract;
import com.erayo.popularmovies1.db.DbHelper;
import com.erayo.popularmovies1.provider.ProviderContract;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieDetailActivity extends AppCompatActivity implements JsonUtil.Callback {

    public final static String REGULAR = "regular";
    public final static String FAVORITE = "favorite";
    public static String TAG = REGULAR;

    @BindView(R.id.imageView) ImageView poster;
    @BindView(R.id.tv_title) TextView tv_title;
    @BindView(R.id.tv_release_date) TextView tv_date;
    @BindView(R.id.tv_overview) TextView tv_overview;
    @BindView(R.id.tv_rate) TextView tv_rating;
    @BindView(R.id.star_imageButton) ImageButton star_image_button;
    @BindView(R.id.trailer_listview) ExpandableHeightListView trailer_listview;
    @BindView(R.id.comments_listview)ExpandableHeightListView comments_lisview;
    @BindView(R.id.scrollView2)ScrollView mScrollView;

    private String poster_url;
    private String title;
    private String date;
    private String overview;
    private double rating;
    private int id;

    private Movie movie;

    private DbHelper db;

    private List<Trailer> trailers;
    private List<Comment> comments;

    private int permission = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ButterKnife.bind(this);

        db = new DbHelper(getBaseContext());

        movie = getIntent().getParcelableExtra("movie");

        initValues();

        Picasso.with(this)
                .load(poster_url)
                .placeholder(R.drawable.searching)
                .error(R.drawable.not_found)
                .into(poster);

        String willBeFormattedDate = date.replace("-", "/");
        SimpleDateFormat input = new SimpleDateFormat("dd/MM/yy", new Locale("en"));
        SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy", new Locale("en"));
        try {
            tv_date.setText(output.format(input.parse(willBeFormattedDate)));
        } catch (ParseException e) {
            e.printStackTrace();
            tv_date.setText(R.string.not_available);
        }

        tv_title.setText(title);
        tv_overview.setText(overview);

        tv_rating.setText(String.valueOf(rating));
        tv_rating.append("/10");

        checkIfMovieIsFavoriteForStar();

        URL trailersUrl = null;
        try {
            trailersUrl = ApiUtilities.trailersUrl(id);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (movie.getPoster_path() != null && movie.getRelease_date() != null){
            //favorite movie sekmesinden ULASILMADIGINI gösterir
            TAG = REGULAR;
            new JsonUtil(this).execute(trailersUrl);
        }
    }

    private void initValues(){
        try {
            if (movie.getPoster_path() != null)
                poster_url = String.valueOf(ApiUtilities.imageUrl(movie.getPoster_path()));
            else {
                poster_url = "not available";
                TAG = FAVORITE;
            }
            title = movie.getTitle();
            if (movie.getRelease_date() != null)
                date = movie.getRelease_date();
            else
                date = "not available";
            overview = movie.getOverview();
            rating = movie.getVote_average();
            id = movie.getId();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.star_imageButton)
    public void starOnTouchSaveOrDelete(){
        if (star_image_button.getTag() == null || star_image_button.getTag().equals("off")){
            star_image_button.setImageResource(android.R.drawable.btn_star_big_on);
            star_image_button.setTag("on");
            ContentValues cv = new ContentValues();
            cv.put(DbContract.MovieEntry.MOVIE_TITLE, movie.getTitle());
            cv.put(DbContract.MovieEntry.MOVIE_OVERVIEW, movie.getOverview());
            cv.put(DbContract.MovieEntry.MOVIE_RELEASE_DATE, movie.getRelease_date());
            cv.put(DbContract.MovieEntry.MOVIE_ID, movie.getId());
            getContentResolver().insert(ProviderContract.INSERT_URI, cv);
        }
        else if (star_image_button.getTag().equals("on")){
            star_image_button.setImageResource(android.R.drawable.btn_star_big_off);
            star_image_button.setTag("off");
            getContentResolver().delete(ProviderContract.DELETE_URI, String.valueOf(movie.getId()), null);
        }
    }

    public void checkIfMovieIsFavoriteForStar(){
        if (getContentResolver().query(ProviderContract.GET_SINGLE_MOVIE_URI, null,
                String.valueOf(movie.getId()), null, null).getCount() == 0){
            star_image_button.setImageResource(android.R.drawable.btn_star_big_off);
            star_image_button.setTag("off");
        } else {
            star_image_button.setImageResource(android.R.drawable.btn_star_big_on);
            star_image_button.setTag("on");
        }
    }

    @Override
    public void onResponse(List list) {
        if (trailers == null) {
            trailers = list;
            TrailerListAdapter adapter = new TrailerListAdapter(getApplicationContext(), 5, (ArrayList<Trailer>) trailers);
            trailer_listview.setAdapter(adapter);
            trailer_listview.setExpanded(true);
            try {
                new JsonUtil(this).execute(ApiUtilities.commentsUrl(id));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        if (comments == null && permission != 0){
            comments = list;
            CommentsListAdapter adapter = new CommentsListAdapter(getApplicationContext(), 5, (ArrayList<Comment>) comments);
            comments_lisview.setAdapter(adapter);
            comments_lisview.setExpanded(true);
        }
        permission = 1;
    }
}

class TrailerListAdapter extends ArrayAdapter<Trailer>{

    private Context mContext;
    private List<Trailer> trailerList;

    public TrailerListAdapter(@NonNull Context context, int resources, ArrayList<Trailer> list) {
        super(context, resources, list);
        mContext = context;
        trailerList = list;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View trailerRow = inflater.inflate(R.layout.trailer_listview_row_layout, parent, false);

        TextView textView = trailerRow.findViewById(R.id.trailer_row_textview);

        final Trailer trailer = trailerList.get(position);

        textView.setText(trailer.trailerTitle);
        trailerRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.trailerUrl));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + trailer.trailerUrl));
                appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    mContext.startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    mContext.startActivity(webIntent);
                }
            }
        });
        return trailerRow;
    }
}

class CommentsListAdapter extends ArrayAdapter<Comment>{
    private Context mContext;
    private List<Comment> commentList;

    public CommentsListAdapter(@NonNull Context context, int resources, ArrayList<Comment> list) {
        super(context, resources, list);
        mContext = context;
        commentList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View trailerRow = inflater.inflate(R.layout.comment_listview_row_layout, parent, false);

        TextView author = trailerRow.findViewById(R.id.comment_author);
        TextView content = trailerRow.findViewById(R.id.comment_content);

        Comment comment = commentList.get(position);

        author.setText(comment.getAuthor());
        content.setText(comment.getContent());

        return trailerRow;
    }
}
