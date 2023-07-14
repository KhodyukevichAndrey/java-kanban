package com.yandex.taskmanagerapp.service;

import com.yandex.taskmanagerapp.model.Epic;
import com.yandex.taskmanagerapp.model.Statuses;
import com.yandex.taskmanagerapp.model.Subtask;
import com.yandex.taskmanagerapp.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int newId = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();

    Comparator<Task> comparator = (o1, o2) -> {
        LocalDateTime first = o1.getStartTime();
        LocalDateTime second = o2.getStartTime();
        if (first != null && second != null) {
            if (first.isBefore(second)) {
                return -1;
            } else if (first.equals(second)) {
                return 0;
            } else return 1;
        } else if (first == null) {
            return 1;
        } else {
            return -1;
        }
    };
    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(comparator);

    @Override
    public List<Task> getAllTask() {                                   // Tasks //
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTask() {
        for (Task task : tasks.values()) {
            historyManager.removeTaskInHistory(task.getId());
            prioritizedTasks.remove(task);
        }
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
        if (checkCrossValues(task)) {
            tasks.put(id, task);
        }
    }

    @Override
    public void updateTask(Task task) {
        Task task1 = tasks.get(task.getId());
        prioritizedTasks.remove(task1); // Добавлено для того, что программа не искала пересечение таски с самой собой
        if (checkCrossValues(task)) {
            tasks.put(task.getId(), task);
        } else {
            prioritizedTasks.add(task1);
        }
    }

    @Override
    public void deleteTaskById(int taskId) {
        historyManager.removeTaskInHistory(taskId);
        prioritizedTasks.remove(tasks.get(taskId));
        tasks.remove(taskId);
    }

    @Override
    public List<Subtask> getAllSubtask() {                            // Subtasks //
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtask() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.removeTaskInHistory(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getEpicsSubtasksId().clear();
            updateEpicStatus(epic.getId());
            updateEpicStartAndEndTime(epic.getId());
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
        if(checkCrossValues(subtask)) {
            subtasks.put(id, subtask);
            Epic currentEpic = epics.get(subtask.getIdEpic());
            List<Integer> epicSubtasksId = currentEpic.getEpicsSubtasksId();
            epicSubtasksId.add(id);
            updateEpicStatus(subtask.getIdEpic());
            updateEpicStartAndEndTime(subtask.getIdEpic());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask subtask1 = subtasks.get(subtask.getId());
        prioritizedTasks.remove(subtask1); // Добавлено для того, что программа не искала пересечени таски с самой собой
        if(checkCrossValues(subtask)) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getIdEpic());
            updateEpicStartAndEndTime(subtask.getIdEpic());
        } else {
            prioritizedTasks.add(subtask1);
        }
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        historyManager.removeTaskInHistory(subtaskId);
        prioritizedTasks.remove(subtasks.get(subtaskId));
        Subtask subtask = subtasks.remove(subtaskId);
        Epic currentEpic = epics.get(subtask.getIdEpic());
        currentEpic.getEpicsSubtasksId().remove((Integer) subtaskId);
        updateEpicStatus(subtask.getIdEpic());
        updateEpicStartAndEndTime(subtask.getIdEpic());
    }

    @Override
    public List<Epic> getAllEpics() {                               //EPIC
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.removeTaskInHistory(epic.getId());
        }
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public TreeSet<Task> getPrioritySet() {
        return prioritizedTasks;
    }

    private void updateEpicStatus(int epicId) {
        HashSet<Statuses> statuses = new HashSet<>();
        Epic currentEpic = epics.get(epicId);
        for (Integer subtaskId : epics.get(epicId).getEpicsSubtasksId()) {
            Subtask subtask = subtasks.get(subtaskId);
            statuses.add(subtask.getStatus());
        }
        if (statuses.size() > 1 || statuses.contains(Statuses.IN_PROGRESS)) {
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

    private boolean checkCrossValues(Task task) {
        LocalDateTime taskStarts = task.getStartTime();
        LocalDateTime taskEnds = task.getEndTime();

        if (taskStarts != null) {
            for (Task taskInTree : prioritizedTasks) {
                LocalDateTime taskInTreeStartTime = taskInTree.getStartTime();
                LocalDateTime taskInTreeEndTime = taskInTree.getEndTime();
                if (taskInTreeStartTime == null) {
                    continue;
                }
                if (!((taskStarts.isBefore(taskInTreeStartTime) && taskEnds.isBefore(taskInTreeStartTime)) ||
                        (taskStarts.isAfter(taskInTreeEndTime)))) {
                    System.out.println("Время выполнения текущей задачи пересекается с временем выполнения задачи - " +
                            taskInTree.getName() + ". Задача не добавлена/обновлена");
                    return false;
                }
            }
        }
        prioritizedTasks.add(task);
        return true;
    }

    private void updateEpicStartAndEndTime(int epicId) {
        Epic currentEpic = epics.get(epicId);

        Optional<LocalDateTime> firstSubtaskStartTime = getFirstSubtaskByStartTime(epicId);

        if (firstSubtaskStartTime.isPresent()) {
            Optional<LocalDateTime> lastSubtaskStartTime = getLastSubtaskByStartTime(epicId);
            LocalDateTime firstStartTime = firstSubtaskStartTime.get();
            currentEpic.setStartTime(firstStartTime);
            int durationOfLastSubtask = 0;
            int durationOfEpic = getEpicDuration(epicId);

            currentEpic.setDuration(durationOfEpic);

            for (Subtask subtask : getEpicsSubtasks(epicId)) {
                if (subtask.getStartTime() != null) {
                    if (firstStartTime.isBefore(subtask.getStartTime())) {
                        durationOfLastSubtask = subtask.getDuration();
                    }
                }
            }
            if (lastSubtaskStartTime.isPresent()) {
                LocalDateTime lastStartTime = lastSubtaskStartTime.get();
                currentEpic.setEndTime(lastStartTime.plus(Duration.ofMinutes(durationOfLastSubtask)));
            }
        } else {
            currentEpic.setStartTime(null);
            currentEpic.setDuration(0);
            currentEpic.setEndTime(null);
        }
    }

    private Optional<LocalDateTime> getFirstSubtaskByStartTime(int epicId) {
        return getEpicsSubtasks(epicId).stream()
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder());
    }

    private Optional<LocalDateTime> getLastSubtaskByStartTime(int epicId) {
        return getEpicsSubtasks(epicId).stream()
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder());
    }

    private int getEpicDuration(int epicId) {
        return getEpicsSubtasks(epicId).stream()
                .map(Task::getDuration)
                .mapToInt(Integer::intValue)
                .sum();
    }
}
