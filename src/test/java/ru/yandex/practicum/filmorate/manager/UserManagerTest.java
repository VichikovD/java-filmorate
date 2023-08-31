package ru.yandex.practicum.filmorate.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.User;
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

class UserManagerTest {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    HttpClient client = HttpClient.newHttpClient();
    URI hostUrl = URI.create("http://localhost:8080/users");
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
    @DisplayName("Create user, response status code 200")
    void createUser() throws IOException, InterruptedException {
        User user = getStandardUser();

        HttpResponse<String> response = sendPostRequestReturnResponse(user, hostUrl);

        assertEquals(200, response.statusCode());
    }

    @Test
    @DisplayName("Create user with blank name, response status code 200")
    void createUserWithoutName() throws IOException, InterruptedException {
        User user = getStandardUser();
        user.setName("");

        HttpResponse<String> response = sendPostRequestReturnResponse(user, hostUrl);

        assertEquals(200, response.statusCode());
    }

    @Test
    @DisplayName("Don't create user with incorrect email, response status code 500")
    void createUserWithIncorrectEmail() throws IOException, InterruptedException {
        User user = getStandardUser();
        user.setEmail("email without at symbol");

        HttpResponse<String> response = sendPostRequestReturnResponse(user, hostUrl);

        assertEquals(500, response.statusCode());
    }

    @Test
    @DisplayName("Don't create user with empty login, response status code 500")
    void createUserWithEmptyLogin() throws IOException, InterruptedException {
        User user = getStandardUser();
        user.setLogin("");

        HttpResponse<String> response = sendPostRequestReturnResponse(user, hostUrl);

        assertEquals(500, response.statusCode());
    }

    @Test
    @DisplayName("Don't create user with future birthday, response status code 500")
    void createUserWithFutureBirthday() throws IOException, InterruptedException {
        User user = getStandardUser();
        user.setBirthday(LocalDate.of(2023, 9, 10));

        HttpResponse<String> response = sendPostRequestReturnResponse(user, hostUrl);

        assertEquals(500, response.statusCode());
    }

    @Test
    @DisplayName("Update user, response status code 200")
    void updateUser() throws IOException, InterruptedException {
        User user = getStandardUser();
        sendPostRequestReturnResponse(user, hostUrl);
        user.setId(1);
        user.setName("new name");
        user.setLogin("NewLogin");
        user.setEmail("new@email");
        user.setBirthday(LocalDate.of(1999, 10, 25));

        HttpResponse<String> response = sendPutRequestReturnResponse(user, hostUrl);

        assertEquals(200, response.statusCode());
    }

    @Test
    @DisplayName("Update user even with blank name, response status code 200")
    void updateUserWithBlankName() throws IOException, InterruptedException {
        User user = getStandardUser();
        sendPostRequestReturnResponse(user, hostUrl);
        user.setId(1);
        user.setName("");
        user.setLogin("NewLogin");
        user.setEmail("new@email");
        user.setBirthday(LocalDate.of(1999, 10, 25));

        HttpResponse<String> response = sendPutRequestReturnResponse(user, hostUrl);

        assertEquals(200, response.statusCode());
    }

    @Test
    @DisplayName("Don't update user with blank login, response status code 500")
    void updateUserWithoutLogin() throws IOException, InterruptedException {
        User user = getStandardUser();
        sendPostRequestReturnResponse(user, hostUrl);
        user.setId(1);
        user.setName("New name");
        user.setLogin("");
        user.setEmail("new@email");
        user.setBirthday(LocalDate.of(1999, 10, 25));

        HttpResponse<String> response = sendPutRequestReturnResponse(user, hostUrl);

        assertEquals(500, response.statusCode());
    }

    @Test
    @DisplayName("Don't update user with wrong email, response status code 500")
    void updateUserWithIncorrectEmail() throws IOException, InterruptedException {
        User user = getStandardUser();
        sendPostRequestReturnResponse(user, hostUrl);
        user.setId(1);
        user.setName("New name");
        user.setLogin("NewLogin");
        user.setEmail("newEmail");
        user.setBirthday(LocalDate.of(1999, 10, 25));

        HttpResponse<String> response = sendPutRequestReturnResponse(user, hostUrl);

        assertEquals(500, response.statusCode());
    }

    @Test
    @DisplayName("Don't update user with birthday in future, response status code 500")
    void updateUserWithIncorrectBirthday() throws IOException, InterruptedException {
        User user = getStandardUser();
        sendPostRequestReturnResponse(user, hostUrl);
        user.setId(1);
        user.setName("New name");
        user.setLogin("NewLogin");
        user.setEmail("new@email");
        user.setBirthday(LocalDate.of(2023, 9, 25));

        HttpResponse<String> response = sendPutRequestReturnResponse(user, hostUrl);

        assertEquals(500, response.statusCode());
    }

    public HttpResponse<String> sendPostRequestReturnResponse(User user, URI url) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher bodyPublisherEpic = HttpRequest.BodyPublishers.ofString(gson.toJson(user), DEFAULT_CHARSET);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(bodyPublisherEpic)
                .setHeader("Content-Type", "application/json")
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> sendPutRequestReturnResponse(User user, URI url) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher bodyPublisherEpic = HttpRequest.BodyPublishers.ofString(gson.toJson(user), DEFAULT_CHARSET);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(bodyPublisherEpic)
                .setHeader("Content-Type", "application/json")
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public User getStandardUser() {
        return User.builder()
                .name("Name")
                .birthday(LocalDate.of(2000, 12, 1))
                .email("some@email")
                .login("login")
                .build();
    }
}