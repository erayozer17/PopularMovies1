package com.erayo.popularmovies1;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

class ApiUtilities {
    private static final String API_KEY = BuildConfig.API_KEY;
    public static final String MAIN = "main";
    public static final String TRAILERS = "trailers";
    public static final String COMMENTS = "comments";
    public static String TAG;

    static URL imageUrl(String imageUrl) throws MalformedURLException {
        imageUrl = imageUrl.substring(1);
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("http")
                .authority("image.tmdb.org")
                .appendPath("t")
                .appendPath("p")
                .appendPath("w342")
                .appendPath(imageUrl);
        return new URL(uriBuilder.build().toString());
    }

    static URL mainScreenDiscoverMoviesUrl(SortingType sortBy) throws MalformedURLException {
        TAG = MAIN;
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(sortBy.str())
                .appendQueryParameter("api_key", API_KEY);
        return new URL(uriBuilder.build().toString());
    }

    static URL trailersUrl(int id) throws MalformedURLException{
        TAG = TRAILERS;
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(String.valueOf(id))
                .appendPath("videos")
                .appendQueryParameter("api_key", API_KEY);
        return new URL(uriBuilder.build().toString());
    }

    static URL commentsUrl(int id) throws MalformedURLException{
        TAG = COMMENTS;
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(String.valueOf(id))
                .appendPath("reviews")
                .appendQueryParameter("api_key", API_KEY);
        return new URL(uriBuilder.build().toString());
    }

    enum SortingType{
        POPULARITY_DESC("popular"),
        VOTEAVERAGE_DESC("top_rated"),
        FAVORITES("favorites");

        private String str;

        SortingType(String str) {
            this.str = str;
        }

        public String str() {
            return str;
        }
    }
}
