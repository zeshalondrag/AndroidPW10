package com.example.kinopoiskapi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface KinopoiskApi {
    @GET("api/v2.1/films/search-by-keyword")
    Call<SearchResponse> searchMovies(
            @Header("X-API-KEY") String apiKey,
            @Query("keyword") String keyword
    );
}