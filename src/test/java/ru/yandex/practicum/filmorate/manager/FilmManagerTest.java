package ru.yandex.practicum.filmorate.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.LocalDateAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmManagerTest {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    HttpClient client = HttpClient.newHttpClient();
    URI hostUrl = URI.create("http://localhost:8080/films");
    ConfigurableApplicationContext app;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    @BeforeEach
    void beforeEach() {
        app = SpringApplication.run(FilmorateApplication.class);
    }

    @AfterEach
    void afterEach() {
        app.close();
    }

    @Test
    @DisplayName("Create film, response status code 200")
    void createFilm() throws IOException, InterruptedException {
        Film film = getStandartFilm();

        HttpResponse<String> response = sendPostRequestReturnResponse(film, hostUrl);

        assertEquals(200, response.statusCode());
    }

    @Test
    @DisplayName("Don't create film with blank name, response status code 500")
    void createFilmWithoutName() throws IOException, InterruptedException {
        Film film = getStandartFilm();
        film.setName("");

        HttpResponse<String> response = sendPostRequestReturnResponse(film, hostUrl);

        assertEquals(500, response.statusCode());
    }

    @Test
    @DisplayName("Don't create film with description lenght more then 200 char, response status code 500")
    void createFilmWithLongDescription() throws IOException, InterruptedException {
        Film film = getStandartFilm();
        film.setDescription("This is very long description more then 200 characters, i just keep wrighting and " +
                "wrighting, it is pretty much already. This is very long description more then 200 characters, " +
                "i just keep wrighting and wrighting, it is pretty much already");

        HttpResponse<String> response = sendPostRequestReturnResponse(film, hostUrl);

        assertEquals(500, response.statusCode());
    }

    @Test
    @DisplayName("Don't create film with releaseDate earlier then first film ever, response status code 500")
    void createFilmWithWrongReleaseDate() throws IOException, InterruptedException {
        Film film = getStandartFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        HttpResponse<String> response = sendPostRequestReturnResponse(film, hostUrl);

        assertEquals(500, response.statusCode());
    }

    @Test
    @DisplayName("Don't create film with negative duration, response status code 500")
    void createFilmWithNegativeDuration() throws IOException, InterruptedException {
        Film film = getStandartFilm();
        film.setDuration(-1);

        HttpResponse<String> response = sendPostRequestReturnResponse(film, hostUrl);

        assertEquals(500, response.statusCode());
    }

    @Test
    @DisplayName("Update film, response status code 200")
    void updateFilm() throws IOException, InterruptedException {
        Film film = getStandartFilm();
        sendPostRequestReturnResponse(film, hostUrl);
        film.setId(1);
        film.setName("New name");
        film.setDescription("New description");
        film.setDuration(10);
        film.setReleaseDate(LocalDate.of(1999, 10, 25));

        HttpResponse<String> response = sendPutRequestReturnResponse(film, hostUrl);

        assertEquals(200, response.statusCode());
    }

    @Test
    @DisplayName("Don't update film with blank name, response status code 500")
    void updateFilmWithoutName() throws IOException, InterruptedException {
        Film film = getStandartFilm();
        sendPostRequestReturnResponse(film, hostUrl);
        film.setId(1);
        film.setName("");
        film.setDescription("New description");
        film.setDuration(10);
        film.setReleaseDate(LocalDate.of(1999, 10, 25));

        HttpResponse<String> response = sendPutRequestReturnResponse(film, hostUrl);

        assertEquals(500, response.statusCode());
    }

    @Test
    @DisplayName("Don't update film with description lenght more then 200 char, response status code 500")
    void updateFilmWithLongDescription() throws IOException, InterruptedException {
        Film film = getStandartFilm();
        sendPostRequestReturnResponse(film, hostUrl);
        film.setId(1);
        film.setName("New name");
        film.setDescription("This is very long description more then 200 characters, i just keep wrighting and " +
                "wrighting, it is pretty much already. This is very long description more then 200 characters, " +
                "i just keep wrighting and wrighting, it is pretty much already");
        film.setDuration(10);
        film.setReleaseDate(LocalDate.of(1999, 10, 25));

        HttpResponse<String> response = sendPutRequestReturnResponse(film, hostUrl);

        assertEquals(500, response.statusCode());
    }

    @Test
    @DisplayName("Don't update film with releaseDate earlier then first film ever, response status code 500")
    void updateFilmWithWrongReleaseDate() throws IOException, InterruptedException {
        Film film = getStandartFilm();
        sendPostRequestReturnResponse(film, hostUrl);
        film.setId(1);
        film.setName("New name");
        film.setDescription("New description");
        film.setDuration(10);
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        HttpResponse<String> response = sendPutRequestReturnResponse(film, hostUrl);

        assertEquals(500, response.statusCode());
    }

    @Test
    @DisplayName("Don't update film with negative duration, response status code 500")
    void updateFilmWithNegativeDuration() throws IOException, InterruptedException {
        Film film = getStandartFilm();
        sendPostRequestReturnResponse(film, hostUrl);
        film.setId(1);
        film.setName("New name");
        film.setDescription("New description");
        film.setDuration(-1);
        film.setReleaseDate(LocalDate.of(1999, 10, 25));

        HttpResponse<String> response = sendPutRequestReturnResponse(film, hostUrl);

        assertEquals(500, response.statusCode());
    }

    public HttpResponse<String> sendPostRequestReturnResponse(Film film, URI url) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher bodyPublisherEpic = HttpRequest.BodyPublishers.ofString(gson.toJson(film), DEFAULT_CHARSET);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(bodyPublisherEpic)
                .setHeader("Content-Type", "application/json")
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> sendPutRequestReturnResponse(Film film, URI url) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher bodyPublisherEpic = HttpRequest.BodyPublishers.ofString(gson.toJson(film), DEFAULT_CHARSET);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(bodyPublisherEpic)
                .setHeader("Content-Type", "application/json")
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public Film getStandartFilm() {
        return Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(2000, 12, 1))
                .duration(120)
                .build();
    }
}