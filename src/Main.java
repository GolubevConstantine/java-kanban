import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager inMemoryTaskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task1 = new Task("Task #1", "Description");
        Task task2 = new Task("Task #2", "Description");
        Task task3 = new Task("Task #3", "Description");
        Epic epic = new Epic("Epic #1", "Description");
        Subtask subtask = new Subtask("New subtask", "Description",2);
        Epic epic1 = new Epic("Epic #2", "Description");
        Subtask subtask1 = new Subtask("New subtask #1", "Description",4);
        Subtask subtask2 = new Subtask("New subtask #2", "Description",4);
        inMemoryTaskManager.addTask(task1);
        inMemoryTaskManager.addTask(task2);
        inMemoryTaskManager.addTask(task3);
        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.addSubtask(subtask1);
        inMemoryTaskManager.addSubtask(subtask2);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(epic1);
        historyManager.add(subtask1);
        historyManager.add(subtask2);
        historyManager.add(task3);
        System.out.println(historyManager.getHistory());
        historyManager.add(task1);
        System.out.println(historyManager.getHistory());
        historyManager.add(task3);
        System.out.println(historyManager.getHistory());
        historyManager.remove(2);
        System.out.println(historyManager.getHistory());
        historyManager.remove(4);
        historyManager.remove(5);
        historyManager.remove(6);
        System.out.println(historyManager.getHistory());

    }
}
