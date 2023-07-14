package testapi.com.yandex.taskmanager;

import com.yandex.taskmanagerapp.model.*;
import com.yandex.taskmanagerapp.service.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

abstract class AbstractTaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task task1;
    protected Task task2;
    protected Task task3;
    protected Task task4;
    protected Subtask subtask1;
    protected Subtask subtask2;
    protected Epic epic1;
    protected Epic epic2;

    @Test
    void shouldReturnAllTasks() {
        final List<Task> tasks = taskManager.getAllTask();

        assertNotNull(tasks, "После инициализации должен быть возвращен список");
        assertEquals(3, tasks.size(),
                "Количество элементов в списке должно соответствовать кол-ву созданных задач");
    }

    @Test
    void shouldDeleteAllTasks() {
        assertFalse(taskManager.getAllTask().isEmpty(), "Список должен содержать созданные задачи");

        taskManager.deleteAllTask();
        List<Task> tasks = taskManager.getAllTask();

        assertTrue(tasks.isEmpty(),
                "После удаления всех задач количество элементов в списке должно быть равно 0");
    }

    @Test
    void shouldReturnTaskById() {
        final Task testTask = taskManager.getTaskById(task1.getId());

        assertNotNull(testTask, "Должна быть возвращена задача");
        assertEquals(task1, testTask, "Задачи должны быть эквивалентны после добавления");
    }

    @Test
    void shouldAddNewTask() {
        taskManager.addNewTask(task4);
        final Task testTask = taskManager.getTaskById(task4.getId());
        final List<Task> tasks = taskManager.getAllTask();

        assertNotNull(testTask, "Должна быть возвращена задача");
        assertEquals(4, tasks.size(),
                "После добавления задачи размер списка должен быть увеличен на 1");
        assertEquals(task4, testTask, "Задачи должны быть эквивалентны после добавления.");
    }

    @Test
    void shouldUpdateTask() {
        task1.setStatus(Statuses.DONE);
        taskManager.updateTask(task1);

        final Task taskAfterChangeStatus = taskManager.getTaskById(task1.getId());

        assertNotNull(taskAfterChangeStatus, "Должна быть возвращена задача");
        assertEquals(task1, taskAfterChangeStatus,
                "После обновления задачи, список должен возвращать её новую версию");
        assertEquals(3, taskManager.getAllTask().size(),
                "После обновления задачи, размер списка должен оставаться неизменным");
    }

    @Test
    void shouldDeleteTaskById() {
        assertTrue(taskManager.getAllTask().contains(task1), "В списке должна находится созданная задача");

        taskManager.deleteTaskById(task1.getId());
        final Task taskAfterDelete = taskManager.getTaskById(task1.getId());

        assertNull(taskAfterDelete, "После удаления задачи Manager не должен её возвращать по ID");
        assertEquals(2, taskManager.getAllTask().size(),
                "После удаления задачи размер списка должен быть уменьшен на 1");
    }

    @Test
    void shouldReturnAllSubtasks() {
        final List<Subtask> subtasks = taskManager.getAllSubtask();

        assertNotNull(subtasks, "Должен быть возвращен список");
        assertEquals(2, subtasks.size(),
                "Количество элементов в списке должно соответствовать кол-ву созданных задач");
    }

    @Test
    void shouldDeleteAllSubtask() {
        assertFalse(taskManager.getAllSubtask().isEmpty(), "Список должен содержать созданные задачи");

        taskManager.deleteAllSubtask();
        final List<Subtask> subtasks = taskManager.getAllSubtask();

        assertTrue(subtasks.isEmpty(),
                "После удаления всех подзадач количество элементов в списке должно быть равно 0");
    }

    @Test
    void shouldReturnSubtaskById() {
        final Subtask testSubtask = taskManager.getSubtaskById(subtask1.getId());

        assertNotNull(testSubtask, "Должна быть возвращена задача");
        assertEquals(subtask1, testSubtask, "Задачи должны быть эквивалентны после получения из списка");
    }

    @Test
    void shouldAddNewSubtaskById() {
        Subtask subtask3 = new Subtask("Subtask3", "description3", Statuses.NEW, epic2.getId());
        taskManager.addNewSubtask(subtask3);

        Subtask testSubtask = taskManager.getSubtaskById(subtask3.getId());

        assertNotNull(testSubtask, "Должна быть возвращена задача");
        assertEquals(subtask3, testSubtask, "Задачи должны быть эквивалентны после добавления.");
        assertEquals(3, taskManager.getAllSubtask().size(),
                "После добавления подзадачи размер списка должен быть увеличен на 1");
    }

    @Test
    void shouldUpdateSubtask() {
        subtask1.setStatus(Statuses.DONE);
        taskManager.updateSubtask(subtask1);

        final Subtask subtaskAfterUpdate = taskManager.getSubtaskById(subtask1.getId());

        assertNotNull(subtaskAfterUpdate, "Должна быть возвращена задача");
        assertEquals(subtask1, subtaskAfterUpdate,
                "После обновления задачи, список должен возвращать её новую версию");
        assertEquals(2, taskManager.getAllSubtask().size(),
                "После обновления задачи, размер списка должен оставаться неизменным");
    }

    @Test
    void shouldDeleteSubtaskById() {
        assertTrue(taskManager.getAllSubtask().contains(subtask1),
                "В списке должна находится созданная подзадача");

        taskManager.deleteSubtaskById(subtask1.getId());

        final Subtask subtaskAfterDelete = taskManager.getSubtaskById(subtask1.getId());

        assertNull(subtaskAfterDelete, "После удаления задачи Manager не должен её возвращать по ID");
        assertEquals(1, taskManager.getAllSubtask().size(),
                "После удаления задачи размер списка должен быть уменьшен на 1");
    }

    @Test
    void shouldReturnAllEpics() {
        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Должен быть возвращен список");
        assertEquals(2, epics.size(),
                "Количество элементов в списке должно соответствовать кол-ву созданных задач");
    }

    @Test
    void shouldDeleteAllEpics() {
        assertEquals(2, taskManager.getAllEpics().size(), "Список должен содержать созданные эпики");

        taskManager.deleteAllEpics();

        final List<Epic> epicsAfterDelete = taskManager.getAllEpics();
        final List<Subtask> subtasksAfterDeleteEpics = taskManager.getAllSubtask();

        assertNotNull(epicsAfterDelete, "Должен быть возвращен список");
        assertTrue(epicsAfterDelete.isEmpty(),
                "После удаления всех эпиков количество элементов в списке должно быть равно 0");
        assertTrue(subtasksAfterDeleteEpics.isEmpty(),
                "После удаления всех эпиков, все подзадачи так же должны быть удалены");
    }

    @Test
    void shouldReturnEpicById() {
        final Epic testEpic = taskManager.getEpicById(epic1.getId());

        assertNotNull(testEpic, "Должна быть возвращена задача");
        assertEquals(epic1, testEpic, "Задачи должны быть эквивалентны после добавления.");
    }

    @Test
    void shouldAddNewEpic() {
        Epic epic3 = new Epic("epic3", "description3");
        taskManager.addNewEpic(epic3);

        final Epic testEpic = taskManager.getEpicById(epic3.getId());
        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(testEpic, "Должна быть возвращена задача");
        assertEquals(epic3, testEpic, "Задачи должны быть эквивалентны после добавления.");
        assertEquals(3, epics.size(),
                "После добавления эпика размер списка должен быть увеличен на 1");
    }

    @Test
    void shouldUpdateEpic() {

        epic1.setName("newName");
        taskManager.updateEpic(epic1);

        final Epic epicAfterChange = taskManager.getEpicById(epic1.getId());

        assertNotNull(epicAfterChange, "Должна быть возвращена задача");
        assertEquals(epic1, epicAfterChange, "Задачи должны быть эквивалентны после добавления.");
        assertEquals(2, taskManager.getAllEpics().size(),
                "После обновления эпика, размер списка должен оставаться неизменным");
    }

    @Test
    void shouldDeleteEpicById() {
        assertTrue(taskManager.getAllEpics().contains(epic1), "Список должен содержать созданный Эпик");

        taskManager.deleteEpicById(epic1.getId());
        final Epic epicAfterDelete = taskManager.getEpicById(epic1.getId());

        assertNull(epicAfterDelete, "После удаления задачи Manager не должен её возвращать по ID");
        assertEquals(1, taskManager.getAllEpics().size(),
                "После удаления эпика размер списка должен быть уменьшен на 1");
    }

    @Test
    void shouldReturnListWithEpicsSubtasks() {
        Epic testEpic = taskManager.getEpicById(epic1.getId());
        List<Subtask> epicsSubtasks = taskManager.getEpicsSubtasks(testEpic.getId());
        List<Subtask> epicsSubtasksFromListWithIdInEpic = testEpic.getEpicsSubtasksId().stream()
                        .map(integer -> taskManager.getSubtaskById(integer))
                                .collect(Collectors.toList());

        assertNotNull(epicsSubtasks, "Должен быть возвращен список");
        assertNotNull(epicsSubtasksFromListWithIdInEpic);
        assertEquals(epic1.getEpicsSubtasksId().size(), epicsSubtasks.size(),
                "Количество элементов в списках должно совпадать");
        assertEquals(epicsSubtasks, epicsSubtasksFromListWithIdInEpic,
                "Списки подзадач должны быть эквивалентны, " +
                        "т.к. заполняются одновременно на основне одних данных при создании/обновлении подзадач");
    }

    @Test
    void shouldReturnHistory() {
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        assertNotNull(taskManager.getHistory(),"Должен быть возвращен список");
        assertEquals(2, taskManager.getHistory().size(),
                "Количество элементов в списке должно соответствовать кол-ву вызванных задач");
        assertTrue(taskManager.getHistory().contains(task1),
                "Возвращаемый список должен содержать задачу, которая была вызвана в программе ранее");
    }

    @Test
    void shouldReturnStartOfTask() {
        LocalDateTime startTimeOfTask1 = task1.getStartTime();
        LocalDateTime expectedStartTime = LocalDateTime.of(2023, Month.JANUARY, 7, 7, 0);

        assertNotNull(expectedStartTime, "Должно быть получено время старта к задаче");
        assertEquals(expectedStartTime, startTimeOfTask1,
                "Время в которое приступают к задаче должно быть равно");
    }

    @Test
    void shouldCalculateEndOfTask() {
        LocalDateTime endTimeOfTask1 = task1.getEndTime();
        LocalDateTime expectedEndTime = LocalDateTime.of(2023, Month.JANUARY, 7, 7, 45);

        assertNotNull(expectedEndTime, "Должно быть получено время окончания выполнения задачи");
        assertEquals(expectedEndTime, endTimeOfTask1, "Рассчет окончания времени выполнения задачи неверен");
    }

    @Test
    void shouldReturnDurationOfTask() {
        int duration = task1.getDuration();
        int expectedValueOfDuration = 45;

        assertEquals(duration, expectedValueOfDuration,
                "Возвращаемая продолжительность выполнения задачи не соответствует");
    }

    @Test
    void shouldCalculateEndTimeOfEpic() {
        LocalDateTime epicEndTime = epic1.getEndTime();
        LocalDateTime expectedEndTime = LocalDateTime.of(2023, Month.OCTOBER, 8, 10, 30);


        assertNotNull(epicEndTime, "Должно быть получено рассчитаное время окончания выполнения эпика");
        assertEquals(subtask1.getStartTime(), epic1.getStartTime(),
                "Время старта эпика должно соответствовать времени старта самой ранней подзадачи");
        assertEquals(subtask2.getEndTime(), expectedEndTime,
                "Время окончания выполнения Эпика должно соответствовать" +
                        " времени окончания самой поздней подзадачи");
    }

    @Test
    void shouldReturnPrioritySet() {
        TreeSet<Task> prioritizedSet = taskManager.getPrioritySet();

        assertNotNull(prioritizedSet, "Должно быть возвращено множество");
        assertEquals(task1, taskManager.getPrioritySet().first(),
                "Должна быть возвращена задача, с самой раней датой старта");
    }
}
