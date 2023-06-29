package com.yandex.taskmanagerapp.service;

import com.yandex.taskmanagerapp.model.Epic;
import com.yandex.taskmanagerapp.model.ManagerSaveException;
import com.yandex.taskmanagerapp.model.Subtask;
import com.yandex.taskmanagerapp.model.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static String historyToString(HistoryManager manager) {
        List<Task> lineOfTasksId = manager.getHistory();
        List<String> idToString = new ArrayList<>();
        for (Task task : lineOfTasksId) {
            idToString.add(Integer.toString(task.getId()));
        }
        return String.join(",", idToString);
    }


    public static FileBackedTasksManager loadFromFile(File file) {
        List<String> allLinesOfFile = new ArrayList<>();
        FileBackedTasksManager fb = new FileBackedTasksManager();
        try(BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while(br.ready()) {
                String line = br.readLine();
                allLinesOfFile.add(line);
            }
            if(allLinesOfFile.isEmpty()) {
                return new FileBackedTasksManager();
            } else {
                for (String line : allLinesOfFile) {
                    if (line.contains("EPIC")) {
                        fb.addNewEpic((Epic) fb.taskFromString(line));
                    } else if (line.contains("SUBTASK")) {
                        fb.addNewSubtask((Subtask) fb.taskFromString(line));
                    } else if (line.contains("TASK")) {
                        fb.addNewTask(fb.taskFromString(line));
                    }
                }
                String historyLine = allLinesOfFile.get(allLinesOfFile.size() - 1);
                List<Integer> history = fb.historyFromString(historyLine);
                for (Integer id : history) {
                    fb.getTaskById(id);
                    fb.getSubtaskById(id);
                    fb.getEpicById(id);
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Невозможно прочитать файл");
        }
        return fb;
    }
}
