package com.yandex.taskmanager;

import com.yandex.taskmanagerapp.client.KVTaskClient;
import com.yandex.taskmanagerapp.model.Epic;
import com.yandex.taskmanagerapp.model.Statuses;
import com.yandex.taskmanagerapp.model.Subtask;
import com.yandex.taskmanagerapp.model.Task;
import com.yandex.taskmanagerapp.server.HttpTaskServer;
import com.yandex.taskmanagerapp.server.KVServer;
import com.yandex.taskmanagerapp.service.HttpTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTest {

    KVServer kvServer;
    HttpTaskServer httpTaskServer;
    HttpTaskManager httpTaskManager;

    @BeforeEach
    void testEnvironment() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            httpTaskManager = new HttpTaskManager(URI.create("http://localhost:8078"));
            httpTaskServer = new HttpTaskServer(httpTaskManager);
            httpTaskServer.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }

        Task task1 = new Task("name1", "description1", Statuses.NEW,
                45, LocalDateTime.of(2023, Month.JANUARY, 7, 7, 0));
        Task task2 = new Task("name2", "description2", Statuses.IN_PROGRESS,
                45, LocalDateTime.of(2023, Month.APRIL, 7, 7, 30));
        Task task3 = new Task("name3", "description3", Statuses.DONE);
        Epic epic1 = new Epic("name1", "description1");
        Epic epic2 = new Epic("name2", "description2");

        assertTrue(httpTaskManager.getAllTask().isEmpty(),
                "Список задач должен быть пустым после инициализации");
        assertTrue(httpTaskManager.getAllSubtask().isEmpty(),
                "Список подзадач должен быть пустым после инициализации");
        assertTrue(httpTaskManager.getAllEpics().isEmpty(),
                "Список Эпиков должен быть пустым после инициализации");

        httpTaskManager.addNewTask(task1);
        httpTaskManager.addNewTask(task2);
        httpTaskManager.addNewTask(task3);
        httpTaskManager.addNewEpic(epic1);
        httpTaskManager.addNewEpic(epic2);
        Subtask subtask1 = new Subtask("Subtask1", "description1", Statuses.NEW,
                45, LocalDateTime.of(2023, Month.JUNE, 7, 7, 30), epic1.getId());
        Subtask subtask2 = new Subtask("Subtask2", "description2", Statuses.NEW,
                120, LocalDateTime.of(2023, Month.OCTOBER, 8, 8, 30), epic1.getId());
        httpTaskManager.addNewSubtask(subtask1);
        httpTaskManager.addNewSubtask(subtask2);
    }

    @AfterEach
    void serverStop() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    void shouldLoadFromServer() throws IOException, InterruptedException {

        HttpClient httpClient = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8078");
        HttpTaskManager newHttpTaskManager = new HttpTaskManager(url);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());


        KVTaskClient KVTaskClientOfCurrentHttpTaskManager = httpTaskManager.getKvTaskClient();
        KVTaskClient KVTaskClientOfNewHttpTaskManager = newHttpTaskManager.getKvTaskClient();

        KVTaskClientOfNewHttpTaskManager.setApiTokenOfClient(KVTaskClientOfCurrentHttpTaskManager.getApiTokenOfClient());
        newHttpTaskManager.load();

        assertEquals(httpTaskManager.getAllTask(), newHttpTaskManager.getAllTask(),
                "После загрузки с сервера списки задач должны быть идентичны");
        assertEquals(httpTaskManager.getHistory(), newHttpTaskManager.getHistory(),
                "После загрузки с сервера истории запросов должны быть идентичны");
        assertEquals(httpTaskManager.getEpicsSubtasks(4), newHttpTaskManager.getEpicsSubtasks(4),
                "После загрузки с сервера списки подзадач эпика должны быть идентичны");
    }
}
