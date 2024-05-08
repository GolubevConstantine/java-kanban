import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;
import manager.*;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    private File file;

    @BeforeEach
    void setUp() {
        file = new File("./resources/forTest.csv");
        super.taskManager = new FileBackedTasksManager(file);
        initTasks();
        taskManager.getTaskById(1);
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(3);
        taskManager.getSubtaskById(4);
    }

    @Test
    void loadFromFile() {
        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile(
                new File("./resources/forTest.csv"));
        assertEquals(1, fileManager.getTasks().size(), "Количество задач после выгрузки не совпададает");
        assertEquals(taskManager.getTasks().size(), fileManager.getTasks().size(),
                "Список задач после выгрузки не совпададает");
        assertEquals(1, fileManager.getEpics().size(), "Количество эпиков после выгрузки не совпададает");
        assertEquals(taskManager.getEpics().size(), fileManager.getEpics().size(),
                "Список эпиков после выгрузки не совпададает");
        assertEquals(2, fileManager.subtasks.size(),
                "Количество подзадач после выгрузки не совпададает");
        assertEquals(taskManager.getSubTasks().size(), fileManager.getSubTasks().size(),
                "Список подзадач после выгрузки не совпададает");
        List<Task> history = taskManager.getHistory();
        List<Task> historyFromFile = fileManager.getHistory();
        assertEquals(4, historyFromFile.size(), "Список истории сформирован неверно");
        assertEquals(history.size(), historyFromFile.size(), "Список истории после выгрузки не совпададает");
        assertEquals(taskManager.getPrioritizedTasks().size(), fileManager.getPrioritizedTasks().size(),
                "Отсортированный список после выгрузки не совпададает");
        assertEquals(4, taskManager.getGeneratorId(),
                "Идентификатор последней добавленной задачи после выгрузки не совпададает");
    }

    @AfterEach
    void tearDown() {
        if ((file.exists())) {
            assertTrue(file.delete());
        }
    }
}