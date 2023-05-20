package com.yandex.taskmanagerapp.service;

import com.yandex.taskmanagerapp.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public static List<Task> lastTenTasks = new ArrayList<>();

    @Override
    public void addTaskToHistory (Task task) {
        if(lastTenTasks.size() < 10) {
            lastTenTasks.add(task);
        } else {
            lastTenTasks.remove(0);
            lastTenTasks.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return lastTenTasks;
    }
}
