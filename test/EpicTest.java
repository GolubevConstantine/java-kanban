
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;


class EpicTest {
    @Test
    void addEpicInSubtask() {
        TaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic #1", "Description");
        inMemoryTaskManager.addEpic(epic);
        Subtask subtask = new Subtask("New subtask", "Description", 2);
        inMemoryTaskManager.addSubtask(subtask);
    }
}