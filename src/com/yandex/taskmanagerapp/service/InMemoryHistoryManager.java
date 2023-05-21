package com.yandex.taskmanagerapp.service;

import com.yandex.taskmanagerapp.model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> lastTenTasks = new LinkedList<>();
    private final static int MAX_SIZE = 10;

    @Override
    public void addTaskToHistory (Task task) {
        if(task == null) {
            return;
        }
        if(lastTenTasks.size() == MAX_SIZE) {
            lastTenTasks.removeFirst();
        }
        lastTenTasks.addLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(lastTenTasks);
    }
}
