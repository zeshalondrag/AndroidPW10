package com.example.kinopoiskapi;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MovieResponse {
    @SerializedName("filmId")
    private int id;

    @SerializedName("nameRu")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("year")
    private String year;

    @SerializedName("rating")
    private String rating;

    @SerializedName("posterUrl")
    private String posterUrl;

    @SerializedName("genres")
    private List<Genre> genres;

    // Внутренний класс для жанров
    public static class Genre {
        @SerializedName("genre")
        private String genreName;

        public String getGenreName() {
            return genreName != null ? genreName : "";
        }
    }

    // Конструктор
    public MovieResponse(int id, String name, String description, String year,
                         String rating, String posterUrl, List<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.year = year;
        this.rating = rating;
        this.posterUrl = posterUrl;
        this.genres = genres;
    }

    // Геттеры
    public int getId() { return id; }
    public String getName() { return name != null ? name : "Нет названия"; }
    public String getDescription() { return description != null ? description : "Нет описания"; }
    public String getYear() { return year != null ? year : "Не указан"; }
    public String getRating() { return rating != null ? rating : "Нет рейтинга"; }
    public String getPosterUrl() { return posterUrl != null ? posterUrl : ""; }
    public List<String> getGenres() {
        List<String> genreNames = new ArrayList<>();
        if (genres != null) {
            for (Genre genre : genres) {
                genreNames.add(genre.getGenreName());
            }
        }
        return genreNames;
    }
}