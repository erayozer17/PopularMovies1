package com.erayo.popularmovies1;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JsonUtil extends AsyncTask<URL, Void, List> {

    private static final String POSTER_PATH = "poster_path";
    private static final String TITLE = "title";
    private static final String RELEASE_DATE = "release_date";
    private static final String OVERVIEW = "overview";
    private static final String VOTE_AVERAGE_INT = "vote_average";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String KEY = "key";
    private static final String AUTHOR = "author";
    private static final String CONTENT = "content";

    private static final String RESULTS = "results";

    static private List<Movie> response;
    private Callback callback;

    JsonUtil(Context context) {
        if (context instanceof Callback) {
            this.callback = (Callback) context;
        }
    }

    @Override
    protected List doInBackground(URL... urls) {
        List list;
        JSONObject jsonObject;

        if (urls.length == 0) {
            return null;
        }

        URL url = urls[0];
        try {

            String plainJsonResponse = getResponseFromHttpUrl(url);

            jsonObject = new JSONObject(plainJsonResponse);
            if (ApiUtilities.TAG.equals(ApiUtilities.MAIN)){
                list = prepareJsonMovies(jsonObject, RESULTS);
            } else if (ApiUtilities.TAG.equals(ApiUtilities.TRAILERS)){
                list = prepareJsonTrailers(jsonObject, RESULTS);
            } else if (ApiUtilities.TAG.equals(ApiUtilities.COMMENTS)){
                list = prepareJsonComments(jsonObject, RESULTS);
            } else {
                list = new ArrayList();
            }

            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List list) {
        callback.onResponse(list);
    }

    public String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    private List<Movie> prepareJsonMovies(JSONObject jsonObject, String nameOfArray) {
        List<Movie> list = new ArrayList<>();
        Movie movie;
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(nameOfArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                movie = new Movie();
                movie.setPoster_path(jsonArray.getJSONObject(i).optString(POSTER_PATH));
                movie.setOverview(jsonArray.getJSONObject(i).optString(OVERVIEW));
                movie.setRelease_date(jsonArray.getJSONObject(i).optString(RELEASE_DATE));
                movie.setTitle(jsonArray.getJSONObject(i).optString(TITLE));
                movie.setVote_average(jsonArray.getJSONObject(i).getDouble(VOTE_AVERAGE_INT));
                movie.setId(jsonArray.getJSONObject(i).getInt(ID));
                list.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<Trailer> prepareJsonTrailers(JSONObject jsonObject, String nameOfArray) {
        List<Trailer> list = new ArrayList<>();
        Trailer trailer;
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(nameOfArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                trailer = new Trailer();
                trailer.setTrailerTitle(jsonArray.getJSONObject(i).optString(NAME));
                trailer.setTrailerUrl(jsonArray.getJSONObject(i).optString(KEY));
                list.add(trailer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    private List prepareJsonComments(JSONObject jsonObject, String nameOfArray) {
        List<Comment> list = new ArrayList<>();
        Comment comment;
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(nameOfArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                comment = new Comment();
                comment.setAuthor(jsonArray.getJSONObject(i).optString(AUTHOR));
                comment.setContent(jsonArray.getJSONObject(i).optString(CONTENT));
                list.add(comment);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Movie> getResponse() {
        return response;
    }

    public void setResponse(List<Movie> response) {
        this.response = response;
    }

    public interface Callback {
        void onResponse(List list);
    }
}
