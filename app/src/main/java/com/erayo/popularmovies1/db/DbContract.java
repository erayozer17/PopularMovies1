package com.erayo.popularmovies1.db;

import android.provider.BaseColumns;

public final class DbContract {
    private DbContract(){}

    public static class MovieEntry implements BaseColumns{
        public static final String MOVIE_TABLE_NAME = "movies";
        public static final String MOVIE_TITLE = "title";
        public static final String MOVIE_OVERVIEW = "overview";
        public static final String MOVIE_RELEASE_DATE = "release_date";
        public static final String MOVIE_VOTE_AVERAGE = "vote_average";
        public static final String MOVIE_ID = "movie_id";


    }
}
