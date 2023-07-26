package com.yandex.taskmanagerapp.client;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final URI url;
    HttpClient httpClient;

    private String apiTokenOfClient;
    Gson gson;

    public KVTaskClient(URI url) {
        this.url = url;
        httpClient = HttpClient.newHttpClient();
        gson = new Gson();
        try {
            URI uriForToken = URI.create(url + "/register");
            HttpRequest request = HttpRequest.newBuilder().uri(uriForToken).GET().build();
            HttpResponse response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            apiTokenOfClient = response.body().toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void put(String key, String json) {
        try {
            URI urlForSave = URI.create(url + "/save/" + key + "?API_TOKEN=" + apiTokenOfClient);
            HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
            HttpRequest request = HttpRequest.newBuilder().uri(urlForSave).POST(body).build();
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String load (String key) {
        HttpResponse<String> response = null;
        try {
            URI urlForLoad = URI.create(url + "/load/" + key + "?API_TOKEN=" + apiTokenOfClient);
            HttpRequest request = HttpRequest.newBuilder().uri(urlForLoad).GET().build();
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if(response == null) {
            String noValue = "Токен в базе сервера не найден";
            return noValue;
        }
        return response.body();
    }

    public String getApiTokenOfClient() { //для тестов
        return apiTokenOfClient;
    }

    public void setApiTokenOfClient(String apiTokenOfClient) {
        this.apiTokenOfClient = apiTokenOfClient;
    }
}
