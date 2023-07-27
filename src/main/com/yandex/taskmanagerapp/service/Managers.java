package com.yandex.taskmanagerapp.service;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.net.URI;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault(URI url) {
        return new HttpTaskManager(url);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager loadFileBackedTasksManager(File file) {
        return FileBackedTasksManager.loadFromFile(file);
    }

    public static Gson getGson() {
        return new GsonBuilder().create();
    }
}
