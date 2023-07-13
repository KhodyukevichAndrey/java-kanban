package com.yandex.taskmanagerapp.service;

import com.yandex.taskmanagerapp.model.Epic;
import com.yandex.taskmanagerapp.model.Statuses;
import com.yandex.taskmanagerapp.model.Task;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;
import java.time.Month;

public class InMemoryTaskManagerTest extends AbstractTaskManagerTest {

    @BeforeEach
    void testEnvironment() {
        taskManager = Managers.getDefault();
        task1 = new Task("name1", "description1", Statuses.NEW,
                45, LocalDateTime.of(2023, Month.JANUARY, 7, 7, 0));
        task2 = new Task("name2", "description2", Statuses.IN_PROGRESS,
                45, LocalDateTime.of(2023, Month.APRIL, 7, 7, 30));
        task3 = new Task("name3", "description3", Statuses.DONE);
        task4 = new Task("name4", "description4", Statuses.DONE);
        epic1 = new Epic("name1", "description1");
        epic2 = new Epic("name2", "description2");
    }
}
