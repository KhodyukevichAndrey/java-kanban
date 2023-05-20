package com.yandex.taskmanagerapp;

import com.yandex.taskmanagerapp.model.Epic;
import com.yandex.taskmanagerapp.model.Statuses;
import com.yandex.taskmanagerapp.model.Subtask;
import com.yandex.taskmanagerapp.model.Task;
import com.yandex.taskmanagerapp.service.Managers;
import com.yandex.taskmanagerapp.service.TaskManager;

public class Main {

    public static void main(String[] args) {

        Managers managers = new Managers();
        TaskManager inMemoryTaskManager = managers.getDefault();

        Task task1 = new Task("Таск1", "Положить в посудомойку" , Statuses.NEW);
        inMemoryTaskManager.addNewTask(task1);

        Epic epic1 = new Epic("Эпик1","Сбор вещей");
        Epic epic2 = new Epic("Эпик2", "Навести порядок");

        inMemoryTaskManager.addNewEpic(epic1);
        inMemoryTaskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("1", "Сбор одежды", Statuses.DONE, epic1.getId());
        Subtask subtask2 = new Subtask("2", "Сбор документов", Statuses.DONE, epic1.getId());
        Subtask subtask3 = new Subtask("3", "Пропылесосить", Statuses.NEW, epic2.getId());

        inMemoryTaskManager.addNewSubtask(subtask1);
        inMemoryTaskManager.addNewSubtask(subtask2);
        inMemoryTaskManager.addNewSubtask(subtask3);

        System.out.println(inMemoryTaskManager.getEpicsSubtasks(epic2.getId()));
        System.out.println(inMemoryTaskManager.getEpicById(epic1.getId()));
        System.out.println(inMemoryTaskManager.getSubtaskById(subtask2.getId()));

        System.out.println(Managers.getDefaultHistory());
    }
}
