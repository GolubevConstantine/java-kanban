
import exceptions.CollisionTaskException;
import manager.*;
import task.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected final LocalDateTime DATE = LocalDateTime.of(2024, 5, 1, 0, 0);
    protected final int EPIC_ID = 2;
    protected Duration DURATION =  Duration.ofMinutes(100);
    protected Task task1;
    protected Epic epic2;
    protected Subtask subtask3;
    protected Subtask subtask4;

    protected void initTasks() {
        task1 = new Task("Задача", "description1", DATE, DURATION);
        taskManager.addTask(task1);
        epic2 = new Epic("Эпик", "description3");
        taskManager.addEpic(epic2);
        subtask3 = new Subtask("Подзадача", "description3", EPIC_ID, DATE.plusDays(1), DURATION);
        taskManager.addSubtask(subtask3);
        subtask4 = new Subtask("Подзадача", "description4", EPIC_ID, DATE.plusDays(2), DURATION);
        taskManager.addSubtask(subtask4);
    }

    @Test
    void addTask() {
        Task expectedTask = taskManager.getTaskById(1);
        assertNotNull(expectedTask, "Задача не найдена.");
        assertNotNull(taskManager.getTasks(), "Задачи на возвращаются.");
        assertEquals(1, taskManager.getTasks().size(), "Неверное количество задач.");
        assertEquals(1, expectedTask.getId(), "Идентификаторы задач не совпадают");
        Task taskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 1)
                .findFirst()
                .orElse(null);
        assertNotNull(taskPriority, "Задача не добавлена в список приоритизации");
        assertEquals(taskPriority, expectedTask, "В список приоритизации добавлена неверная задача");
    }

    @Test
    void addEpic() {
        Epic expectedEpic = taskManager.getEpicById(2);
        assertNotNull(expectedEpic, "Задача не найдена.");
        assertNotNull(taskManager.getEpics(), "Задачи на возвращаются.");
        assertEquals(1, taskManager.getEpics().size(), "Неверное количество задач.");
        assertNotNull(expectedEpic.getSubtaskIds(), "Список подзадач не создан.");
        assertEquals(Status.NEW, expectedEpic.getStatus(), "Статус не NEW");
        assertEquals(2, expectedEpic.getId(), "Идентификаторы задач не совпадают");
    }

    @Test
    void addSubtask() {
        Epic expectedEpicOfSubtask = taskManager.getEpicById(EPIC_ID);
        assertNotNull(expectedEpicOfSubtask.getStartTime(), "Время эпика не null");
        Subtask expectedSubtask = taskManager.getSubtaskById(3);
        assertNotNull(expectedSubtask, "Задача не найдена.");
        assertNotNull(taskManager.getSubTasks(), "Задачи на возвращаются.");
        assertEquals(2, taskManager.getSubTasks().size(), "Неверное количество задач.");
        assertNotNull(expectedEpicOfSubtask, "Эпик подзадачи не найден");
        assertNotNull(taskManager.getEpicSubtask(expectedEpicOfSubtask), "Список подзадач не обновился");
        assertEquals(DATE.plusDays(1), expectedEpicOfSubtask.getStartTime(), "Время эпика не обновилось");
        assertEquals(Status.NEW, expectedEpicOfSubtask.getStatus(), "Статус не NEW");
        assertEquals(3, expectedSubtask.getId(), "Идентификаторы задач не совпадают");
        assertEquals(expectedEpicOfSubtask, epic2, "Эпик подзадачи неверный");
        Task subtaskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 3)
                .findFirst()
                .orElse(null);
        assertNotNull(subtaskPriority, "Задача не добавлена в список приоритизации");
        assertEquals(subtaskPriority, expectedSubtask, "В список приоритизации добавлена неверная задача");
        assertNotNull(expectedEpicOfSubtask.getStartTime(), "Время эпика не изменилось");
    }

    @Test
    void checkEpicStatus() {
        Epic expectedEpicOfSubtask = taskManager.getEpicById(EPIC_ID);
        Subtask updateSubtask4 = new Subtask(4, "Подзадача", "description4", Status.DONE, EPIC_ID,
                DATE.plusDays(2), DURATION);
        taskManager.updateSubtask(updateSubtask4);
        assertEquals(Status.IN_PROGRESS, expectedEpicOfSubtask.getStatus(), "Статус не IN_PROGRESS");
        Subtask updateSubtask3 = new Subtask(3, "Подзадача", "description3", Status.DONE, EPIC_ID,
                DATE.plusDays(1), DURATION);
        Subtask update2Subtask4 = new Subtask(4, "Подзадача", "description4", Status.DONE, EPIC_ID,
                DATE.plusDays(2), DURATION);
        taskManager.updateSubtask(updateSubtask3);
        taskManager.updateSubtask(update2Subtask4);
        assertEquals(Status.DONE, expectedEpicOfSubtask.getStatus(), "Статус не DONE");
        Subtask update2Subtask3 = new Subtask(3, "Подзадача", "description3", Status.IN_PROGRESS,
                EPIC_ID, DATE.plusDays(1), DURATION);
        Subtask update3Subtask4 = new Subtask(4, "Подзадача", "description4", Status.IN_PROGRESS,
                EPIC_ID, DATE.plusDays(2), DURATION);
        taskManager.updateSubtask(update2Subtask3);
        taskManager.updateSubtask(update3Subtask4);
        assertEquals(Status.IN_PROGRESS, expectedEpicOfSubtask.getStatus(), "Статус не IN_PROGRESS");
        assertEquals( Duration.ofMinutes(200).toMinutes(), expectedEpicOfSubtask.getDuration().toMinutes(), "Продолжительность эпика не обновилась");
    }

    @Test
    void getHistory() {
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(4);
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(3);
        taskManager.getTaskById(1);
        List<Task> history = taskManager.getHistory();
        assertEquals(4, history.size(), "Список истории сформирован неверно");
        assertEquals(2, history.get(0).getId(), "Задача 2 не добавлена в список истории");
        assertEquals(4, history.get(1).getId(), "Задача 4 не добавлена в список истории");
        assertEquals(3, history.get(2).getId(), "Задача 3 не добавлена в список истории");
        assertEquals(1, history.get(3).getId(), "Задача 1 не добавлена в список истории");
    }

    @Test
    void getPrioritizedTasks() {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(1, prioritizedTasks.get(0).getId(), "Задача 1 не приоритизирована");
        assertEquals(3, prioritizedTasks.get(1).getId(), "Задача 3 не приоритизирована");
        assertEquals(4, prioritizedTasks.get(2).getId(), "Задача 4 не приоритизирована");
    }

    @Test
    void removeTaskById() {
        assertNotNull(taskManager.getTasks(), "Список задач не заполнен");
        assertEquals(1, taskManager.getTasks().size(), "Неверное количество задач.");
        taskManager.deleteTaskById(1);
        assertNull(taskManager.getTaskById(1), "Задача не удалена");
        Task taskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 1)
                .findFirst()
                .orElse(null);
        assertNull(taskPriority, "Задача не удалена из списка приоритизации");
    }

    @Test
    void removeSubtaskById() {
        assertNotNull(taskManager.getSubTasks(), "Список подзадач не заполнен");
        assertEquals(2, taskManager.getSubTasks().size(), "Неверное количество задач.");
        taskManager.deleteSubtaskById(3);
        assertNull(taskManager.getSubtaskById(3), "Подзадача не удалена");
        Task subtaskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 3)
                .findFirst()
                .orElse(null);
        assertNull(subtaskPriority, "Задача не удалена из списка приоритизации");
        assertEquals(DATE.plusDays(2), taskManager.getEpicById(EPIC_ID).getStartTime(), "Время эпика не изменилось");
    }

    @Test
    void removeEpicById() {
        assertNotNull(taskManager.getEpics(), "Список эпиков не заполнен");
        taskManager.deleteEpicById(2);
        assertNull(taskManager.getEpicById(2), "Эпик не удален");
    }

    @Test
    void validate() {
        Task task1 = new Task("Задача1", "description1", DATE, Duration.ofDays(100));
        Task task2 = new Task("Задача2", "description2", DATE, Duration.ofDays(100));

        CollisionTaskException exception = assertThrows(CollisionTaskException.class,
                () -> {
                    taskManager.addTask(task1);
                    taskManager.addTask(task2);
                });
        assertEquals("Время выполнения задачи пересекается со временем уже существующей " +
                "задачи. Выберите другую дату.", exception.getMessage());
    }
}