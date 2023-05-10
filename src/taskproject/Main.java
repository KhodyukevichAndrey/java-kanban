package taskproject;

import taskproject.models.Task; // импорты для тестов
import taskproject.models.Subtask;
import taskproject.models.Epic;
import taskproject.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // - Шаблоны для тестов, на случай если пригодяться. Апдейт статуса Эпика зашил в добавление новой сабтаски

        Task task1 = new Task("Помыть посуду", "Положить в посудомойку" , "NEW");

        taskManager.addNewTask(task1);

        Epic epic1 = new Epic("Переезд","Сбор вещей", "NEW");
        Epic epic2 = new Epic("Уборка", "Навести порядок", "NEW");

        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        Subtask subtask1;
        subtask1 = new Subtask("Сбор вещей", "Сбор одежды", "NEW", epic1.getId());
        Subtask subtask2;
        subtask2 = new Subtask("Сбор вещей", "Сбор документов", "DONE", epic1.getId());
        Subtask subtask3;
        subtask3 = new Subtask("Уборка", "Пропылесосить", "IN_PROGRESS", epic2.getId());

        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);

        System.out.println(epic1.toString());
        System.out.println(epic2.toString());
        System.out.println(subtask1.toString());
        System.out.println(subtask2.toString());
        System.out.println(subtask3.toString());
    }
}
