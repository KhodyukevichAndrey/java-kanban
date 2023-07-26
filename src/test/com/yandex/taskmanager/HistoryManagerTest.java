package com.yandex.taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.yandex.taskmanagerapp.model.Statuses;
import com.yandex.taskmanagerapp.model.Task;
import com.yandex.taskmanagerapp.service.HistoryManager;
import com.yandex.taskmanagerapp.service.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;
    private Task task4;

    @BeforeEach
    void createHistoryTestEnvironment() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task ("task1", "description1", Statuses.NEW);
        task1.setId(1);
        task2 = new Task ("task2", "description2", Statuses.NEW);
        task2.setId(2);
        task3 = new Task ("task2", "description2", Statuses.NEW);
        task3.setId(3);
        task4 = null;

        assertTrue(historyManager.getHistory().isEmpty(),
                "Список истории должен быть пустым после инициализации.");

        historyManager.addTaskToHistory(task1);
        historyManager.addTaskToHistory(task2);
    }

    @Test
    void shouldAddTaskToHistory() {
        historyManager.addTaskToHistory(task3);

        assertEquals(task3, historyManager.getHistory().get(2),
                "Задача до и после добавления в историю должна быть неизменна");
        assertEquals(3, historyManager.getHistory().size(),
                "При добавлении задачи в историю, ее размер должен увеличиваться на 1");
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        historyManager.removeTaskInHistory(task1.getId());

        assertEquals(1, historyManager.getHistory().size(),
                "После удаления задачи из истории, размер истории должен быть уменьшен на 1");
    }

    @Test
    void shouldGiveHistory() {
        final List<Task> tasks = historyManager.getHistory();

        assertNotNull(tasks, "Должен быть возвращен список с историей");
        assertEquals(2, tasks.size(), "Размер истории не соответсвует кол-ву добавленных задач");
    }

    @Test
    void shouldReturnEmptyHistory() {
        historyManager.removeTaskInHistory(task1.getId());
        historyManager.removeTaskInHistory(task2.getId());

        assertNotNull(task1, "При удалении задачи из истории, сама задача не должна быть удалена");
        assertTrue(historyManager.getHistory().isEmpty(),
                "При удалении всех задач из истории, она должна быть пустой");
    }

    @Test
    void shouldReturnCorrectHistoryWithoutRepeats() {
        historyManager.addTaskToHistory(task1);
        historyManager.addTaskToHistory(task1);

        assertEquals(2, historyManager.getHistory().size(),
                "При повторном добавлении одинаковых задач в историю," +
                        " ее размер должен оставаться без изменений");
        assertEquals(task1, historyManager.getHistory().get(1), "При добавлении задачи в историю," +
                " она должна добавляться в конец списка");
    }

    @Test
    void shouldRemoveTaskInHistoryFromStart() {
        historyManager.addTaskToHistory(task3);

        assertEquals(task3, historyManager.getHistory().get(2),
                "При добавлении задачи в историю, она должна добавляться в конец списка");

        historyManager.removeTaskInHistory(task3.getId());

        assertEquals(task2, historyManager.getHistory().get(1),
                "При удалении задачи из списка," +
                        " в конец истории должен смещаться последний добавленный элемент");
    }

    @Test
    void shouldRemoveTaskInHistoryFromMiddle() {
        historyManager.addTaskToHistory(task3);
        final List<Task> currentHistory = historyManager.getHistory();

        assertEquals(task1, historyManager.getHistory().get(0),
                "При добавлении задачи в конец списка, первый элемент должен оставаться без изменений");

        historyManager.removeTaskInHistory(task2.getId());

        assertEquals(task3, historyManager.getHistory().get(1),
                "При удалении задачи из середины списка, следующий за ним элемент должен вставать на его место");
        assertEquals(task1, historyManager.getHistory().get(0),
                "При удалении элемента из середины списка, предыдущий элемент должен оставаться без изменений");
    }

    @Test
    void shouldRemoveTaskInHistoryFromEnd() {
        historyManager.addTaskToHistory(task3);

        assertEquals(task3, historyManager.getHistory().get(2),
                "Последняя добавленная задача должна быть в конце списка истории");

        historyManager.removeTaskInHistory(task3.getId());

        assertEquals(task2, historyManager.getHistory().get(1),
                "После удаления последней вызванной задачи," +
                        " задача вызванная перед последней должна занять последнее место");
    }

    @Test
    void shouldRemoveOnlyExistingTasksFromHistory() { // новые тесты ↓
        assertEquals(2, historyManager.getHistory().size(),
                "Размер истории должен соответствовать количеству добавленных задач");

        historyManager.removeTaskInHistory(task3.getId());
        historyManager.removeTaskInHistory(15);

        assertEquals(2, historyManager.getHistory().size(),
                "При попытки удаления не существующей задачи в истории," +
                        " количество ее элементов должно оставаться без изменений");
        assertEquals(task1, historyManager.getHistory().get(0), "При попытке удаления не существующей задачи" +
                "в истории, порядок ее элементов должен оставаться неизменным");
    }

    @Test
    void shouldCorrectHandleWithAddingNullTasks() {
        historyManager.addTaskToHistory(task4);

        assertEquals(2, historyManager.getHistory().size(),
                "При добавлении не существующей задачи, размер истории должен оставаться без изменений");

    }

    @Test
    void shouldCorrectHandleWithRemoveFromEmptyHistory() {
        historyManager.removeTaskInHistory(task1.getId());
        historyManager.removeTaskInHistory(task2.getId());
        final List<Task> currentHistory = historyManager.getHistory();

        assertTrue(currentHistory.isEmpty(), "Должна быть возвращена пустая история");

        historyManager.removeTaskInHistory(task1.getId());
        historyManager.removeTaskInHistory(15);

        assertEquals(currentHistory, historyManager.getHistory(), "При попытки удаления значений" +
                " из пустой истории, она должна оставаться без изменений");
    }
}