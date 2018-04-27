package com.erayo.popularmovies1.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.erayo.popularmovies1.Movie;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PopularMovies.db";

    private Context mContext;

    public DbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_FOR_MOVIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_FOR_MOVIES);
        onCreate(db);
    }

    public void insertMovie(ContentValues cv){
        SQLiteDatabase db = getWritableDatabase();
        db.insert(DbContract.MovieEntry.MOVIE_TABLE_NAME, null, cv);
        db.close();
    }

    public Cursor getAllMovies() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectSql = "select * from " + DbContract.MovieEntry.MOVIE_TABLE_NAME;
        return db.rawQuery( selectSql, null );
    }

    public Cursor getMovie(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectSql = "select * from " + DbContract.MovieEntry.MOVIE_TABLE_NAME +
                " where " + DbContract.MovieEntry.MOVIE_ID + "=" + id;
        return db.rawQuery( selectSql, null );
    }

    public void deleteMovie(String id){
        SQLiteDatabase db = getWritableDatabase();
        String deleteSql = "delete from " + DbContract.MovieEntry.MOVIE_TABLE_NAME +
                " where " + DbContract.MovieEntry.MOVIE_ID + "=" + id;
        db.execSQL(deleteSql);
        db.close();
    }

    private static final String SQL_CREATE_ENTRIES_FOR_MOVIES =
            "CREATE TABLE " + DbContract.MovieEntry.MOVIE_TABLE_NAME + " (" +
                    DbContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                    DbContract.MovieEntry.MOVIE_TITLE + " TEXT," +
                    DbContract.MovieEntry.MOVIE_RELEASE_DATE + " TEXT," +
                    DbContract.MovieEntry.MOVIE_VOTE_AVERAGE + " REAL," +
                    DbContract.MovieEntry.MOVIE_ID + " INTEGER," +
                    DbContract.MovieEntry.MOVIE_OVERVIEW + " TEXT)";

    private static final String SQL_DELETE_ENTRIES_FOR_MOVIES =
            "DROP TABLE IF EXISTS " + DbContract.MovieEntry.MOVIE_TABLE_NAME;
}
