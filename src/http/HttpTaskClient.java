package http;

import exceptions.RequestException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpTaskClient {

    private final String url = "http://localhost:8078";
    private String apiToken;
    private final HttpClient client = HttpClient.newHttpClient();

    public HttpTaskClient() {
        register();
    }

    public void put(String key, String json) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/save" + key + "?API_TOKEN=" + apiToken))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200) {
                throw new RequestException("Ошибка получения запроса. Код ошибки: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RequestException("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public String load(String key) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/load" + key + "?API_TOKEN=" + apiToken))
                .header("Content-Type", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RequestException("Ошибка получения запроса. Код ошибки: " + response.statusCode());
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RequestException("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    private void register() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/register"))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                apiToken = response.body();
            } else {
                throw new RequestException("Ошибка получения запроса. Код ошибки: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RequestException("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }
}