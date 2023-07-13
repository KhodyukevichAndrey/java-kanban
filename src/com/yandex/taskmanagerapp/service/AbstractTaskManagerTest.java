package com.yandex.taskmanagerapp.service;

import com.yandex.taskmanagerapp.model.Epic;
import com.yandex.taskmanagerapp.model.Statuses;
import com.yandex.taskmanagerapp.model.Subtask;
import com.yandex.taskmanagerapp.model.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class AbstractTaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task task1;
    protected Task task2;
    protected Task task3;
    protected Task task4;
    protected Epic epic1;
    protected Epic epic2;

    @Test
    void shouldReturnListWithAllTask() {
        final List<Task> emptyTasksList = taskManager.getAllTask();

        assertNotNull(emptyTasksList, "Список не пустой");
        assertTrue(emptyTasksList.isEmpty(), "Список не пустой");

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        final List<Task> listWithTasks = taskManager.getAllTask();

        assertNotNull(listWithTasks, "Список не пустой");
        assertEquals(2, listWithTasks.size(), "Количество элементов в списке отличается");
        assertEquals(task1, taskManager.getTaskById(task1.getId()), "Задачи не совпадают");
    }

    @Test
    void shouldDeleteAllTasks() {
        List<Task> tasks = taskManager.getAllTask();

        assertNotNull(tasks, "Задача не найдена.");
        assertTrue(tasks.isEmpty(), "Список не пустой");

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        tasks = taskManager.getAllTask();

        assertNotNull(tasks);
        assertEquals(2, tasks.size(), "Количество элементов в списке отличается");
        assertEquals(task1, taskManager.getTaskById(task1.getId()), "Задачи не совпадают");

        taskManager.deleteAllTask();
        tasks = taskManager.getAllTask();

        assertNotNull(tasks, "Задача не найдена.");
        assertTrue(tasks.isEmpty(), "Список не пустой");
    }

    @Test
    void shouldReturnTaskById() {

        taskManager.addNewTask(task1);

        final Task testTask = taskManager.getTaskById(task1.getId());

        assertNotNull(testTask, "Задача не найдена.");
        assertEquals(task1, testTask, "Задачи не совпадают");
    }

    @Test
    void shouldAddNewTask() {
        taskManager.addNewTask(task1);

        final Task testTask = taskManager.getTaskById(task1.getId());

        assertNotNull(testTask, "Задача не найдена.");
        assertEquals(task1, testTask, "Задачи не равны");

        final List<Task> tasks = taskManager.getAllTask();

        assertNotNull(tasks, "Задача не найдена.");
        assertEquals(1, tasks.size(), "Неверное количество задач");
        assertEquals(task1, testTask, "Задачи не совпадают.");
    }

    @Test
    void shouldUpdateTask() {
        taskManager.addNewTask(task1);
        int id = task1.getId();
        final Task taskBeforeChange = taskManager.getTaskById(id);

        assertNotNull(taskBeforeChange, "Задача не найдена.");
        assertEquals(task1, taskBeforeChange, "Задачи не совпадают.");

        task1.setStatus(Statuses.DONE);
        taskManager.updateTask(task1);
        final Task taskAfterChangeStatus = taskManager.getTaskById(id);

        assertNotNull(taskAfterChangeStatus, "Задача не найдена.");
        assertEquals(task1, taskAfterChangeStatus, "Задачи не совпадают.");
    }

    @Test
    void shouldDeleteTaskById() {
        taskManager.addNewTask(task1);
        int id = task1.getId();

        final Task testTask = taskManager.getTaskById(id);

        assertNotNull(testTask, "Задача не найдена.");
        assertEquals(task1, testTask, "Задачи не совпадают.");

        taskManager.deleteTaskById(id);

        final Task taskAfterDelete = taskManager.getTaskById(id);

        assertNull(taskAfterDelete, "Задача не удалена");
    }

    @Test
    void shouldReturnAllSubtasks() {
        final List<Subtask> emptySubtasksList = taskManager.getAllSubtask();

        assertNotNull(emptySubtasksList, "Список не найден");
        assertTrue(emptySubtasksList.isEmpty(), "Список не пустой");

        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "description1", Statuses.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask2", "description2", Statuses.IN_PROGRESS, epic1.getId());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        final List<Subtask> listWithSubtasks = taskManager.getAllSubtask();

        assertNotNull(listWithSubtasks, "Список не найден");
        assertEquals(2, listWithSubtasks.size(), "Неверное количество задач.");
        assertEquals(subtask1, taskManager.getSubtaskById(subtask1.getId()), "Задачи не совпадают");
    }

    @Test
    void shouldDeleteAllSubtask() {
        List<Subtask> subtasks = taskManager.getAllSubtask();
        assertEquals(0, subtasks.size());

        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "description1", Statuses.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask2", "description2", Statuses.IN_PROGRESS, epic1.getId());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        subtasks = taskManager.getAllSubtask();

        assertNotNull(subtasks, "Задача не найдена.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask1, taskManager.getSubtaskById(subtask1.getId()), "Задачи не совпадают");

        taskManager.deleteAllSubtask();
        subtasks = taskManager.getAllSubtask();

        assertNotNull(subtasks, "Задача не найдена.");
        assertTrue(subtasks.isEmpty(), "Список не пустой");
    }

    @Test
    void shouldReturnSubtaskById() {

        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "description1", Statuses.NEW, epic1.getId());
        taskManager.addNewSubtask(subtask1);

        final Subtask testSubtask = taskManager.getSubtaskById(subtask1.getId());

        assertNotNull(testSubtask, "Задача не найдена.");
        assertEquals(subtask1, testSubtask, "Задачи не совпадают.");
    }

    @Test
    void shouldAddNewSubtaskById() {
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "description1", Statuses.NEW, epic1.getId());
        taskManager.addNewSubtask(subtask1);

        Subtask testSubtask = taskManager.getSubtaskById(subtask1.getId());

        assertNotNull(testSubtask, "Задача не найдена.");
        assertEquals(subtask1, testSubtask, "Задачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtask();

        assertNotNull(subtasks);
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask1, subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void shouldUpdateSubtask() {
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "description1", Statuses.NEW, epic1.getId());
        taskManager.addNewSubtask(subtask1);
        int id = subtask1.getId();

        final Subtask testSubtask = taskManager.getSubtaskById(id);

        assertNotNull(testSubtask, "Задача не найдена.");
        assertEquals(subtask1, testSubtask, "Задачи не совпадают.");

        subtask1.setStatus(Statuses.DONE);
        taskManager.updateSubtask(subtask1);

        final Subtask subtaskAfterUpdate = taskManager.getSubtaskById(id);

        assertNotNull(subtaskAfterUpdate, "Задача не найдена.");
        assertEquals(subtask1, subtaskAfterUpdate, "Задачи не совпадают.");
    }

    @Test
    void shouldDeleteSubtaskById() {
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "description1", Statuses.NEW, epic1.getId());
        taskManager.addNewSubtask(subtask1);
        int id = subtask1.getId();

        final Subtask testSubtask = taskManager.getSubtaskById(id);

        assertNotNull(testSubtask, "Задача не найдена.");
        assertEquals(subtask1, testSubtask, "Задачи не совпадают.");

        taskManager.deleteSubtaskById(id);

        final Subtask subtaskAfterDelete = taskManager.getSubtaskById(id);

        assertNull(subtaskAfterDelete, "Задача не удалена");
    }

    @Test
    void shouldReturnAllEpics() {
        final List<Epic> emptyEpicsList = taskManager.getAllEpics();

        assertNotNull(emptyEpicsList, "Задача не найдена.");
        assertTrue(emptyEpicsList.isEmpty(), "Список не пустой");

        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        final List<Epic> listWithEpics = taskManager.getAllEpics();

        assertNotNull(listWithEpics, "Список не найден");
        assertEquals(2, listWithEpics.size(), "Неверное количество задач.");
    }

    @Test
    void shouldDeleteAllEpics() {
        final List<Epic> epics = taskManager.getAllEpics();

        assertTrue(epics.isEmpty(), "Список не пустой");

        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        final List<Epic> listWithEpics = taskManager.getAllEpics();

        assertNotNull(listWithEpics, "Список не найден");
        assertEquals(2, listWithEpics.size(), "Неверное количество задач.");

        taskManager.deleteAllEpics();

        final List<Epic> listAfterDeleteEpics = taskManager.getAllEpics();

        assertNotNull(listAfterDeleteEpics, "Список не найден");
        assertTrue(listAfterDeleteEpics.isEmpty(), "Список не пустой");
    }

    @Test
    void shouldReturnEpicById() {
        taskManager.addNewEpic(epic1);
        final Epic testEpic = taskManager.getEpicById(epic1.getId());

        assertNotNull(testEpic, "Задача не найдена.");
        assertEquals(epic1, testEpic, "Задачи не совпадают.");
    }

    @Test
    void shouldAddNewEpic() {
        taskManager.addNewEpic(epic1);
        final Epic testEpic = taskManager.getEpicById(epic1.getId());

        assertNotNull(testEpic, "Задача не найдена.");
        assertEquals(epic1, testEpic, "Задачи не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Задача не найдена.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epics.get(0), testEpic, "Задачи не совпадают.");
    }

    @Test
    void shouldUpdateEpic() {
        taskManager.addNewEpic(epic1);
        int id = epic1.getId();
        final Epic epicBeforeChange = taskManager.getEpicById(id);

        assertNotNull(epicBeforeChange, "Задача не найдена.");
        assertEquals(epic1, epicBeforeChange, "Задачи не совпадают.");

        epicBeforeChange.setStatus(Statuses.DONE);
        taskManager.updateEpic(epic1);

        final Epic epicAfterChange = taskManager.getEpicById(id);

        assertNotNull(epicAfterChange, "Задача не найдена.");
        assertEquals(epic1, epicAfterChange, "Задачи не совпадают.");
    }

    @Test
    void shouldDeleteEpicById() {
        final Epic epic = taskManager.getEpicById(0);

        assertNull(epic);

        taskManager.addNewEpic(epic1);
        int id = epic1.getId();
        final Epic testEpic = taskManager.getEpicById(id);

        assertNotNull(testEpic, "Задача не найдена.");
        assertEquals(epic1, testEpic,"Задачи не совпадают.");

        taskManager.deleteEpicById(id);

        final Epic epicAfterDelete = taskManager.getEpicById(id);

        assertNull(epicAfterDelete, "Задача не удалена");
    }

    @Test
    void shouldReturnListWithEpicsSubtasks() {
        taskManager.addNewEpic(epic1);
        int id = epic1.getId();
        Subtask subtask1 = new Subtask("Subtask1", "description1", Statuses.NEW, id);
        Subtask subtask2 = new Subtask("Subtask2", "description2", Statuses.NEW, id);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        Epic testEpic = taskManager.getEpicById(id);

        assertNotNull(testEpic, "Задача не найдена.");
        assertEquals(epic1, testEpic,  "Задачи не совпадают.");

        List<Subtask> epicsSubtasks = new ArrayList<>();
        epicsSubtasks.add(subtask1);
        epicsSubtasks.add(subtask2);

        List<Subtask> epicsSubtasksFromManager = taskManager.getEpicsSubtasks(id);

        assertNotNull(epicsSubtasks, "Список не найден");
        assertEquals(epicsSubtasks, epicsSubtasksFromManager, "Списки не соответствуют");
        assertEquals(epicsSubtasks.get(0), epicsSubtasksFromManager.get(0), "Задачи не совпадают.");
    }

    @Test
    void shouldReturnHistory() {
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);

        List<Task> emptyHistory = taskManager.getHistory();

        assertNotNull(emptyHistory, "История не найдена");
        assertTrue(emptyHistory.isEmpty(), "История не пуста");

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        List<Task> listForTestHistory = new ArrayList<>();
        listForTestHistory.add(task1);
        listForTestHistory.add(task2);

        assertNotNull(taskManager.getHistory(),"История не найдена");
        assertEquals(listForTestHistory, taskManager.getHistory(), "Истории не совпадают");
        assertEquals(2, taskManager.getHistory().size(), "Неверное количество задач в истории");
    }

    @Test
    void shouldReturnStartOfTask() {
        taskManager.addNewTask(task1);
        Task task = taskManager.getTaskById(task1.getId());

        assertNotNull(task, "Задача не найдена.");
        assertEquals(task1, task,  "Задачи не совпадают.");
        assertEquals(task1.getStartTime(), task.getStartTime(),  "Задачи должны быть начаты в одно время");
    }

    @Test
    void shouldCalculateEndOfTask() {
        taskManager.addNewTask(task1);
        Task task = taskManager.getTaskById(task1.getId());

        assertNotNull(task, "Задача не найдена.");
        assertEquals(task1, task,  "Задачи не совпадают.");
        assertEquals(task1.getEndTime(), task.getEndTime(), "Расчет окончания выполнения задач некорректен");
    }

    @Test
    void shouldReturnDurationOfTask() {
        taskManager.addNewTask(task1);
        Task task = taskManager.getTaskById(task1.getId());

        assertNotNull(task, "Задача не найдена.");
        assertEquals(task1, task,  "Задачи не совпадают.");
        assertEquals(task1.getDuration(), task.getDuration(), "Длительность выполнения не совпадает");
    }

    @Test
    void shouldCalculateEndTimeOfEpic() {
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask1", "description1", Statuses.NEW,
                45, LocalDateTime.of(2023, Month.JUNE, 7, 7, 30), epic1.getId());
        Subtask subtask2 = new Subtask("Subtask2", "description2", Statuses.NEW,
                120, LocalDateTime.of(2023, Month.OCTOBER, 8, 8, 30), epic1.getId());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertNotNull(epic1.getEndTime(), "Время старта задачи не указано");
        assertTrue(subtask1.getStartTime().isBefore(subtask2.getStartTime()), "Задача 1 должна быть раньше");
        assertEquals(subtask2.getEndTime(), epic1.getEndTime(), "Расчет окончания эпика неверен");
        assertEquals(subtask1.getStartTime(), epic1.getStartTime(), "Расчет начала эпика неверен");
    }
}
