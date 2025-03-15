package com.example.kinopoiskapi;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String API_TOKEN = "1af69720-4b53-4776-82f9-3b16946a0b2e";
    private static final String BASE_URL = "https://kinopoiskapiunofficial.tech/";
    private TextView resultTextView;
    private EditText searchEditText;
    private ImageView posterImageView;
    private KinopoiskApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);
        searchEditText = findViewById(R.id.searchEditText);
        posterImageView = findViewById(R.id.posterImageView);
        Button searchButton = findViewById(R.id.searchButton);

        // Инициализация Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(KinopoiskApi.class);

        searchButton.setOnClickListener(v -> {
            String query = searchEditText.getText().toString().trim();
            if (!query.isEmpty()) {
                searchMovies(query);
            } else {
                Toast.makeText(this, "Введите название фильма", Toast.LENGTH_SHORT).show();
            }
        });

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchMovies(query);
                }
                return true;
            }
            return false;
        });
    }

    private void searchMovies(String query) {
        Call<SearchResponse> call = api.searchMovies(API_TOKEN, query);
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MovieResponse> movies = response.body().getFilms();
                    displaySearchResults(movies);
                } else {
                    handleError(response.code());
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                showError("Ошибка соединения: " + t.getMessage());
            }
        });
    }

    private void displaySearchResults(List<MovieResponse> movies) {
        if (movies.isEmpty()) {
            resultTextView.setText("Фильмы не найдены");
            posterImageView.setImageDrawable(null);
            return;
        }

        // Берем первый фильм из результатов
        MovieResponse movie = movies.get(0);

        String movieInfo = "Название: " + movie.getName() + "\n" +
                "Год: " + movie.getYear() + "\n" +
                "Рейтинг: " + movie.getRating() + "\n" +
                "Жанры: " + String.join(", ", movie.getGenres()) + "\n" +
                "Описание: " + movie.getDescription();

        resultTextView.setText(movieInfo);

        // Загружаем постер
        if (!movie.getPosterUrl().isEmpty()) {
            Glide.with(this)
                    .load(movie.getPosterUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(posterImageView);
        } else {
            posterImageView.setImageDrawable(null);
        }
    }

    private void handleError(int responseCode) {
        String errorMessage;
        switch (responseCode) {
            case 404:
                errorMessage = "Фильмы не найдены (404)";
                break;
            case 500:
                errorMessage = "Ошибка сервера (500)";
                break;
            case 401:
                errorMessage = "Ошибка авторизации (401)";
                break;
            case 403:
                errorMessage = "Доступ запрещен (403)";
                break;
            default:
                errorMessage = "Неизвестная ошибка (код: " + responseCode + ")";
        }
        showError(errorMessage);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        resultTextView.setText(message);
        posterImageView.setImageDrawable(null);
    }
}