package com.example.kinopoiskapi;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse {
    @SerializedName("films")
    private List<MovieResponse> films;

    public List<MovieResponse> getFilms() {
        return films != null ? films : new ArrayList<>();
    }
}