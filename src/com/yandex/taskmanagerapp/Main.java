package com.yandex.taskmanagerapp;

import com.yandex.taskmanagerapp.model.Epic;
import com.yandex.taskmanagerapp.model.Statuses;
import com.yandex.taskmanagerapp.model.Subtask;
import com.yandex.taskmanagerapp.model.Task;
import com.yandex.taskmanagerapp.service.Managers;
import com.yandex.taskmanagerapp.service.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Таск1", "Положить в посудомойку" , Statuses.NEW);
        taskManager.addNewTask(task1);

        Epic epic1 = new Epic("Эпик1","a");
        Epic epic2 = new Epic("Эпик2", "b");

        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("1", "a", Statuses.DONE, epic1.getId());
        Subtask subtask2 = new Subtask("2", "b", Statuses.DONE, epic1.getId());
        Subtask subtask3 = new Subtask("3", "c", Statuses.NEW, epic1.getId());

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        taskManager.getEpicById(epic1.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getSubtaskById(subtask2.getId());


        System.out.println(taskManager.getHistory());
        taskManager.deleteEpicById(epic1.getId());
        System.out.println(taskManager.getHistory());
    }
}
