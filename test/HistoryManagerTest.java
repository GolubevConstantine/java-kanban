
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HistoryManagerTest {
    @Test
    void add() {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        int taskId = inMemoryTaskManager.addTask(task);
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertEquals(1,taskId, "Задача не найдена.");
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

}
