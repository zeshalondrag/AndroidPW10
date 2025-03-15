package com.example.kinopoiskapi;

import java.util.List;

public class MovieResponse {
    private int id;
    private String name;
    private String description;
    private String year;
    private String rating;
    private String posterUrl;
    private List<String> genres;

    public MovieResponse(int id, String name, String description, String year,
                         String rating, String posterUrl, List<String> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.year = year;
        this.rating = rating;
        this.posterUrl = posterUrl;
        this.genres = genres;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getYear() { return year; }
    public String getRating() { return rating; }
    public String getPosterUrl() { return posterUrl; }
    public List<String> getGenres() { return genres; }
}