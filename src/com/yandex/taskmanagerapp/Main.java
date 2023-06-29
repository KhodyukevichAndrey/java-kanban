package com.yandex.taskmanagerapp;

import com.yandex.taskmanagerapp.model.*;
import com.yandex.taskmanagerapp.service.*;


public class Main {

    public static void main (String[] args) {

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
        Epic epic1 = new Epic("epicName1","description1");
        fileBackedTasksManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("SubtaskName1", "description1", Statuses.DONE, epic1.getId());
        fileBackedTasksManager.addNewSubtask(subtask1);

    }
}
