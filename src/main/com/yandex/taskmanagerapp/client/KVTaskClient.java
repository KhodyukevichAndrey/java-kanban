package com.yandex.taskmanagerapp.client;

import com.yandex.taskmanagerapp.exceptions.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final URI url;
    private final HttpClient httpClient;
    private final String apiTokenOfClient;

    public KVTaskClient(URI url) {
        this.url = url;
        httpClient = HttpClient.newHttpClient();
        apiTokenOfClient = registerToken(url);
    }

    public void put(String key, String json) {
        try {
            URI urlForSave = URI.create(url + "/save/" + key + "?API_TOKEN=" + apiTokenOfClient);
            HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
            HttpRequest request = HttpRequest.newBuilder().uri(urlForSave).POST(body).build();
            HttpResponse response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            if(response.statusCode() != 200) {
                throw new ManagerSaveException("Не удалось сохранить данные на сервере. " +
                        "Ошибка " + response.statusCode());
            }
        } catch (IOException | InterruptedException exception) {
            throw new ManagerSaveException("Не удалось отправить запрос на сервер");
        }
    }

    public String load (String key) {
        try {
            URI urlForLoad = URI.create(url + "/load/" + key + "?API_TOKEN=" + apiTokenOfClient);
            HttpRequest request = HttpRequest.newBuilder().uri(urlForLoad).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() != 200) {
                throw new ManagerSaveException("Не удалось загрузить полученные данные от сервера." +
                        " Ошибка " + response.statusCode());
            }
            return response.body();
        } catch (IOException | InterruptedException exception) {
            throw new ManagerSaveException("Не удалось отправить запрос на сервер");
        }
    }

    private String registerToken(URI url) {
        try {
            URI uriForToken = URI.create(url + "/register");
            HttpRequest request = HttpRequest.newBuilder().uri(uriForToken).GET().build();
            HttpResponse response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() != 200) {
                throw new ManagerSaveException("Не удалось зарегестрировать токен на сервере. " +
                        "Ошибка сервера " + response.statusCode());
            }
            return response.body().toString();
        } catch (IOException | InterruptedException exception) {
            throw new ManagerSaveException("Не удалось отправить запрос на сервер");
        }
    }
}
