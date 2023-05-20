package com.yandex.taskmanagerapp.service;
import com.yandex.taskmanagerapp.model.Task;
import java.util.List;

public class Managers { /* вот эту часть с созданием утилитарного класса не совсем понял,
    но вроде получилось, если конечно это то, что требовалось */

    public TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static List<Task> getDefaultHistory() {
        return InMemoryHistoryManager.lastTenTasks;
    }
}
