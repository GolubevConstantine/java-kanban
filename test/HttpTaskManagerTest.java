import http.HttpTaskManager;
import http.TaskHttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    private TaskHttpServer server;

    @BeforeEach
    void setUp() {
        try {
            server = new TaskHttpServer();
            server.start();
            super.taskManager = new HttpTaskManager();
            initTasks();
            taskManager.getTaskById(1);
            taskManager.getEpicById(2);
            taskManager.getSubtaskById(4);
            taskManager.getSubtaskById(3);
        } catch (IOException e) {
            System.out.println("Ошибка при создании менеджера");
        }
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void saveAndLoad() {
        HttpTaskManager httpTaskManager = new HttpTaskManager(true);

        assertEquals(taskManager.getTasks().toString(), httpTaskManager.getTasks().toString(),
                "Список задач после выгрузки не совпадает");
        assertEquals(taskManager.getEpics().toString(), httpTaskManager.getEpics().toString(),
                "Список эпиков после выгрузки не совпадает");
        assertEquals(taskManager.getSubTasks().toString(), httpTaskManager.getSubTasks().toString(),
                "Список подзадач после выгрузки не совпадает");
        assertEquals(taskManager.getPrioritizedTasks().toString(), httpTaskManager.getPrioritizedTasks().toString(),
                "Список приоритизации после выгрузки не совпадает");
        assertEquals(taskManager.getHistory().toString(), httpTaskManager.getHistory().toString(),
                "Список истории после выгрузки не совпадает");

        assertEquals(1, httpTaskManager.getTasks().get(0).getId(),
                "Id после выгрузки не совпадает");
        assertEquals(2, httpTaskManager.getEpics().get(0).getId(),
                "Id после выгрузки не совпадает");
        assertEquals(3, httpTaskManager.getSubTasks().get(0).getId(),
                "Id после выгрузки не совпадает");
        assertEquals(4, httpTaskManager.getSubTasks().get(1).getId(),
                "Id после выгрузки не совпадает");

        assertEquals(httpTaskManager.getGeneratorId(), taskManager.getGeneratorId(),
                "Идентификатор последней добавленной задачи после выгрузки не совпадает");
    }
}