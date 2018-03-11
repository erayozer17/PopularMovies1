package com.erayo.popularmovies1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ClickListener,JsonUtil.Callback{

    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    List<Movie> mainListMovies;
    Toast toast;
    JsonUtil jsonUtil = new JsonUtil(this);
    static ApiUtilities.SortingType sortingType = ApiUtilities.SortingType.VOTEAVERAGE_DESC;
    Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            if (isConnected()){
                URL requestedUrl = ApiUtilities.mainScreenDiscoverMoviesUrl(sortingType);
                jsonUtil.execute(requestedUrl);
            } else {
                showToastForConnError();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        int numberOfColumns = 2;

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId){
            case (R.string.popularity_desc):
                try {
                    if (isConnected()){
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
        }

        return true;
    }

    private void updateMenu(){
        if (sortingType == ApiUtilities.SortingType.POPULARITY_DESC){
            mMenu.findItem(R.string.popularity_desc).setVisible(false);
            mMenu.findItem(R.string.voting_desc).setVisible(true);
        } else if (sortingType == ApiUtilities.SortingType.VOTEAVERAGE_DESC){
            mMenu.findItem(R.string.popularity_desc).setVisible(true);
            mMenu.findItem(R.string.voting_desc).setVisible(false);
        }
    }

    private boolean isConnected(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onResponse(List<Movie> movies) {
        mainListMovies = movies;
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
