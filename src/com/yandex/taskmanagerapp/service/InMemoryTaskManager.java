package com.yandex.taskmanagerapp.service;

import com.yandex.taskmanagerapp.model.Epic;
import com.yandex.taskmanagerapp.model.Statuses;
import com.yandex.taskmanagerapp.model.Subtask;
import com.yandex.taskmanagerapp.model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int newId = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    @Override
    public List<Task> getAllTask() {                                   // Tasks //
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTask() {
        tasks.clear();
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        historyManager.addTaskToHistory(task);
        return task;
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
        historyManager.removeTaskInHistory(taskId);
        tasks.remove(taskId);
    }

    @Override
    public List<Subtask> getAllSubtask() {                            // Subtasks //
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
        Subtask subtask = subtasks.get(subtaskId);
        historyManager.addTaskToHistory(subtask);
        return subtask;
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        subtask.setId(generateId());
        int id = subtask.getId();
        subtasks.put(id, subtask);
        Epic currentEpic = epics.get(subtask.getIdEpic());
        List<Integer> epicSubtasksId = currentEpic.getEpicsSubtasksId();
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
        historyManager.removeTaskInHistory(subtaskId);
        Subtask subtask = subtasks.remove(subtaskId);
        Epic currentEpic = epics.get(subtask.getIdEpic());
        currentEpic.getEpicsSubtasksId().remove((Integer) subtaskId);
        updateEpicStatus(subtask.getIdEpic());
    }

    @Override
    public List<Epic> getAllEpics() {                               //EPIC
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubtask();
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        historyManager.addTaskToHistory(epic);
        return epic;
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
        historyManager.removeTaskInHistory(epicId);
        Epic epic = epics.remove(epicId);
        for (Integer subtaskId : epic.getEpicsSubtasksId()) {
            historyManager.removeTaskInHistory(subtaskId);
            subtasks.remove(subtaskId);
        }
    }

    @Override
    public List<Subtask> getEpicsSubtasks(int epicId) {
        List<Subtask> currentEpicsSubtasks = new ArrayList<>();
        for (Integer subtaskId : epics.get(epicId).getEpicsSubtasksId()) {
            currentEpicsSubtasks.add(subtasks.get(subtaskId));
        }
        return currentEpicsSubtasks;
    }

    @Override public List<Task> getHistory() {
        return historyManager.getHistory();
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
