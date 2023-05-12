package com.yandex.taskmanagerapp;

import com.yandex.taskmanagerapp.model.Task; // импорты для тестов
import com.yandex.taskmanagerapp.model.Subtask;
import com.yandex.taskmanagerapp.model.Epic;
import com.yandex.taskmanagerapp.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // - Шаблоны для тестов, на случай если пригодяться

        Task task1 = new Task("Помыть посуду", "Положить в посудомойку" , "NEW");

        taskManager.addNewTask(task1);

        Epic epic1 = new Epic("Переезд","Сбор вещей");
        Epic epic2 = new Epic("Уборка", "Навести порядок");

        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        Subtask subtask1;
        subtask1 = new Subtask("Сбор вещей", "Сбор одежды", "DONE", epic1.getId());
        Subtask subtask2;
        subtask2 = new Subtask("Сбор вещей", "Сбор документов", "NEW", epic1.getId());
        Subtask subtask3;
        subtask3 = new Subtask("Уборка", "Пропылесосить", "IN_PROGRESS", epic2.getId());

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        System.out.println(epic1.toString());
        System.out.println(epic2.toString());
        System.out.println(subtask1.toString());
        System.out.println(subtask2.toString());
        System.out.println(subtask3.toString());

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtask());

        taskManager.deleteEpicById(epic2.getId());
        taskManager.deleteSubtaskById(subtask1.getId());

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtask());
        System.out.println(taskManager.getEpicsSubtasks(epic1.getId()));
        System.out.println(taskManager.getSubtaskById(subtask2.getId()));
        System.out.println(taskManager.getEpicById(epic1.getId()));
    }
}
