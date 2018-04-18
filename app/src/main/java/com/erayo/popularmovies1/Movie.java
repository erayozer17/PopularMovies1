package com.erayo.popularmovies1;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Movie implements Parcelable{
    String poster_path;
    String title;
    String release_date;
    String overview;
    double vote_average;
    int id;

    private Movie(Parcel in){
        poster_path = in.readString();
        title = in.readString();
        release_date = in.readString();
        overview = in.readString();
        vote_average = in.readDouble();
        id = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(poster_path);
        parcel.writeString(title);
        parcel.writeString(release_date);
        parcel.writeString(overview);
        parcel.writeDouble(vote_average);
        parcel.writeInt(id);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
}
