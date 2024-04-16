
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import task.Subtask;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    private final TaskManager inMemoryTaskManager = Managers.getDefault();

    @Test
    void addNewSubtaskWithOutEpic() {
        Subtask subtask = new Subtask("New subtask", "Description", 2);
         inMemoryTaskManager.addSubtask(subtask);
        final List<Task> tasks = inMemoryTaskManager.getTasks();

        assertTrue(tasks.isEmpty(), "Подзадачи возвращаются.");

    }
}