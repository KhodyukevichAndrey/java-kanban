package com.yandex.taskmanagerapp.service;

import java.io.File;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager loadFileBackedTasksManager(File file) {
        return FileBackedTasksManager.loadFromFile(file);
    }
}
