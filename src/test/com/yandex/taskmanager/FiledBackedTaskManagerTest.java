package com.yandex.taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import com.yandex.taskmanagerapp.model.Epic;
import com.yandex.taskmanagerapp.model.Statuses;
import com.yandex.taskmanagerapp.model.Subtask;
import com.yandex.taskmanagerapp.model.Task;
import com.yandex.taskmanagerapp.service.FileBackedTasksManager;
import com.yandex.taskmanagerapp.service.Managers;
import com.yandex.taskmanagerapp.service.TaskManager;

import java.io.File;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

public class FiledBackedTaskManagerTest extends AbstractTaskManagerTest {

    private File fileWithDatabase;
    private final File emptyFile = new File("emptyFile");;
    private TaskManager loadFromFile;


    @BeforeEach
    void testEnvironment() {
        fileWithDatabase = new File("testFile");
        taskManager = new FileBackedTasksManager(fileWithDatabase);
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

    @Test
    void shouldRestoreTasksWhenEmpty() {
        taskManager.deleteAllTask();

        loadFromFile = Managers.loadFileBackedTasksManager(fileWithDatabase);

        assertNotNull(loadFromFile);
        assertNull(loadFromFile.getTaskById(0),
                "Задача должна быть не доступна после удаления всего списка задач");
        assertTrue(loadFromFile.getAllTask().isEmpty(),
                "При загрузке из файла список задач должен быть пустым");
    }

    @Test
    void shouldRestoreWhenEpicsWithoutSubtasks() {
        taskManager.deleteAllSubtask();

        loadFromFile = Managers.loadFileBackedTasksManager(fileWithDatabase);

        assertNotNull(loadFromFile);
        assertEquals(2, loadFromFile.getAllEpics().size(),
                "При загрузке должно восстановиться 2 эпика");
        assertTrue(epic1.getEpicsSubtasksId().isEmpty(),
                "Список Id подзадач эпика должен быть пустым");
    }

    @Test
    void shouldRestoreWhenHistoryIsEmpty() {
        loadFromFile = Managers.loadFileBackedTasksManager(fileWithDatabase);

        assertNotNull(loadFromFile);
        assertTrue(loadFromFile.getHistory().isEmpty(), "Должна восстановиться пустая история");
    }

    @Test
    void shouldRestoreTasks() {
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask1.getId());

        loadFromFile = Managers.loadFileBackedTasksManager(fileWithDatabase);

        assertNotNull(loadFromFile);
        assertEquals(3, loadFromFile.getAllTask().size(),
                "При загрузке должнО восстановиться 3 задачи");
        assertEquals(2, loadFromFile.getAllSubtask().size(),
                "При загрузке должно восстановиться 2 подзадачи");
        assertEquals(2, loadFromFile.getAllEpics().size(),
                "При загрузке должно восстановиться 2 эпика");
        assertEquals(2, loadFromFile.getHistory().size(),
                "При загрузке должна восстановиться история с 2 элементами");
    }

    @Test
    void shouldHandleWithGettingNonExistingTask() { // новые тесты ↓

        Task task = taskManager.getTaskById(15);

        assertNull(task, "При попытки запроса задачи с несуществующим идентификатором" +
                " - задачи не должны возвращаться");
        assertEquals(0, taskManager.getHistory().size(), "При попытке получения задачи с" +
                " несуществующим идентификатором, размер истории должен оставаться без изменений");
        assertEquals(3, taskManager.getAllTask().size(), "При попытке получения задачи с" +
                " несуществующим идентификатором, список задач должен оставаться без изменений");
    }

    @Test
    void shouldHandleWithGettingNonExistingSubtask() {

        Subtask subtask = taskManager.getSubtaskById(15);

        assertNull(subtask, "При попытки запроса задачи с несуществующим идентификатором" +
                " - задачи не должны возвращаться");
        assertEquals(0, taskManager.getHistory().size(), "При попытке получения задачи с" +
                " несуществующим идентификатором, размер истории должен оставаться без изменений");
        assertEquals(2, taskManager.getAllSubtask().size(), "При попытке получения задачи с" +
                " несуществующим идентификатором, список подзадач должен оставаться без изменений");
    }

    @Test
    void shouldHandleWithGettingNonExistingEpic() {

        Epic epic = taskManager.getEpicById(15);

        assertNull(epic, "При попытки запроса задачи с несуществующим идентификатором" +
                " - задачи не должны возвращаться");
        assertEquals(0, taskManager.getHistory().size(), "При попытке получения задачи с" +
                " несуществующим идентификатором, размер истории должен оставаться без изменений");
        assertEquals(2, taskManager.getAllEpics().size(), "При попытке получения задачи с" +
                " несуществующим идентификатором, список задач должен оставаться без изменений");
    }

    @Test
    void shouldThrowsNullExceptionWhenAddNonExistTask() {
        Executable exception = () -> taskManager.addNewTask(null);
        assertThrows(NullPointerException.class, exception, "При попытке добавить несуществующую " +
                "задачу должна возникать NullPointerException");
        assertEquals(3, taskManager.getAllTask().size(), "При попытке создания несуществующей задачи" +
                " список подзадач должен оставаться без изменений");
    }

    @Test
    void shouldThrowsNullExceptionWhenAddNonExistSubtask() {
        Executable exception = () -> taskManager.addNewSubtask(null);
        assertThrows(NullPointerException.class, exception, "При попытке добавить несуществующую " +
                "подзадачу должна возникать NullPointerException");
        assertEquals(2, taskManager.getAllSubtask().size(), "При попытке создания " +
                "несуществующей подзадачи список подзадач должен оставаться без изменений");
    }

    @Test
    void shouldThrowsNullExceptionWhenAddNonExistEpic() {
        Executable exception = () -> taskManager.addNewEpic(null);
        assertThrows(NullPointerException.class, exception, "При попытке добавить несуществующий " +
                "эпик должна возникать NullPointerException");
        assertEquals(2, taskManager.getAllEpics().size(), "При попытке создания несуществующего эпика" +
                " список эпиков должен оставаться без изменений");
    }

    @Test
    void shouldThrowsNullExceptionWhenGetNonExistingEpicSubtask() {
        Executable exception = () -> taskManager.getEpicsSubtasks(15);

        assertThrows(NullPointerException.class, exception, "При попытке запросить список подзадач " +
                "несуществующего эпика, должна возникать NullPointerException");
        assertEquals(2, taskManager.getAllSubtask().size(), "При попытке запросить список подзадач" +
                "несуществующего эпика - список подзадач должен оставаться без изменений");
        assertEquals(2, taskManager.getAllEpics().size(), "При попытке запросить список подзадач" +
                " несуществующего эпика - список эпиков должен оставаться без изменений");
    }
}
