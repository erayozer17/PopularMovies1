package com.erayo.popularmovies1.provider;

import android.net.Uri;

public class ProviderContract {
    public static final String AUTHORITY = "com.erayo.popularmovies1";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
}
