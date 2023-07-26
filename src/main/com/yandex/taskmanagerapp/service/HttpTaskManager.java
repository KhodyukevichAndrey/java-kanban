package com.yandex.taskmanagerapp.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yandex.taskmanagerapp.model.Epic;
import com.yandex.taskmanagerapp.model.Subtask;
import com.yandex.taskmanagerapp.model.Task;
import com.yandex.taskmanagerapp.client.KVTaskClient;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpTaskManager extends FileBackedTasksManager {

    KVTaskClient kvTaskClient;
    Gson gson;
    public HttpTaskManager(URI url) {
        kvTaskClient = new KVTaskClient(url);
        gson = new Gson();
    }

    @Override
    public void save() {
        String jsonOfTasks = gson.toJson(tasks);
        if(!jsonOfTasks.isEmpty()) {
            kvTaskClient.put("tasks", jsonOfTasks);
        }

        String jsonOfEpics = gson.toJson(epics);
        if(!jsonOfEpics.isEmpty()) {
            kvTaskClient.put("epics", jsonOfEpics);
        }

        String jsonOfSubtasks = gson.toJson(subtasks);
        if(!jsonOfSubtasks.isEmpty()) {
            kvTaskClient.put("subtasks", jsonOfSubtasks);
        }

        String jsonOfHistory = gson.toJson(getHistory());
        if(!jsonOfHistory.isEmpty()){
            kvTaskClient.put("history", jsonOfHistory);
        }

        String jsonOfPriorityTasks = gson.toJson(getPriorityTasks());
        if(!jsonOfPriorityTasks.isEmpty()) {
            kvTaskClient.put("priorityTasks", jsonOfPriorityTasks);
        }

    }

    public void load() {
        String jsonOfTasks = kvTaskClient.load("tasks");
        Map<Integer, Task> tasksFromServer =
                gson.fromJson(jsonOfTasks, new TypeToken<Map<Integer, Task>>() {}.getType());
        tasks.putAll(tasksFromServer);

        String jsonOfEpics = kvTaskClient.load("epics");
        Map<Integer, Epic> epicsFromServer =
                gson.fromJson(jsonOfEpics, new TypeToken<Map<Integer, Epic>>() {}.getType());
        epics.putAll(epicsFromServer);

        String jsonOfSubtasks = kvTaskClient.load("subtasks");
        Map<Integer, Subtask> subtasksFromServer =
                gson.fromJson(jsonOfSubtasks, new TypeToken<Map<Integer, Subtask>>() {}.getType());
        subtasks.putAll(subtasksFromServer);

        String jsonOfHistory = kvTaskClient.load("history");
        List<Task> historyFromServer = gson.fromJson(jsonOfHistory,
                new TypeToken<ArrayList<Task>>() {}.getType());
        getHistory().addAll(historyFromServer);

        String jsonOfPriorityTasks = kvTaskClient.load("priorityTasks");
        ArrayList<Task> priorityTasks =
                gson.fromJson(jsonOfPriorityTasks, new TypeToken<ArrayList<Task>>() {}.getType());
        prioritizedTasks.addAll(priorityTasks);
    }

    public KVTaskClient getKvTaskClient() {
        return kvTaskClient;
    }
}
