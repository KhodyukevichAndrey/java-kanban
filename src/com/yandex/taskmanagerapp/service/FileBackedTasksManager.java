package com.yandex.taskmanagerapp.service;

import com.yandex.taskmanagerapp.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    public static void main (String[] args) {

        FileBackedTasksManager fileBackedTasksManager = Managers.loadFromFile(new File("tasksFile.csv"));

        Epic epic1 = new Epic("epicName1","description1");
        Epic epic2 = new Epic("epicName2", "description2");
        Epic epic3 = new Epic("epicName3", "description3");

        fileBackedTasksManager.addNewEpic(epic1);
        fileBackedTasksManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("SubtaskName1", "description1", Statuses.DONE, epic1.getId());
        Subtask subtask2 = new Subtask("SubtaskName2", "description2", Statuses.DONE, epic2.getId());
        Subtask subtask3 = new Subtask("SubtaskName3", "description3", Statuses.NEW, epic2.getId());

        fileBackedTasksManager.addNewSubtask(subtask1);
        fileBackedTasksManager.addNewSubtask(subtask2);
        fileBackedTasksManager.addNewSubtask(subtask3);
        fileBackedTasksManager.addNewEpic(epic3);

        System.out.println(fileBackedTasksManager.getEpicById(epic1.getId()));
        System.out.println(fileBackedTasksManager.getEpicById(epic2.getId()));
        System.out.println(fileBackedTasksManager.getSubtaskById(subtask1.getId()));

        System.out.println(fileBackedTasksManager.getHistory());

        System.out.println(fileBackedTasksManager.getHistory());

        fileBackedTasksManager = Managers.loadFromFile(new File("tasksFile.csv"));

        System.out.println(fileBackedTasksManager.getHistory());
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = super.getTaskById(taskId);
        save();
        return task;
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        super.addNewSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        super.deleteSubtaskById(subtaskId);
        save();
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = super.getSubtaskById(subtaskId);
        save();
        return subtask;
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = super.getEpicById(epicId);
        save();
        return epic;
    }

    @Override
    public void addNewEpic (Epic epic) {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    public Task taskFromString(String value) {
        String[] elements = value.split(",");
        Statuses status = Statuses.valueOf(elements[3]);
        int id = Integer.parseInt(elements[1]);
        if(super.getNewId() < id) {
            super.setNewId(id);
        }
        if (elements[0].equals("TASK")) {
            Task task = new Task(elements[2], elements[4], status);
            task.setId(id);
            return task;
        } else if (elements[0].equals("SUBTASK")) {
            int epicId = Integer.parseInt(elements[5]);
            Subtask subtask = new Subtask(elements[2], elements[4], status, epicId);
            subtask.setId(id);
            return subtask;
        } else {
            Epic epic = new Epic(elements[2], elements[4]);
            epic.setId(id);
            return epic;
        }
    }

    public List<Integer> historyFromString(String value) throws ManagerSaveException {
        String[] historyElements = value.split(",");
        List<Integer> history = new ArrayList<>();
        for (String element : historyElements) {
            history.add(Integer.parseInt(element));
        }
        return history;
    }

    private void save() throws ManagerSaveException {
        List<String> ConvertedTasksToLines = new ArrayList<>();

        for(Epic epic : super.getAllEpics()) {
            ConvertedTasksToLines.add(Types.EPIC + "," + epic.toString());
        }
        for(Subtask subtask : super.getAllSubtask()) {
            ConvertedTasksToLines.add(Types.SUBTASK + "," + subtask.toString());
        }
        for(Task task : super.getAllTask()) {
            ConvertedTasksToLines.add(Types.TASK + "," + task.toString());
        }

        try(Writer writer = new FileWriter("tasksFile.csv")) {
            writer.write("type,id,name,status,description,epicId");
            for(String taskInLine : ConvertedTasksToLines) {
                writer.write("\n" + taskInLine);
            }
            writer.write("\n");
            writer.write("\n" + Managers.historyToString(super.getHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException("Невозможно записать файл");
        }
    }
}

