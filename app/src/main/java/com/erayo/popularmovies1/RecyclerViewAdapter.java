package com.erayo.popularmovies1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    final private ClickListener mOnClickListener;
    public List<Movie> movieList;
    private Context context;
    private LayoutInflater mInflater;

    RecyclerViewAdapter(List<Movie> list, ClickListener clickListener, Context context){
        this.movieList = list;
        this.mOnClickListener = clickListener;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_view_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        try {
            Movie movie = movieList.get(position);

            String poster_url = String.valueOf(ApiUtilities.imageUrl(movie.getPoster_path()));

            Picasso.with(context)
                    .load(poster_url)
                    .error(R.drawable.not_found)
                    .placeholder(R.drawable.searching)
                    .fit()
                    .into(holder.imageView);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
       return movieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        ImageView imageView;

        public ViewHolder(View view){
            super(view);
            imageView = view.findViewById(R.id.adapter_image_view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onItemClick(clickedPosition);
        }
    }

    public interface ClickListener{
        void onItemClick(int clickedItemPosition);
    }
}
