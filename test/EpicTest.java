
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;


class EpicTest {
    @Test
    void addEpicInSubtask() {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        Epic epic = new Epic("Epic #1", "Description");
        inMemoryTaskManager.addEpic(epic);
        Subtask subtask = new Subtask("New subtask", "Description", 2);
        inMemoryTaskManager.addSubtask(subtask);
    }
}