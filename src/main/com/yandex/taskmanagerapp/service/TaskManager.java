package com.yandex.taskmanagerapp.service;

import com.yandex.taskmanagerapp.model.Epic;
import com.yandex.taskmanagerapp.model.Subtask;
import com.yandex.taskmanagerapp.model.Task;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    List<Task> getAllTask();

    void deleteAllTask();

    Task getTaskById(int taskId);

    void addNewTask(Task task);

    void updateTask(Task task);

    void deleteTaskById(int taskId);

    List<Subtask> getAllSubtask();

    void deleteAllSubtask();

    Subtask getSubtaskById(int subtaskId);

    void addNewSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(int subtaskId);

    List<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicById(int epicId);

    void addNewEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpicById(int epicId);

    List<Subtask> getEpicsSubtasks(int epicId);

    List<Task> getHistory();

    TreeSet<Task> getPriorityTasks();
}
