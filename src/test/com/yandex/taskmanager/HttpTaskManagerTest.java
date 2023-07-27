package com.yandex.taskmanager;

import com.yandex.taskmanagerapp.model.Epic;
import com.yandex.taskmanagerapp.model.Statuses;
import com.yandex.taskmanagerapp.model.Subtask;
import com.yandex.taskmanagerapp.model.Task;
import com.yandex.taskmanagerapp.server.HttpTaskServer;
import com.yandex.taskmanagerapp.server.KVServer;
import com.yandex.taskmanagerapp.service.HttpTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTest extends AbstractTaskManagerTest {

    KVServer kvServer;
    HttpTaskServer httpTaskServer;

    @BeforeEach
    void testEnvironment() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            taskManager = new HttpTaskManager(URI.create("http://localhost:8078"));
            httpTaskServer = new HttpTaskServer(taskManager);
            httpTaskServer.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        task1 = new Task("name1", "description1", Statuses.NEW,
                45, LocalDateTime.of(2023, Month.JANUARY, 7, 7, 0));
        task2 = new Task("name2", "description2", Statuses.IN_PROGRESS,
                45, LocalDateTime.of(2023, Month.APRIL, 7, 7, 30));
        task3 = new Task("name3", "description3", Statuses.DONE);
        task4 = new Task("name4", "description4", Statuses.DONE);
        epic1 = new Epic("name1", "description1");
        epic2 = new Epic("name2", "description2");

        assertTrue(taskManager.getAllTask().isEmpty(),
                "Список задач должен быть пустым после инициализации");
        assertTrue(taskManager.getAllSubtask().isEmpty(),
                "Список подзадач должен быть пустым после инициализации");
        assertTrue(taskManager.getAllEpics().isEmpty(),
                "Список Эпиков должен быть пустым после инициализации");

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        subtask1 = new Subtask("Subtask1", "description1", Statuses.NEW,
                45, LocalDateTime.of(2023, Month.JUNE, 7, 7, 30), epic1.getId());
        subtask2 = new Subtask("Subtask2", "description2", Statuses.NEW,
                120, LocalDateTime.of(2023, Month.OCTOBER, 8, 8, 30), epic1.getId());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
    }

    @AfterEach
    void serverStop() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    void shouldLoadFromServer() {
        URI url = URI.create("http://localhost:8078");
        HttpTaskManager httpTaskManager = new HttpTaskManager(url, true);

        assertEquals(taskManager.getAllTask(), httpTaskManager.getAllTask(),
                "Список задач после выгрузки не совпададает");
        assertEquals(taskManager.getAllSubtask(), httpTaskManager.getAllSubtask(),
                "Список подзадач после выгрузки не совпададает");
        assertEquals(taskManager.getAllEpics(), httpTaskManager.getAllEpics(),
                "Список эпиков после выгрузки не совпададает");
        assertEquals(taskManager.getHistory(), httpTaskManager.getHistory(),
                "Список истории после выгрузки не совпададает");
        assertEquals(taskManager.getEpicsSubtasks(4), httpTaskManager.getEpicsSubtasks(4),
                "Список подзадач Эпика после выгрузки не совпададает");
        assertEquals(List.copyOf(taskManager.getPriorityTasks()), List.copyOf(httpTaskManager.getPriorityTasks()),
                "Отсортированный список задач после выгрузки не совпададает");
    }
}
