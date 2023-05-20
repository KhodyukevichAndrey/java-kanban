package com.yandex.taskmanagerapp.service;

import com.yandex.taskmanagerapp.model.Epic;
import com.yandex.taskmanagerapp.model.Statuses;
import com.yandex.taskmanagerapp.model.Subtask;
import com.yandex.taskmanagerapp.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class InMemoryTaskManager implements TaskManager {

    InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    private int newId = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    @Override
    public ArrayList<Task> getAllTask() {                                   // Tasks //
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTask() {
        tasks.clear();
    }

    @Override
    public Task getTaskById(int taskId) {
        inMemoryHistoryManager.addTaskToHistory(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public void addNewTask(Task task) {
        task.setId(generateId());
        int id = task.getId();
        tasks.put(id, task);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {                            // Subtasks //
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getEpicsSubtasksId().clear();
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        inMemoryHistoryManager.addTaskToHistory(subtasks.get(subtaskId));
        return subtasks.get(subtaskId);
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        subtask.setId(generateId());
        int id = subtask.getId();
        subtasks.put(id, subtask);
        Epic currentEpic = epics.get(subtask.getIdEpic());
        ArrayList<Integer> epicSubtasksId = currentEpic.getEpicsSubtasksId();
        epicSubtasksId.add(id);
        updateEpicStatus(subtask.getIdEpic());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getIdEpic());
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);
        Epic currentEpic = epics.get(subtask.getIdEpic());
        currentEpic.getEpicsSubtasksId().remove((Integer) subtaskId);
        updateEpicStatus(subtask.getIdEpic());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {                               //EPIC
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubtask();
    }

    @Override
    public Epic getEpicById(int epicId) {
        inMemoryHistoryManager.addTaskToHistory(epics.get(epicId));
        return epics.get(epicId);
    }

    @Override
    public void addNewEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void deleteEpicById(int epicId) {
        Epic epic = epics.remove(epicId);
        for (Integer subtaskId : epic.getEpicsSubtasksId()) {
            subtasks.remove(subtaskId);
        }
    }

    @Override
    public ArrayList<Subtask> getEpicsSubtasks(int epicId) {
        ArrayList<Subtask> currentEpicsSubtasks = new ArrayList<>();
        for (Integer subtaskId : epics.get(epicId).getEpicsSubtasksId()) {
            currentEpicsSubtasks.add(subtasks.get(subtaskId));
        }
        return currentEpicsSubtasks;
    }

    private void updateEpicStatus(int epicId) {
        HashSet<Statuses> statuses = new HashSet<>();
        Epic currentEpic = epics.get(epicId);
        for (Integer subtaskId : epics.get(epicId).getEpicsSubtasksId()) {
            Subtask subtask = subtasks.get(subtaskId);
            statuses.add(subtask.getStatus());
        }
        if(statuses.size() > 1 || statuses.contains(Statuses.IN_PROGRESS)) {
            currentEpic.setStatus(Statuses.IN_PROGRESS);
        } else if (statuses.contains(Statuses.NEW) || statuses.isEmpty()) {
            currentEpic.setStatus(Statuses.NEW);
        } else {
            currentEpic.setStatus(Statuses.DONE);
        }

    }

    private int generateId() {
        return ++newId;
    }
}
