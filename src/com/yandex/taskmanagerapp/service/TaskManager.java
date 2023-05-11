package com.yandex.taskmanagerapp.service;

import com.yandex.taskmanagerapp.models.Epic;
import com.yandex.taskmanagerapp.models.Subtask;
import com.yandex.taskmanagerapp.models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TaskManager {

    private int newId = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    public ArrayList<Task> getAllTask() {                                   // Tasks //
        return new ArrayList<Task>(tasks.values());
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

    public void updateTaskStatus(Task task) {
        for (Task task1 : tasks.values()) {
            if (task.getName().equals(task1.getName()) || task.getDescription().equals(task1.getDescription())) {
                int id = task1.getId();
                tasks.put(id, task);
            }
        }
    }

    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    public ArrayList<Subtask> getAllSubtask() {                            // Subtasks //
        return new ArrayList<Subtask>(subtasks.values());
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
        for (Subtask subtask1 : subtasks.values()) {
            if (subtask.getName().equals(subtask1.getName())
                    || subtask.getDescription().equals(subtask1.getDescription())) {
                int id = subtask1.getId();
                tasks.put(id, subtask);
            }
        }
        updateEpicStatus(subtask.getIdEpic());
    }

    public void deleteSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        subtasks.remove(subtaskId);
        updateEpicStatus(subtask.getIdEpic());
    }

    public ArrayList<Epic> getAllEpics() {                               //EPIC
        return new ArrayList<Epic>(epics.values());
    }

    public void deleteAllEpics() {
        epics.clear();
        deleteAllSubtask();
    }

    public Epic getEpicByid(int epicId) {
        return epics.get(epicId);
    }

    public void addNewEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    public void updateEpic(Epic epic) {
        for (Integer epicId : epics.keySet()) {
            if (epic.getId() == epicId) {
                epics.put(epicId, epic);
            }
        }
    }

    public void deleteEpicById(int epicId) {
        epics.remove(epicId);
    }

    public ArrayList<Subtask> getEpicsSubtasks(int epicId) {
        ArrayList<Subtask> currentEpicsSubtasks = new ArrayList<>();
        for (Subtask currentSubtask : subtasks.values()) {
            if (currentSubtask.getIdEpic() == epicId) {
                currentEpicsSubtasks.add(currentSubtask);
            }
        }
        return currentEpicsSubtasks;
    }

    private void updateEpicStatus(int idEpic) {
        HashSet<String> statuses = new HashSet<>();
        /* HashSet не было в теории, погуглил :)
        вроде +- понял, реализовал в таком виде,
        но возможно можно это сделать проще */
        Epic currentEpic = epics.get(idEpic);
        for (Integer idSubtasks : subtasks.keySet()) {
            Subtask subtask = subtasks.get(idSubtasks);
            if (subtask.getIdEpic() == idEpic) {
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
