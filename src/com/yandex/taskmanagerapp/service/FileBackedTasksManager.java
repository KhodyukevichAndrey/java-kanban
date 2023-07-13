package com.yandex.taskmanagerapp.service;

import com.yandex.taskmanagerapp.exceptions.ManagerSaveException;
import com.yandex.taskmanagerapp.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;
    private static final String HEADLINE = "type,id,name,status,description,duration,startTime,epicId/endTime";

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static void main (String[] args) {

        File file = new File("tasksFile.csv");
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);

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

    public static FileBackedTasksManager loadFromFile(File file) {
        List<String> allLinesOfFile = new ArrayList<>();
        FileBackedTasksManager fb = new FileBackedTasksManager(file);
        try(BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while(br.ready()) {
                String line = br.readLine();
                allLinesOfFile.add(line);
                Task taskFromFile = fb.taskFromString(line);
                if (taskFromFile != null) {
                    switch (taskFromFile.getType()) {
                        case TASK:
                            fb.tasks.put(taskFromFile.getId(), taskFromFile);
                            break;
                        case SUBTASK:
                            Subtask subtask = (Subtask) taskFromFile;
                            fb.subtasks.put(taskFromFile.getId(), subtask);
                            fb.epics.get(subtask.getIdEpic()).getEpicsSubtasksId().add(subtask.getId());
                            break;
                        case EPIC:
                            fb.epics.put(taskFromFile.getId(), (Epic) taskFromFile);
                            break;
                    }
                }
            }
            if(allLinesOfFile.isEmpty()) {
                return fb;
            }
            String historyLine = allLinesOfFile.get(allLinesOfFile.size() - 1);
            List<Integer> history = fb.historyFromString(historyLine);
            for (Integer id : history) {
                fb.historyManager.addTaskToHistory(fb.tasks.get(id));
                fb.historyManager.addTaskToHistory(fb.subtasks.get(id));
                fb.historyManager.addTaskToHistory(fb.epics.get(id));
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Невозможно прочитать файл");
        }
        return fb;
    }

    private void save() {
        List<String> convertedTasksToLines = convertTaskToLines(); // вынес в отдельный метод

        try(Writer writer = new FileWriter(file)) {
            writer.write(HEADLINE);
            for(String taskInLine : convertedTasksToLines) {
                writer.write("\n" + taskInLine);
            }
            writer.write("\n");
            writer.write("\n" + historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Невозможно записать файл");
        }
    }


    private Task taskFromString(String value) {
        String[] elements = value.split(",");
        Type type;
        try {
            type = Type.valueOf(elements[0]);
        } catch (IllegalArgumentException e) {
            return null;
        }
        int id = Integer.parseInt(elements[1]);
        String taskName = elements[2];
        Statuses status = Statuses.valueOf(elements[3]);
        String taskDescription = elements[4];
        int duration = Integer.parseInt(elements[5]);
        LocalDateTime startTime = null;
        if(!elements[6].contains("null")) {
            startTime = LocalDateTime.parse(elements[6]);
        }

        if (newId < id) {
            newId = id;
        }
        switch (type) {
            case TASK:
                Task task = new Task(taskName, taskDescription, status, duration, startTime);
                task.setId(id);
                return task;
            case SUBTASK:
                int epicId = Integer.parseInt(elements[7]);
                Subtask subtask = new Subtask(taskName, taskDescription, status, duration, startTime, epicId);
                subtask.setId(id);
                return subtask;
            case EPIC:
                Epic epic = new Epic(taskName, taskDescription);
                epic.setStatus(status);
                epic.setId(id);
                epic.setDuration(duration);
                epic.setStartTime(startTime);
                if(!elements[7].contains("null")) {
                    epic.setEndTime(LocalDateTime.parse(elements[8]));
                }
                return epic;
        }
        return null;
    }

    private List<String> convertTaskToLines() {
        List<String> tasksLines = new ArrayList<>();

        for(Epic epic : super.getAllEpics()) {
            tasksLines.add(epic.getType() + "," + epic);
        }
        for(Subtask subtask : super.getAllSubtask()) {
            tasksLines.add(subtask.getType() + "," + subtask);
        }
        for(Task task : super.getAllTask()) {
            tasksLines.add(task.getType() + "," + task);
        }
        return tasksLines;
    }

    private static String historyToString(HistoryManager manager) {
        List<Task> lineOfTasksId = manager.getHistory();
        List<String> idToString = new ArrayList<>();
        for (Task task : lineOfTasksId) {
            idToString.add(Integer.toString(task.getId()));
        }
        return String.join(",", idToString);
    }

    private List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        if(!value.isBlank()) {
            String[] historyElements = value.split(",");
            for (String element : historyElements) {
                history.add(Integer.parseInt(element));
            }
        }
        return history;
    }
}

