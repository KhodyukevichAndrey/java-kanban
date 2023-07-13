package com.yandex.taskmanagerapp.service;

import com.yandex.taskmanagerapp.model.Statuses;
import com.yandex.taskmanagerapp.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    HistoryManager historyManager1;
    HistoryManager historyManager2;
    Task task1;
    Task task2;
    Task task3;

    @BeforeEach
    public void createHistoryTestEnvironment() {
        historyManager1 = Managers.getDefaultHistory();
        historyManager2 = Managers.getDefaultHistory();
        task1 = new Task ("task1", "description1", Statuses.NEW);
        task1.setId(1);
        task2 = new Task ("task2", "description2", Statuses.NEW);
        task2.setId(2);
        task3 = new Task ("task2", "description2", Statuses.NEW);
        task3.setId(3);
    }

    @Test
    public void shouldAddTaskToHistory() {
        historyManager1.addTaskToHistory(task1);
        final Task task4 = historyManager1.getHistory().get(0);

        assertEquals(task1, task4, "Добавленная задача не соответствует");
    }

    @Test
    public void shouldRemoveTaskFromHistory() {
        final List<Task> emptyHistory = historyManager1.getHistory();
        historyManager1.addTaskToHistory(task1);
        historyManager1.removeTaskInHistory(task1.getId());

        assertEquals(emptyHistory, historyManager1.getHistory(), "Задача удалена некорректно");
    }

    @Test
    public void shouldGiveHistory() {
        final List<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        historyManager1.addTaskToHistory(task1);

        assertEquals(tasks, historyManager1.getHistory(), "Возвращаемая история не соответствует");
    }

    @Test
    public void shouldReturnEmptyHistory() {
        final List<Task> history = historyManager1.getHistory();

        assertEquals(0, history.size(), "История не пустая");
    }

    @Test
    public void shouldReturnCorrectHistoryWithoutRepeats() {
        historyManager1.addTaskToHistory(task1);
        final List<Task> historyWithOneTask = historyManager1.getHistory();
        historyManager1.addTaskToHistory(task1);
        final List<Task> historyAfterRepeat = historyManager1.getHistory();

        assertEquals(historyWithOneTask, historyAfterRepeat, "История содержит повторы");
    }

    @Test
    public void shouldRemoveTaskInHistoryFromStart() {
        historyManager1.addTaskToHistory(task1);
        historyManager1.addTaskToHistory(task2);
        historyManager1.removeTaskInHistory(task1.getId());
        historyManager2.addTaskToHistory(task2);

        final List<Task> historyAfterFirstRemove = historyManager1.getHistory();
        final List<Task> historyWithSecondTask = historyManager2.getHistory();

        assertEquals(historyWithSecondTask, historyAfterFirstRemove,
                "Некорректно удалены задачи из начала истории");
    }

    @Test
    public void shouldRemoveTaskInHistoryFromMiddle() {
        historyManager1.addTaskToHistory(task1);
        historyManager1.addTaskToHistory(task2);
        historyManager1.addTaskToHistory(task3);
        historyManager1.removeTaskInHistory(task2.getId());
        historyManager2.addTaskToHistory(task1);
        historyManager2.addTaskToHistory(task3);

        final List<Task> historyAfterRemoveMiddleTask = historyManager1.getHistory();
        final List<Task> historyWithFirstAndThirdTask = historyManager2.getHistory();

        assertEquals(historyWithFirstAndThirdTask, historyAfterRemoveMiddleTask,
                "Некорректно удалены задачи из середины истории");
    }

    @Test
    public void shouldRemoveTaskInHistoryFromEnd() {
        historyManager1.addTaskToHistory(task1);
        historyManager1.addTaskToHistory(task2);
        historyManager1.addTaskToHistory(task3);
        historyManager1.removeTaskInHistory(task3.getId());
        historyManager2.addTaskToHistory(task1);
        historyManager2.addTaskToHistory(task2);

        final List<Task> historyAfterRemoveLastTask = historyManager1.getHistory();
        final List<Task> historyWithoutThirdTask = historyManager2.getHistory();

        assertEquals(historyWithoutThirdTask, historyAfterRemoveLastTask,
                "Некорректно удалены задачи из конца истории");
    }
}