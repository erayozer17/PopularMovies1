package com.erayo.popularmovies1.provider;

import android.net.Uri;

public class ProviderContract {
    public static final String AUTHORITY = "com.erayo.popularmovies1";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = "movies";
    public static final String INSERT_MOVIES = "insert_movies";
    public static final String DELETE_MOVIES = "delete_movies";
    public static final String GET_SINGLE_MOVIE = "get_single_movie";

    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
    public static final Uri INSERT_URI = BASE_CONTENT_URI.buildUpon().appendPath(INSERT_MOVIES).build();
    public static final Uri DELETE_URI = BASE_CONTENT_URI.buildUpon().appendPath(DELETE_MOVIES).build();
    public static final Uri GET_SINGLE_MOVIE_URI = BASE_CONTENT_URI.buildUpon().appendPath(GET_SINGLE_MOVIE).build();
}
