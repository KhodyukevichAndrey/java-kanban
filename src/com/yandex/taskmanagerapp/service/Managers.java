package com.yandex.taskmanagerapp.service;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() { /* тоже подумал странно возвращать список,
    но смутила немного формулировка ТЗ */
        return new InMemoryHistoryManager();
    }
}
