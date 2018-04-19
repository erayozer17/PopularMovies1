package com.erayo.popularmovies1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class FavoriteMoviesRecyclerViewAdapter extends RecyclerView.Adapter<FavoriteMoviesRecyclerViewAdapter.ViewHolder> {

    public List<Movie> movieList;
    private Context context;
    private LayoutInflater mInflater;

    public FavoriteMoviesRecyclerViewAdapter(List<Movie> list, Context context) {
        this.movieList = list;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public FavoriteMoviesRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.favorite_movies_row_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        holder.title.setText(movie.title);
        holder.average.setText(movie.overview);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView average;
        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.favorite_movie_title_tv);
            average = view.findViewById(R.id.favorite_movie_average_tv);
        }
    }
}
