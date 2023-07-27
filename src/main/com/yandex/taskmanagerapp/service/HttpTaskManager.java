package com.yandex.taskmanagerapp.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yandex.taskmanagerapp.exceptions.ManagerSaveException;
import com.yandex.taskmanagerapp.model.Epic;
import com.yandex.taskmanagerapp.model.Subtask;
import com.yandex.taskmanagerapp.model.Task;
import com.yandex.taskmanagerapp.client.KVTaskClient;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {

    public final KVTaskClient kvTaskClient;
    private final Gson gson = Managers.getGson();

    private static final String TASKS = "tasks";
    private static final String SUBTASKS = "subtasks";
    private static final String EPICS = "epics";
    private static final String HISTORY = "history";
    private static final String MAX_ID = "maxId";

    public HttpTaskManager (URI url, boolean isLoading) {
        kvTaskClient = new KVTaskClient(url);
        if(isLoading) {load();}
    }

    public HttpTaskManager (URI url) {
        this(url, false);
    }

    @Override
    public void save() {
        String jsonOfTasks = gson.toJson(tasks);
        if(!jsonOfTasks.isEmpty()) {
            kvTaskClient.put(TASKS, jsonOfTasks);
        }

        String jsonOfEpics = gson.toJson(epics);
        if(!jsonOfEpics.isEmpty()) {
            kvTaskClient.put(EPICS, jsonOfEpics);
        }

        String jsonOfSubtasks = gson.toJson(subtasks);
        if(!jsonOfSubtasks.isEmpty()) {
            kvTaskClient.put(SUBTASKS, jsonOfSubtasks);
        }

        String jsonOfHistory = gson.toJson(getHistory().stream().map(Task::getId).collect(Collectors.toList()));
        if(!jsonOfHistory.isEmpty()){
            kvTaskClient.put(HISTORY, jsonOfHistory);
        }

        kvTaskClient.put(MAX_ID, String.valueOf(newId));
    }

    private void load() {
        String jsonOfTasks = kvTaskClient.load(TASKS);
        Map<Integer, Task> tasksFromServer =
                gson.fromJson(jsonOfTasks, new TypeToken<Map<Integer, Task>>() {}.getType());
        tasks.putAll(tasksFromServer);
        prioritizedTasks.addAll(tasksFromServer.values());

        String jsonOfEpics = kvTaskClient.load(EPICS);
        Map<Integer, Epic> epicsFromServer =
                gson.fromJson(jsonOfEpics, new TypeToken<Map<Integer, Epic>>() {}.getType());
        epics.putAll(epicsFromServer);

        String jsonOfSubtasks = kvTaskClient.load(SUBTASKS);
        Map<Integer, Subtask> subtasksFromServer =
                gson.fromJson(jsonOfSubtasks, new TypeToken<Map<Integer, Subtask>>() {}.getType());
        subtasks.putAll(subtasksFromServer);
        prioritizedTasks.addAll(subtasksFromServer.values());

        String jsonOfHistory = kvTaskClient.load(HISTORY);
        List<Integer> historyOfId = gson.fromJson(jsonOfHistory, new TypeToken<List<Integer>>() {}.getType());
        for (Integer id : historyOfId) {
            historyManager.addTaskToHistory(tasks.get(id));
            historyManager.addTaskToHistory(subtasks.get(id));
            historyManager.addTaskToHistory(epics.get(id));
        }

        String jsonOfId = kvTaskClient.load(MAX_ID);
        try {
            newId = Integer.parseInt(jsonOfId);
        } catch (NumberFormatException exception) {
            throw new ManagerSaveException("Сервер передал значение, которое не является числом");
        }
    }
}
