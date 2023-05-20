package com.yandex.taskmanagerapp.service;

import com.yandex.taskmanagerapp.model.Task;
import java.util.List;

public interface HistoryManager {

    void addTaskToHistory (Task task);

    List<Task> getHistory();
}
