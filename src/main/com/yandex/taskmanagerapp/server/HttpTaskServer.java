package com.yandex.taskmanagerapp.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.yandex.taskmanagerapp.model.Epic;
import com.yandex.taskmanagerapp.model.Subtask;
import com.yandex.taskmanagerapp.model.Task;
import com.yandex.taskmanagerapp.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    HttpServer server;
    private static final int PORT = 8080;

    private final TaskManager httpTaskManager;
    private final Gson gson = new Gson();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpTaskManager = taskManager;
        server.createContext("/tasks", this::handleTasks);
    }

    private void handleTasks(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String requestMethod = httpExchange.getRequestMethod();

            switch (requestMethod) {
                case "GET":
                    if (Pattern.matches("^/tasks/task/$", path)) {
                        String response = gson.toJson(httpTaskManager.getAllTask());
                        if(response.isEmpty()) {
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                        sendText(httpExchange, response);
                        break;
                    }

                    if (Pattern.matches("^/tasks/task/\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/task/", "");
                        int id = parsePathId(pathId);
                        Task task = httpTaskManager.getTaskById(id);
                        if (id != -1 && task != null) {
                            String response = gson.toJson(task);
                            sendText(httpExchange, response);
                        } else {
                            System.out.println("Задача с указанным ID не найдена " +
                                    "или введен некорректный {id=" + pathId + "}");
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                        break;
                    }

                    if (Pattern.matches("^/tasks/subtask/$", path)) {
                        String response = gson.toJson(httpTaskManager.getAllSubtask());
                        if(response.isEmpty()) {
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                        sendText(httpExchange, response);
                        break;
                    }

                    if (Pattern.matches("^/tasks/subtask/\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/subtask/", "");
                        int id = parsePathId(pathId);
                        Subtask subtask = httpTaskManager.getSubtaskById(id);
                        if (id != -1 && subtask != null) {
                            String response = gson.toJson(subtask);
                            sendText(httpExchange, response);
                        } else {
                            System.out.println("Подзадача с указанным ID не найдена " +
                                    "или введен некорректный {id=" + pathId + "}");
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                        break;
                    }

                    if (Pattern.matches("^/tasks/epic/$", path)) {
                        String response = gson.toJson(httpTaskManager.getAllEpics());
                        if(response.isEmpty()) {
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                        sendText(httpExchange, response);
                        break;
                    }

                    if (Pattern.matches("^/tasks/epic/\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/epic/", "");
                        int id = parsePathId(pathId);
                        Epic epic = httpTaskManager.getEpicById(id);
                        if (id != -1 && epic != null) {
                            String response = gson.toJson(epic);
                            sendText(httpExchange, response);
                        } else {
                            System.out.println("Эпик с указанным ID не найден " +
                                    "или введен некорректный {id=" + pathId + "}");
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                        break;
                    }

                    if(Pattern.matches("^/tasks/subtask/epic/\\d+$", path)) {
                        String pathId = path.replaceFirst("/tasks/subtask/epic/", "");
                        int id = parsePathId(pathId);
                        Epic epic = httpTaskManager.getEpicById(id);
                        if (id != -1 && epic != null) {
                            String response = gson.toJson(httpTaskManager.getEpicsSubtasks(id));
                            sendText(httpExchange, response);
                        } else {
                            System.out.println("Эпик с указанным ID не найден " +
                                    "или введен некорректный {id=" + pathId + "}");
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                        break;
                    }

                    if(Pattern.matches("^/tasks/history/", path)) {
                        String response = gson.toJson(httpTaskManager.getHistory());
                        if(response.isEmpty()) {
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                        sendText(httpExchange, response);
                        break;
                    }

                    if(Pattern.matches("^/tasks/$", path)) {
                        String response = gson.toJson(httpTaskManager.getPriorityTasks());
                        if(response.isEmpty()) {
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                        sendText(httpExchange, response);
                        break;
                    }
                    break;

                case "POST":
                    InputStream inputStream = httpExchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

                    if (Pattern.matches("/tasks/task/$", path)) {
                        Task currentTask = gson.fromJson(body, Task.class);
                        httpTaskManager.addNewTask(currentTask);
                        String response = "Задача успешно добавлена";

                        sendText(httpExchange, response);
                        break;
                    }
                    if (Pattern.matches("/tasks/task/\\d+$", path)) {
                        Task currentTask = gson.fromJson(body, Task.class);
                        httpTaskManager.updateTask(currentTask);
                        String response = "Задача успешно обновлена";
                        sendText(httpExchange, response);
                        break;
                    }
                    if (Pattern.matches("/tasks/subtask/$", path)) {
                        Subtask currentSubtask = gson.fromJson(body, Subtask.class);
                        httpTaskManager.addNewSubtask(currentSubtask);
                        String response = "Подзадача успешно добавлена";
                        sendText(httpExchange, response);
                        break;
                    }
                    if (Pattern.matches("/tasks/subtask/\\d+$", path)) {
                        Subtask currentSubtask = gson.fromJson(body, Subtask.class);
                        httpTaskManager.updateSubtask(currentSubtask);
                        String response = "Подзадача успешно обновлена";
                        sendText(httpExchange, response);
                        break;
                    }
                    if (Pattern.matches("/tasks/epic/$", path)) {
                        Epic currentEpic = gson.fromJson(body, Epic.class);
                        httpTaskManager.addNewEpic(currentEpic);
                        String response = "Эпик успешно добавлен";
                        sendText(httpExchange, response);
                        break;
                    }
                    if (Pattern.matches("/tasks/epic/\\d+$", path)) {
                        Epic currentEpic = gson.fromJson(body, Epic.class);
                        httpTaskManager.updateEpic(currentEpic);
                        String response = "эпик успешно обновлен";
                        sendText(httpExchange, response);
                        break;
                    }

                    break;

                case "DELETE":
                    if (Pattern.matches("^/tasks/task/$", path)) {
                        httpTaskManager.deleteAllTask();
                        System.out.println("Задачи успешно удалены");
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    }

                    if (Pattern.matches("^/tasks/task/\\d+?$", path)) {
                        String pathId = path.replaceFirst("/tasks/task/", "");
                        int id = parsePathId(pathId);
                        Task task = httpTaskManager.getTaskById(id);
                        if (id != -1 && task != null) {
                            httpTaskManager.deleteTaskById(id);
                            httpExchange.sendResponseHeaders(200, 0);
                        } else {
                            System.out.println("Задача с указанным ID не найдена " +
                                    "или введен некорректный {id=" + pathId + "}");
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                        break;
                    }

                    if (Pattern.matches("^/tasks/subtask/$", path)) {
                        httpTaskManager.deleteAllSubtask();
                        System.out.println("Подзадачи успешно удалены");
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    }

                    if (Pattern.matches("^/tasks/subtask/\\d+?$", path)) {
                        String pathId = path.replaceFirst("/tasks/subtask/", "");
                        int id = parsePathId(pathId);
                        Subtask subtask = httpTaskManager.getSubtaskById(id);
                        if (id != -1 && subtask != null) {
                            httpTaskManager.deleteSubtaskById(id);
                            httpExchange.sendResponseHeaders(200, 0);
                        } else {
                            System.out.println("Подзадача с указанным ID не найдена " +
                                    "или введен некорректный {id=" + pathId + "}");
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                        break;
                    }

                    if (Pattern.matches("^/tasks/epic/$", path)) {
                        httpTaskManager.deleteAllEpics();
                        System.out.println("Эпики успешно удалены");
                        httpExchange.sendResponseHeaders(200, 0);
                        break;
                    }

                    if (Pattern.matches("^/tasks/epic/\\d+?$", path)) {
                        String pathId = path.replaceFirst("/tasks/epic/", "");
                        int id = parsePathId(pathId);
                        Epic epic = httpTaskManager.getEpicById(id);
                        if (id != -1 && epic != null) {
                            httpTaskManager.deleteEpicById(id);
                            httpExchange.sendResponseHeaders(200, 0);
                        } else {
                            System.out.println("Эпик с указанным ID не найден " +
                                    "или введен некорректный {id=" + pathId + "}");
                            httpExchange.sendResponseHeaders(405, 0);
                        }
                        break;
                    }
                    break;
                default: {
                    System.out.println("Серевер обрабатывает GET, POST, DELETE запросы. Был получен запрос "
                            + requestMethod);
                    httpExchange.sendResponseHeaders(405, 0);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    private void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    private int parsePathId(String pathId) {
        try {
            return Integer.parseInt(pathId);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }
}
