package com.example.kinopoiskapi;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String API_TOKEN = "1af69720-4b53-4776-82f9-3b16946a0b2e";
    private static final String API_SEARCH_URL = "https://kinopoiskapiunofficial.tech/api/v2.1/films/search-by-keyword?keyword=";
    private TextView resultTextView;
    private EditText searchEditText;
    private ImageView posterImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);
        searchEditText = findViewById(R.id.searchEditText);
        posterImageView = findViewById(R.id.posterImageView);
        Button searchButton = findViewById(R.id.searchButton);

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
        new Thread(() -> {
            try {
                String encodedQuery = URLEncoder.encode(query, "UTF-8");
                URL url = new URL(API_SEARCH_URL + encodedQuery);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("X-API-KEY", API_TOKEN);
                connection.setRequestProperty("Accept", "application/json");

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    List<MovieResponse> movies = parseSearchResponse(response.toString());
                    runOnUiThread(() -> displaySearchResults(movies));
                } else {
                    handleError(responseCode);
                }

                connection.disconnect();
            } catch (Exception e) {
                runOnUiThread(() -> showError("Ошибка соединения: " + e.getMessage()));
            }
        }).start();
    }

    private List<MovieResponse> parseSearchResponse(String jsonResponse) throws Exception {
        List<MovieResponse> movies = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray filmsArray = jsonObject.getJSONArray("films");

        for (int i = 0; i < filmsArray.length(); i++) {
            JSONObject film = filmsArray.getJSONObject(i);

            int id = film.optInt("filmId", 0);
            String name = film.optString("nameRu", "Нет названия");
            String description = film.optString("description", "Нет описания");
            String year = film.optString("year", "Не указан");
            String rating = film.optString("rating", "Нет рейтинга");
            String posterUrl = film.optString("posterUrl", "");

            List<String> genres = new ArrayList<>();
            JSONArray genresArray = film.optJSONArray("genres");
            if (genresArray != null) {
                for (int j = 0; j < genresArray.length(); j++) {
                    genres.add(genresArray.getJSONObject(j).optString("genre", ""));
                }
            }

            movies.add(new MovieResponse(id, name, description, year, rating, posterUrl, genres));
        }
        return movies;
    }

    private void displaySearchResults(List<MovieResponse> movies) {
        if (movies.isEmpty()) {
            resultTextView.setText("Фильмы не найдены");
            posterImageView.setImageDrawable(null);
            return;
        }

        MovieResponse movie = movies.get(0);

        String movieInfo = "Название: " + movie.getName() + "\n" +
                "Год: " + movie.getYear() + "\n" +
                "Рейтинг: " + movie.getRating() + "\n" +
                "Жанры: " + String.join(", ", movie.getGenres()) + "\n" +
                "Описание: " + movie.getDescription();

        resultTextView.setText(movieInfo);

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
        runOnUiThread(() -> showError(errorMessage));
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        resultTextView.setText(message);
        posterImageView.setImageDrawable(null);
    }
}