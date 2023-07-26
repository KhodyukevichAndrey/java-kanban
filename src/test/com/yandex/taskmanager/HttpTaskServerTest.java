package com.yandex.taskmanager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yandex.taskmanagerapp.model.*;
import com.yandex.taskmanagerapp.server.HttpTaskServer;
import com.yandex.taskmanagerapp.server.KVServer;
import com.yandex.taskmanagerapp.service.Managers;
import com.yandex.taskmanagerapp.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HttpTaskServerTest {

    HttpTaskServer httpTaskServer;
    HttpClient client;
    Gson gson;
    private TaskManager taskManager;
    private Task task;
    private Task task1;
    private Epic epic;
    private Epic epic1;
    private Subtask subtask;
    private Subtask subtask1;
    URI uri;
    KVServer kvServer;

    @BeforeEach
    void testEnvironment() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = Managers.getDefault(URI.create("http://localhost:8078"));
        httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();

        gson = new Gson();
        client = HttpClient.newHttpClient();
        task = new Task("name1", "description1", Statuses.NEW,
                45, LocalDateTime.of(2023, Month.JANUARY, 7, 7, 0));
        task1 = new Task("name2", "description2", Statuses.IN_PROGRESS,
                45, LocalDateTime.of(2023, Month.APRIL, 7, 7, 30));
        task1.setId(4);
        epic = new Epic("name1", "description1");
        epic1 = new Epic("name2", "description2");
        epic1.setId(4);
        taskManager.addNewTask(task);
        taskManager.addNewEpic(epic);
        subtask = new Subtask("Subtask1", "description1", Statuses.NEW,
                45, LocalDateTime.of(2023, Month.JUNE, 7, 7, 30), epic.getId());
        subtask1 = new Subtask("Subtask1", "description1", Statuses.NEW,
                45, LocalDateTime.of(2023, Month.OCTOBER, 7, 7, 30), epic.getId());
        subtask1.setId(4);
        taskManager.addNewSubtask(subtask);
        taskManager.getTaskById(task.getId());
    }

    @AfterEach
    void serverStop() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    void shouldReturnTasksListFromServer() throws IOException, InterruptedException {
        uri = URI.create("http://localhost:8080/tasks/task/");

        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasksFromManager = taskManager.getAllTask();
        List<Task> tasksFromServer = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {}.getType());

        assertNotNull(tasksFromServer);
        assertEquals(1, tasksFromServer.size(),
                "Количество элементов в списке должно соответствовать кол-ву созданных задач");
        assertEquals(tasksFromManager, tasksFromServer, "Сервер должен возвращать список созданных задач");
    }

    @Test
    void shouldReturnTaskFromServer() throws IOException, InterruptedException {
        uri = URI.create("http://localhost:8080/tasks/task/" + task.getId());

        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Task taskFromManager = taskManager.getTaskById(task.getId());
        Task taskFromServer = gson.fromJson(response.body(), Task.class);

        assertNotNull(taskFromServer);
        assertEquals(taskFromManager, taskFromServer, "Сервер должен возвращать созданную задачу");
    }

    @Test
    void shouldReturnSubtasksListFromServer() throws IOException, InterruptedException {
        uri = URI.create("http://localhost:8080/tasks/subtask/");

        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Subtask> subtasksFromManager = taskManager.getAllSubtask();
        List<Subtask> subtasksFromServer = gson.fromJson(response.body(),
                new TypeToken<ArrayList<Subtask>>() {}.getType());

        assertNotNull(subtasksFromServer);
        assertEquals(1, subtasksFromServer.size(),
                "Количество элементов в списке должно соответствовать кол-ву созданных задач");
        assertEquals(subtasksFromManager, subtasksFromServer, "Сервер должен возвращать список созданных подзадач");
    }

    @Test
    void shouldReturnSubtaskFromServer() throws IOException, InterruptedException {
        uri = URI.create("http://localhost:8080/tasks/subtask/" + subtask.getId());

        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtaskFromManager = taskManager.getSubtaskById(subtask.getId());
        Subtask subtaskFromServer = gson.fromJson(response.body(), Subtask.class);

        assertNotNull(subtaskFromServer);
        assertEquals(subtaskFromManager, subtaskFromServer, "Сервер должен возвращать созданную задачу");
    }

    @Test
    void shouldReturnEpicsListFromServer() throws IOException, InterruptedException {
        uri = URI.create("http://localhost:8080/tasks/epic/");

        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Epic> epicsFromManager = taskManager.getAllEpics();
        List<Epic> epicsFromServer = gson.fromJson(response.body(),
                new TypeToken<ArrayList<Epic>>() {}.getType());

        assertNotNull(epicsFromServer);
        assertEquals(1, epicsFromServer.size(),
                "Количество элементов в списке должно соответствовать кол-ву созданных задач");
        assertEquals(epicsFromManager, epicsFromServer, "Сервер должен возвращать список созданных эпиков");
    }

    @Test
    void shouldReturnEpicFromServer() throws IOException, InterruptedException {
        uri = URI.create("http://localhost:8080/tasks/epic/" + epic.getId());

        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic epicFromManager = taskManager.getEpicById(epic.getId());
        Epic epicFromServer = gson.fromJson(response.body(), Epic.class);

        assertNotNull(epicFromServer);
        assertEquals(epicFromManager, epicFromServer, "Сервер должен возвращать созданную задачу");
    }

    @Test
    void shouldReturnEpicsSubtasksFromServer() throws IOException, InterruptedException {
        uri = URI.create("http://localhost:8080/tasks/subtask/epic/" + epic.getId());

        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Subtask> epicsSubtasksFromManager = taskManager.getEpicsSubtasks(epic.getId());
        List<Subtask> epicsSubtasksFromServer = gson.fromJson(response.body(),
                new TypeToken<ArrayList<Subtask>>() {}.getType());

        assertNotNull(epicsSubtasksFromServer);
        assertEquals(epicsSubtasksFromManager, epicsSubtasksFromServer,
                "Сервер должен возвращать список созданных подзадач эпика");
    }

    @Test
    void shouldReturnHistoryFromServer() throws IOException, InterruptedException {
        uri = URI.create("http://localhost:8080/tasks/history/");

        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> historyFromManager = taskManager.getHistory();
        List<Task> historyFromServer = gson.fromJson(response.body(),
                new TypeToken<ArrayList<Task>>() {}.getType());

        assertNotNull(historyFromServer);
        assertEquals(1, historyFromServer.size(),
                "Количество элементов в списке должно соответствовать кол-ву вызванных задач");
        assertEquals(historyFromManager, historyFromServer,
                "Сервер должен возвращать историю менеджера");
    }

    @Test
    void shouldReturnPrioritizedTasksFromServer() throws IOException, InterruptedException {
        uri = URI.create("http://localhost:8080/tasks/");

        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Set<Task> priorityTasksFromManager = taskManager.getPriorityTasks();
        Set<Task> priorityTasksFromServer = gson.fromJson(response.body(),
                new TypeToken<Set<Task>>() {}.getType());

        assertNotNull(priorityTasksFromServer);
        assertEquals(2, priorityTasksFromServer.size(),
                "Количество элементов в списке должно соответствовать " +
                        "кол-ву созданных задач (задачи + подзадачи)");
        assertEquals(priorityTasksFromManager, priorityTasksFromServer);
    }

    @Test
    void shouldCreateTaskByServer() throws IOException, InterruptedException {
        uri = URI.create("http://localhost:8080/tasks/task/");

        String json = gson.toJson(task1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        Task currentTask = taskManager.getTaskById(task1.getId());

        assertNotNull(currentTask);
        assertEquals(task1, currentTask, "Задачи должны быть эквивалентны после добавления.");
        assertEquals(2, taskManager.getAllTask().size(),
                "После добавления задачи размер списка должен быть увеличен на 1");
    }

    @Test
    void shouldUpdateTaskByServer() throws IOException, InterruptedException {
        uri = URI.create("http://localhost:8080/tasks/task/" + task.getId());

        task.setName("NameAfterUpdate");
        String json = gson.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        Task currentTask = taskManager.getTaskById(task.getId());

        assertNotNull(currentTask);
        assertEquals(task, currentTask, "Задачи должны быть эквивалентны после обновления");
        assertEquals(1, taskManager.getAllTask().size(),
                "После обновления задачи размер списка не должен изменяться");
    }

    @Test
    void shouldCreateSubtaskByServer() throws IOException, InterruptedException {
        uri = URI.create("http://localhost:8080/tasks/subtask/");

        String json = gson.toJson(subtask1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask currentSubtask = taskManager.getSubtaskById(subtask1.getId());

        assertNotNull(currentSubtask);
        assertEquals(subtask1, currentSubtask, "Подзадачи должны быть эквивалентны после добавления.");
        assertEquals(2, taskManager.getAllSubtask().size(),
                "После добавления подзадачи размер списка должен быть увеличен на 1");
    }

    @Test
    void shouldUpdateSubtaskByServer() throws IOException, InterruptedException {
        uri = URI.create("http://localhost:8080/tasks/subtask/" + subtask.getId());

        subtask.setName("NameAfterUpdate");
        String json = gson.toJson(subtask);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask currentSubtask = taskManager.getSubtaskById(subtask.getId());

        assertNotNull(currentSubtask);
        assertEquals(subtask, currentSubtask, "Подзадачи должны быть эквивалентны после обновления");
        assertEquals(1, taskManager.getAllSubtask().size(),
                "После обновления подзадачи размер списка не должен изменяться");
    }

    @Test
    void shouldCreateEpicByServer() throws IOException, InterruptedException {
        uri = URI.create("http://localhost:8080/tasks/epic/");

        Gson gsonWithoutAdapter = new Gson(); /*При использовании стандартного gson выдает ошибку,
        т.к. там определен LocalDateTimeAdapter, а у эпика такого поля нет,
        поскольку рассчитывается он из подзадач. Тест проходит, но с предупреждением от библиотеки*/
        String json = gsonWithoutAdapter.toJson(epic1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic currentEpic = taskManager.getEpicById(epic1.getId());

        assertNotNull(currentEpic);
        assertEquals(epic1, currentEpic, "Эпики должны быть эквивалентны после добавления.");
        assertEquals(2, taskManager.getAllEpics().size(),
                "После добавления эпика размер списка должен быть увеличен на 1");
    }

    @Test
    void shouldUpdateEpicByServer() throws IOException, InterruptedException {
        uri = URI.create("http://localhost:8080/tasks/epic/" + epic.getId());

        epic.setName("NameAfterUpdate");
        String json = gson.toJson(epic);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        Epic currentEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(currentEpic);
        assertEquals(epic, currentEpic, "Задачи должны быть эквивалентны после обновления");
        assertEquals(1, taskManager.getAllSubtask().size(),
                "После обновления эпика размер списка не должен изменяться");
    }

    @Test
    void shouldDeleteAllTasksByServer() throws IOException, InterruptedException {
        uri = URI.create("http://localhost:8080/tasks/task/");

        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        assertTrue(taskManager.getAllTask().isEmpty(), "После удаления задач, список должен быть пустой");
    }

    @Test
    void shouldDeleteTaskWithIdByServer() throws IOException, InterruptedException {
        uri = URI.create("http://localhost:8080/tasks/task/" + task.getId());

        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        assertNull(taskManager.getTaskById(task.getId()),
                "После удаления задачи, менеджер не должен ее возвращать");
        assertEquals(0, taskManager.getAllTask().size(),
                "После удаления задачи, размер списка должен уменьшаться на 1");
    }

    @Test
    void shouldDeleteAllSubtasksByServer() throws IOException, InterruptedException {
        uri = URI.create("http://localhost:8080/tasks/subtask/");

        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        assertTrue(taskManager.getAllSubtask().isEmpty(), "После удаления подзадач, список должен быть пустой");
    }

    @Test
    void shouldDeleteSubtaskWithIdByServer() throws IOException, InterruptedException {
        uri = URI.create("http://localhost:8080/tasks/subtask/" + subtask.getId());

        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        assertNull(taskManager.getSubtaskById(subtask.getId()),
                "После удаления подзадачи, менеджер не должен ее возвращать");
        assertEquals(0, taskManager.getAllSubtask().size(),
                "После удаления подзадачи, размер списка должен уменьшаться на 1");
        assertEquals(0, taskManager.getEpicsSubtasks(epic.getId()).size(),
                "После удаления подзадачи, размер списка подзадач эпика должен уменьшаться на 1");
    }

    @Test
    void shouldDeleteAllEpicsByServer() throws IOException, InterruptedException {
        uri = URI.create("http://localhost:8080/tasks/epic/");

        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        assertTrue(taskManager.getAllEpics().isEmpty(), "После удаления эпиков, список должен быть пустой");
        assertTrue(taskManager.getAllSubtask().isEmpty(),
                "После удаления эпиков, список подзадач так же должен быть пустой");
    }

    @Test
    void shouldDeleteEpicWithIdByServer() throws IOException, InterruptedException {
        uri = URI.create("http://localhost:8080/tasks/epic/" + epic.getId());

        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        assertNull(taskManager.getEpicById(epic.getId()),
                "После удаления эпика, менеджер не должен ее возвращать");
        assertEquals(0, taskManager.getAllEpics().size(),
                "После удаления эпика, размер списка должен уменьшаться на 1");
    }
}
