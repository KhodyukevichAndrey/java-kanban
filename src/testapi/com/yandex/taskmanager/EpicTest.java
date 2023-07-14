package testapi.com.yandex.taskmanager;

import com.yandex.taskmanagerapp.model.Epic;
import com.yandex.taskmanagerapp.model.Statuses;
import com.yandex.taskmanagerapp.model.Subtask;
import com.yandex.taskmanagerapp.service.InMemoryTaskManager;
import com.yandex.taskmanagerapp.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    TaskManager taskManager;
    Epic epic;
    Subtask subtask1;
    Subtask subtask2;
    Subtask subtask3;

    @BeforeEach
    public void createEpicTestEnvironment() {
        taskManager = new InMemoryTaskManager();
        epic = new Epic("epicName1", "description1");
        taskManager.addNewEpic(epic);
        subtask1 = new Subtask("Subtask1", "description1", Statuses.NEW, epic.getId());
        subtask2 = new Subtask("Subtask2", "description2", Statuses.IN_PROGRESS, epic.getId());
        subtask3 = new Subtask("Subtask3", "description3", Statuses.DONE, epic.getId());
    }

    @Test
    public void shouldReturnEmptyListOfEpic() {
        final List<Integer> epicsSubtasksIdForEpic1 = epic.getEpicsSubtasksId();

        assertEquals(new ArrayList<>(), epicsSubtasksIdForEpic1, "У эпика есть подзадачи.");
    }

    @Test
    public void shouldReturnEpicListWithSubtasks() {
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        final List<Integer> epicsSubtasksId = new ArrayList<>();
        epicsSubtasksId.add(2);
        epicsSubtasksId.add(3);

        assertEquals(epic.getEpicsSubtasksId(), epicsSubtasksId, "Количество подзадач Эпика отличается.");
    }

    @Test
    public void shouldReturnEpicWithNewStatus() {
        taskManager.addNewSubtask(subtask1);

        assertEquals(Statuses.NEW, epic.getStatus(), "Статус Эпика отличается от NEW");
    }

    @Test
    public void shouldReturnEpicWithDoneStatus() {
        taskManager.addNewSubtask(subtask3);

        assertEquals(Statuses.DONE, epic.getStatus(), "Статус Эпика отличается от DONE");
    }

    @Test
    public void shouldReturnEpicWithInProgressStatusWhenSubtasksHaveNewAndDone() {
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask3);

        assertEquals(Statuses.IN_PROGRESS, epic.getStatus(), "Статус Эпика отличается от IN_PROGRESS");
    }

    @Test
    public void shouldReturnEpicWithInProgressStatusWhenSubtasksHaveInProgress() {
        taskManager.addNewSubtask(subtask2);

        assertEquals(Statuses.IN_PROGRESS, epic.getStatus(),"Статус Эпика отличается от IN_PROGRESS");
    }
}