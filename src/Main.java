import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Task #1", "Description");
        Epic epic = new Epic("Task #2", "Description");
        Subtask subtask = new Subtask("New subtask","Description");
        taskManager.addTask(task1);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        taskManager.getTasks();
        taskManager.getEpics();
        taskManager.getSubTasks();
    }
}
