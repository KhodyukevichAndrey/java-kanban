package com.yandex.taskmanager;

import com.yandex.taskmanagerapp.service.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import com.yandex.taskmanagerapp.model.Epic;
import com.yandex.taskmanagerapp.model.Statuses;
import com.yandex.taskmanagerapp.model.Subtask;
import com.yandex.taskmanagerapp.model.Task;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.Month;

public class InMemoryTaskManagerTest extends AbstractTaskManagerTest {

    @BeforeEach
    void testEnvironment() {
        taskManager = new InMemoryTaskManager();

        assertTrue(taskManager.getAllTask().isEmpty(),
                "Список задач должен быть пустым после инициализации");
        assertTrue(taskManager.getAllSubtask().isEmpty(),
                "Список подзадач должен быть пустым после инициализации");
        assertTrue(taskManager.getAllEpics().isEmpty(),
                "Список Эпиков должен быть пустым после инициализации");

        // Создание задач для тестирования
        task1 = new Task("name1", "description1", Statuses.NEW,
                45, LocalDateTime.of(2023, Month.JANUARY, 7, 7, 0));
        task2 = new Task("name2", "description2", Statuses.IN_PROGRESS,
                45, LocalDateTime.of(2023, Month.APRIL, 7, 7, 30));
        task3 = new Task("name3", "description3", Statuses.DONE);
        task4 = new Task("name4", "description4", Statuses.DONE);
        epic1 = new Epic("name1", "description1");
        epic2 = new Epic("name2", "description2");
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
}
