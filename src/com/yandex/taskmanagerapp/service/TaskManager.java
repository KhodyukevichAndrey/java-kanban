package com.yandex.taskmanagerapp.service;

import com.yandex.taskmanagerapp.model.Epic;
import com.yandex.taskmanagerapp.model.Subtask;
import com.yandex.taskmanagerapp.model.Task;

import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Task> getAllTask();

    void deleteAllTask();

    Task getTaskById(int taskId);

    void addNewTask(Task task);

    void updateTask(Task task);

    void deleteTaskById(int taskId);

    ArrayList<Subtask> getAllSubtask();

    void deleteAllSubtask();

    Subtask getSubtaskById(int subtaskId);

    void addNewSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(int subtaskId);

    ArrayList<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicById(int epicId);

    void addNewEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpicById(int epicId);

    ArrayList<Subtask> getEpicsSubtasks(int epicId);

}
