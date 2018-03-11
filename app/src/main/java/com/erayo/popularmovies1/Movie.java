package com.erayo.popularmovies1;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Movie {
    String poster_path;
    String title;
    String release_date;
    String overview;
    double vote_average;
}
