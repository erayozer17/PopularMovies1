package com.erayo.popularmovies1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.erayo.popularmovies1.db.DbContract;
import com.erayo.popularmovies1.db.DbHelper;
import com.erayo.popularmovies1.provider.ProviderContract;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ClickListener,JsonUtil.Callback{

    private static final String FAVORITE_MOVIES = "favorite_movies";

    RecyclerViewAdapter adapter;
    FavoriteMoviesRecyclerViewAdapter favoriteMoviesRecyclerViewAdapter;
    RecyclerView recyclerView;
    List<Movie> mainListMovies;
    ArrayList<Movie> favoriteMovies;
    Toast toast;
    JsonUtil jsonUtil = new JsonUtil(this);
    static ApiUtilities.SortingType sortingType = ApiUtilities.SortingType.VOTEAVERAGE_DESC;
    Menu mMenu;
    DbHelper db;
    int numberOfColumns = 2;
    static int rotationController = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        db = new DbHelper(getBaseContext());

        if(savedInstanceState != null && rotationController % 2 == 1) {
            if (savedInstanceState.containsKey(FAVORITE_MOVIES)) {
                List<Movie> list = savedInstanceState.getParcelableArrayList(FAVORITE_MOVIES);
                resurrectFavoriteMovies(list);
                savedInstanceState.clear();
            }
        } else {
            try {
                if (isConnected()){
                    URL requestedUrl = ApiUtilities.mainScreenDiscoverMoviesUrl(sortingType);
                    jsonUtil.execute(requestedUrl);
                    recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
                } else {
                    showToastForConnError();
                    showFavoriteMovies();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemClick(int clickedItemPosition) {
        Movie movie = mainListMovies.get(clickedItemPosition);

        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, mMenu);

        mMenu = menu;

        mMenu.add(Menu.NONE,
                R.string.popularity_desc,
                Menu.NONE,
                "Popularity Desc")
                .setVisible(true)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        mMenu.add(Menu.NONE,
                R.string.voting_desc,
                Menu.NONE,
                "Voting Desc")
                .setVisible(false)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        mMenu.add(Menu.NONE,
                R.string.favorites,
                Menu.NONE,
                "Favorites")
                .setVisible(false)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId){
            case (R.string.popularity_desc):
                try {
                    if (isConnected()){
                        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
                        URL requestedUrl = ApiUtilities.mainScreenDiscoverMoviesUrl(ApiUtilities.SortingType.POPULARITY_DESC);
                        new JsonUtil(this).execute(requestedUrl);
                        sortingType = ApiUtilities.SortingType.POPULARITY_DESC;
                        updateMenu();
                    } else {
                        showToastForConnError();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                break;
            case (R.string.voting_desc):
                try {
                    if (isConnected()){
                        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
                        URL requestedUrl = ApiUtilities.mainScreenDiscoverMoviesUrl(ApiUtilities.SortingType.VOTEAVERAGE_DESC);
                        new JsonUtil(this).execute(requestedUrl);
                        sortingType = ApiUtilities.SortingType.VOTEAVERAGE_DESC;
                        updateMenu();
                    } else {
                        showToastForConnError();
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                break;
            case (R.string.favorites):
                showFavoriteMovies();
                break;
        }

        return true;
    }

    private void updateMenu(){
        if (sortingType == ApiUtilities.SortingType.POPULARITY_DESC){
            mMenu.findItem(R.string.favorites).setVisible(true);
            mMenu.findItem(R.string.popularity_desc).setVisible(false);
            mMenu.findItem(R.string.voting_desc).setVisible(false);
        } else if (sortingType == ApiUtilities.SortingType.VOTEAVERAGE_DESC){
            mMenu.findItem(R.string.favorites).setVisible(false);
            mMenu.findItem(R.string.popularity_desc).setVisible(true);
            mMenu.findItem(R.string.voting_desc).setVisible(false);
        } else if (sortingType == ApiUtilities.SortingType.FAVORITES){
            mMenu.findItem(R.string.favorites).setVisible(false);
            mMenu.findItem(R.string.popularity_desc).setVisible(false);
            mMenu.findItem(R.string.voting_desc).setVisible(true);
        }
    }

    private void showFavoriteMovies(){
        sortingType = ApiUtilities.SortingType.FAVORITES;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoriteMovies = new ArrayList<>();
        Cursor cursor = getContentResolver().query(ProviderContract.CONTENT_URI, null,
                null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Movie movie = new Movie();
            movie.setTitle(cursor.getString(cursor.getColumnIndex(DbContract.MovieEntry.MOVIE_TITLE)));
            movie.setOverview(cursor.getString(cursor.getColumnIndex(DbContract.MovieEntry.MOVIE_OVERVIEW)));
            favoriteMovies.add(movie);
            cursor.moveToNext();
        }
        cursor.close();
        favoriteMoviesRecyclerViewAdapter = new FavoriteMoviesRecyclerViewAdapter(favoriteMovies, this);
        recyclerView.setAdapter(favoriteMoviesRecyclerViewAdapter);
        favoriteMoviesRecyclerViewAdapter.notifyDataSetChanged();
        if (isConnected()){
            updateMenu();
        }
    }

    private void resurrectFavoriteMovies(List<Movie> favoriteMovies){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoriteMoviesRecyclerViewAdapter = new FavoriteMoviesRecyclerViewAdapter(favoriteMovies, this);
        recyclerView.setAdapter(favoriteMoviesRecyclerViewAdapter);
        favoriteMoviesRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(FAVORITE_MOVIES, favoriteMovies);
        rotationController++;
        super.onSaveInstanceState(outState);
    }

    private boolean isConnected(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onResponse(List list) {
        mainListMovies = list;
        adapter = new RecyclerViewAdapter(mainListMovies, this, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void showToastForConnError() {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(MainActivity.this, R.string.con_warn, Toast.LENGTH_LONG);
        toast.show();
    }
}
