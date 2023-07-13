package com.yandex.taskmanagerapp.service;

import com.yandex.taskmanagerapp.model.Epic;
import com.yandex.taskmanagerapp.model.Statuses;
import com.yandex.taskmanagerapp.model.Subtask;
import com.yandex.taskmanagerapp.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

public class FiledBackedTaskManagerTest extends AbstractTaskManagerTest {

    File file;

    @BeforeEach
    void testEnvironment() {
        file = new File("tasksFile.csv");
        taskManager = new FileBackedTasksManager(file);
        task1 = new Task("name1", "description1", Statuses.NEW,
                45, LocalDateTime.of(2023, Month.APRIL, 4, 4, 30));
        task2 = new Task("name2", "description2", Statuses.IN_PROGRESS,
                45, LocalDateTime.of(2023, Month.JULY, 7, 7, 30));
        task3 = new Task("name3", "description3", Statuses.DONE,
                45, LocalDateTime.of(2023, Month.OCTOBER, 10, 10, 30));
        epic1 = new Epic("name1", "description1");
        epic2 = new Epic("name2", "description2");
    }

    @Test
    void shouldSaveAndLoadTasksWhenEmpty() {

        taskManager = Managers.loadFileBackedTasksManager(file);
        TaskManager loadFromFile = Managers.loadFileBackedTasksManager(file);

        assertNotNull(loadFromFile);
        assertEquals(taskManager.getAllTask(), loadFromFile.getAllTask());
        assertEquals(taskManager.getAllSubtask(), loadFromFile.getAllSubtask());
        assertEquals(taskManager.getAllEpics(), loadFromFile.getAllEpics());
        assertEquals(taskManager.getHistory(), loadFromFile.getHistory());
    }

    @Test
    void shouldSaveAndLoadWhenEpicsWithoutSubtasks() {
        taskManager = Managers.loadFileBackedTasksManager(file);
        TaskManager loadFromFile = Managers.loadFileBackedTasksManager(file);

        assertNotNull(taskManager);
        assertNotNull(loadFromFile);
        assertEquals(taskManager.getAllEpics(), loadFromFile.getAllEpics());
        assertEquals(taskManager.getHistory(), loadFromFile.getHistory());

        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        taskManager.getEpicById(epic1.getId());
        loadFromFile = Managers.loadFileBackedTasksManager(file);

        assertNotNull(taskManager);
        assertNotNull(loadFromFile);
        assertEquals(taskManager.getAllEpics(), loadFromFile.getAllEpics());
        assertEquals(taskManager.getHistory(), loadFromFile.getHistory());
    }

    @Test
    void shouldSaveAndLoadWhenHistoryIsEmpty() {
        taskManager = Managers.loadFileBackedTasksManager(file);
        TaskManager loadFromFile = Managers.loadFileBackedTasksManager(file);

        assertNotNull(taskManager);
        assertNotNull(loadFromFile);
        assertEquals(taskManager.getAllEpics(), loadFromFile.getAllEpics());
        assertEquals(taskManager.getHistory(), loadFromFile.getHistory());

        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        loadFromFile = Managers.loadFileBackedTasksManager(file);

        assertNotNull(taskManager);
        assertNotNull(loadFromFile);
        assertEquals(taskManager.getAllEpics(), loadFromFile.getAllEpics());
        assertEquals(taskManager.getHistory(), loadFromFile.getHistory());

    }

    @Test
    void shouldSaveAndLoadTasks() {
        taskManager = Managers.loadFileBackedTasksManager(file);
        TaskManager loadFromFile = Managers.loadFileBackedTasksManager(file);

        assertNotNull(taskManager);
        assertNotNull(loadFromFile);
        assertEquals(taskManager.getAllEpics(), loadFromFile.getAllEpics());
        assertEquals(taskManager.getHistory(), loadFromFile.getHistory());

        taskManager.addNewTask(task3);
        taskManager.addNewEpic(epic1);
        taskManager.getTaskById(task3.getId());
        Subtask subtask1 = new Subtask("Subtask1", "description1", Statuses.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask2", "description2", Statuses.NEW, epic1.getId());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertNotNull(loadFromFile);
        loadFromFile = Managers.loadFileBackedTasksManager(file);

        assertNotNull(taskManager);
        assertNotNull(loadFromFile);
        assertEquals(taskManager.getAllTask(), loadFromFile.getAllTask());
        assertEquals(taskManager.getAllSubtask(), loadFromFile.getAllSubtask());
        assertEquals(taskManager.getAllEpics(), loadFromFile.getAllEpics());
        assertEquals(taskManager.getHistory(), loadFromFile.getHistory());
    }

    @Test
    void shouldPhoto() {
    }
}
