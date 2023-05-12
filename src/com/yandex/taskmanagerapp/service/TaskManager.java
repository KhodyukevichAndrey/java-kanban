package com.yandex.taskmanagerapp.service;

import com.yandex.taskmanagerapp.model.Epic;
import com.yandex.taskmanagerapp.model.Subtask;
import com.yandex.taskmanagerapp.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TaskManager {

    private int newId = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    public ArrayList<Task> getAllTask() {                                   // Tasks //
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTask() {
        tasks.clear();
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public void addNewTask(Task task) {
        task.setId(generateId());
        int id = task.getId();
        tasks.put(id, task);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    public ArrayList<Subtask> getAllSubtask() {                            // Subtasks //
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubtask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getEpicsSubtasksId().clear();
            updateEpicStatus(epic.getId());
        }
    }

    public Subtask getSubtaskById(int subtaskId) {
        return subtasks.get(subtaskId);
    }

    public void addNewSubtask(Subtask subtask) {
        subtask.setId(generateId());
        int id = subtask.getId();
        subtasks.put(id, subtask);
        Epic currentEpic = epics.get(subtask.getIdEpic());
        ArrayList<Integer> epicSubtasksId = currentEpic.getEpicsSubtasksId();
        epicSubtasksId.add(id);
        updateEpicStatus(subtask.getIdEpic());
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getIdEpic());
    }

    public void deleteSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        Epic currentEpic = epics.get(subtask.getIdEpic());
        for (Integer epicSubtaskId : currentEpic.getEpicsSubtasksId()) {
            currentEpic.getEpicsSubtasksId().remove(epicSubtaskId);
        }
        subtasks.remove(subtaskId);
        updateEpicStatus(subtask.getIdEpic());
    }

    public ArrayList<Epic> getAllEpics() {                               //EPIC
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubtask();
    }

    public Epic getEpicById(int epicId) {
        return epics.get(epicId);
    }

    public void addNewEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic); /* Сделал аналогчно Таскам, без проверки условия,
        но про сокращение времени на обработку понял ;) */
    }

    public void deleteEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        for (Integer subtaskId : epic.getEpicsSubtasksId()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(epicId);
    }

    public ArrayList<Subtask> getEpicsSubtasks(int epicId) {
        ArrayList<Subtask> currentEpicsSubtasks = new ArrayList<>();
        for (Integer subtaskId : epics.get(epicId).getEpicsSubtasksId()) { // оптимальное итерирование
            if(subtasks.get(subtaskId) == null) {
                continue;
            }
            currentEpicsSubtasks.add(subtasks.get(subtaskId));
        }
        return currentEpicsSubtasks;
    }

    private void updateEpicStatus(int epicId) {
        HashSet<String> statuses = new HashSet<>();
        Epic currentEpic = epics.get(epicId);
        for (Integer subtaskId : epics.get(epicId).getEpicsSubtasksId()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null) { /* Пришлось добавить данную проверку, т.к. сначала удаляется сабтаска,
                                   а потом апдейт пытается ее из списка сабтасек получить, и не находит
                                   (можно было еще реализовать через contains, но показалось с нулл получше выглядит */
                continue;
            }
            if (subtask.getIdEpic() == epicId) {
                    statuses.add(subtask.getStatus());
                }
            }
        if(statuses.size() > 1 || statuses.contains("IN_PROGRESS")) {
            currentEpic.setStatus("IN_PROGRESS");
        } else if (statuses.contains("NEW") || statuses.isEmpty()) {
            currentEpic.setStatus("NEW");
        } else {
            currentEpic.setStatus("DONE");
        }

    }

    private int generateId() {
        return ++newId;
    }

}
