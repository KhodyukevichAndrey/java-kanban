package taskproject.service;

import taskproject.models.Epic;
import taskproject.models.Subtask;
import taskproject.models.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    int newId = 0;
    ArrayList<Integer> id = new ArrayList<>();
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();
    HashMap<HashMap<Integer, Epic>, ArrayList<Subtask>> epicSubtasks = new HashMap<>();

    public int generateId() {
        newId++;
        id.add(newId);
        return newId;
    }

    public ArrayList<Task> getAllTask () {                                   // Tasks //
        ArrayList<Task> allTask = new ArrayList<>();
        allTask.addAll(tasks.values());
        return allTask;
    }

    public void deleteAllTask() {
        tasks.clear();
    }

    public Task getTaskById (int taskId) {
        return tasks.get(taskId);
    }

    public void addNewTask(Task task) {
        task.setId(generateId());
        int id = task.getId();
        tasks.put(id, task);
    }

    public void updateTaskStatus (Task task) {
        int id = task.getId();
        tasks.put(id, task);
    }

    public void deleteTaskById (int taskId) {
        tasks.remove(taskId);
    }

    public ArrayList<Subtask> getAllSubtask () {                            // Subtasks //
        ArrayList<Subtask> allSubtask = new ArrayList<>();
        allSubtask.addAll(subtasks.values());
        return allSubtask;
    }

    public void deleteAllSubtask() {
        subtasks.clear();
    }

    public Subtask getSubtaskById (int subtaskId) {
        return subtasks.get(subtaskId);
    }

    public void addNewSubtask (Subtask subtask) {
        subtask.setId(generateId());
        int id = subtask.getId();
        subtasks.put(id, subtask);
        subtask.setIdEpic(subtask.getIdEpic());
        Epic currentEpic = getEpicById(subtask.getIdEpic());
        HashMap<Integer, Epic> currentEpicMap = new HashMap<>();
        currentEpicMap.put(subtask.getIdEpic(),currentEpic);
        for(HashMap<Integer, Epic> currentMap : epicSubtasks.keySet()) {
            if(currentMap.equals(currentEpicMap)) {
                ArrayList<Subtask> currentSubtask = epicSubtasks.get(currentMap);
                currentSubtask.add(subtask);
            }
        }
        updateEpicStatus(subtask.getIdEpic());
    }

    public void updateSubtask (Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getIdEpic());
    }

    public void deleteSubtaskById (int subtaskId) {
        Subtask subtask = getSubtaskById(subtaskId);
        Epic currentEpic = getEpicById(subtask.getIdEpic());
        HashMap<Integer, Epic> currentEpicMap = new HashMap<>();
        currentEpicMap.put(subtask.getIdEpic(),currentEpic);
        for(HashMap<Integer, Epic> currentMap : epicSubtasks.keySet()) {
            if (currentMap.equals(currentEpicMap)) {
                ArrayList<Subtask> currentSubtask = epicSubtasks.get(currentMap);
                currentSubtask.remove(subtask);
            }
        }
        subtasks.remove(subtaskId);
    }

    public ArrayList<Epic> getAllEpics () {                                         // Epics //
        ArrayList<Epic> allEpics = new ArrayList<>();
        for(HashMap<Integer, Epic> epics : epicSubtasks.keySet()) {
            allEpics.addAll(epics.values());
        }
        return allEpics;
    }

    public void deleteAllEpics () {
        epicSubtasks.clear();
    }

    public Epic getEpicById (int epicId) {
        Epic epic1 = null;
        HashMap<Integer, Epic> currentEpicMap = new HashMap<>();
        for(HashMap<Integer, Epic> epics : epicSubtasks.keySet()) {
            if(epics.containsKey(epicId)) {
                epic1 = epics.get(epicId);
            }
        }
        return epic1;
    }

    public void addNewEpic (Epic epic) {
        epic.setId(generateId());
        int id = epic.getId();
        HashMap<Integer, Epic> newEpic = new HashMap<>();
        newEpic.put(id, epic);
        ArrayList<Subtask> newListForSubtasks = new ArrayList<>();
        epicSubtasks.put(newEpic, newListForSubtasks);
    }

    public void updateEpic (Epic epic) {
        int id = epic.getId();
        HashMap<Integer, Epic> currentEpic = new HashMap<>();
        currentEpic.put(id, epic);
        for(HashMap<Integer, Epic> epics : epicSubtasks.keySet()) {
            if(epics.equals(currentEpic)) {
                epicSubtasks.put(currentEpic, epicSubtasks.get(currentEpic));
            }
        }
    }

    public void deleteEpicById (int epicId) {
        for (HashMap<Integer, Epic> epics : epicSubtasks.keySet()) {
            Epic currentEpic = epics.get(epicId);
            HashMap<Integer, Epic> currentEpicMap = new HashMap<>();
            currentEpicMap.put(epicId, currentEpic);
            epicSubtasks.remove(currentEpicMap);
        }
    }

    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> subtasks1 = new ArrayList<>();
        Epic currentEpic = getEpicById(epicId);
        HashMap<Integer, Epic> currentEpicMap = new HashMap<>();
        currentEpicMap.put(epicId, currentEpic);
        for(HashMap<Integer, Epic> currentMap : epicSubtasks.keySet()) {
            if(currentMap.equals(currentEpicMap)) {
                ArrayList<Subtask> currentSubtask = epicSubtasks.get(currentMap);
                subtasks1.addAll(currentSubtask);
            }
        }
        return subtasks1;
    }

    public void updateEpicStatus(int idEpic) {
        Epic currentEpic = getEpicById(idEpic);
        ArrayList<Subtask> epicsBySubtasks = getEpicSubtasks(idEpic);
        ArrayList<String> epicStatuses = new ArrayList<>();
        for (Subtask subtask : epicsBySubtasks) {
            epicStatuses.add(subtask.getStatus());
        }
        if(epicStatuses.contains("IN_PROGRESS")) {
            currentEpic.setStatus("IN_PROGRESS");
        } else if (epicStatuses.contains("NEW") && !epicStatuses.contains("DONE")) {
            currentEpic.setStatus("NEW");
        } else if (epicStatuses.contains("NEW") && epicStatuses.contains("DONE")) {
            currentEpic.setStatus("IN_PROGRESS");
        } else {
            currentEpic.setStatus("DONE");
        }
    }
}
