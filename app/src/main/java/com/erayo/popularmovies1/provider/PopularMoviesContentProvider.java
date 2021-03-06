package com.erayo.popularmovies1.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.erayo.popularmovies1.db.DbContract;
import com.erayo.popularmovies1.db.DbHelper;

public class PopularMoviesContentProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int GET_MOVIE = 101;
    public static final int INSERT_MOVIE = 200;
    public static final int DELETE_MOVIE = 300;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public DbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {
            case MOVIES:
                retCursor =  dbHelper.getAllMovies();
                break;
            case GET_MOVIE:
                retCursor = dbHelper.getMovie(selection);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INSERT_MOVIE:
                dbHelper.insertMovie(values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case DELETE_MOVIE:
                dbHelper.deleteMovie(selection);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(ProviderContract.AUTHORITY, ProviderContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(ProviderContract.AUTHORITY, ProviderContract.GET_SINGLE_MOVIE, GET_MOVIE);
        uriMatcher.addURI(ProviderContract.AUTHORITY, ProviderContract.INSERT_MOVIES, INSERT_MOVIE);
        uriMatcher.addURI(ProviderContract.AUTHORITY, ProviderContract.DELETE_MOVIES, DELETE_MOVIE);

        return uriMatcher;
    }
}
