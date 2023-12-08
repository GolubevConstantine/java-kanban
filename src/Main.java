import manager.TaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Task #1", "Description");
        Epic epic = new Epic("Epic #1", "Description");
        Subtask subtask = new Subtask("New subtask", "Description",2);
        Epic epic1 = new Epic("Epic #2", "Description");
        Subtask subtask1 = new Subtask("New subtask #1", "Description",4);
        Subtask subtask2 = new Subtask("New subtask #2", "Description",4);
        taskManager.addTask(task1);
        //Создание.Сам объект должен передаваться в качестве параметра.
        taskManager.addSubtask(subtask);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        //Удаление по идентификатору.
        //taskManager.deleteTaskById(1);
        //taskManager.deleteEpicById(4);
       taskManager.deleteSubtaskById(5);
        //Обновление.Новая версия объекта с верным идентификатором передаётся в виде параметра.
        task1.setTitle("TASK #100");
        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);
        System.out.println(taskManager.getTaskById(1));
        epic1.setTitle("Epic #2 After Update");
        epic1.setDescription("Description After Update");
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);
        taskManager.updateEpic(epic1);
        System.out.println(taskManager.getEpicSubtask(epic1));
        System.out.println(taskManager.getEpicById(4));
        subtask1.setTitle("New subtask #1 After Update");
        subtask1.setDescription("Description After Update");
        subtask1.setStatus(Status.DONE);
        System.out.println(taskManager.getSubtaskById(5));
        System.out.println("Получение списка всех задач.");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubTasks());
        System.out.println("Получение по идентификатору.");
        System.out.println(taskManager.getTaskById(0));
        System.out.println(taskManager.getTaskById(1));
        System.out.println(taskManager.getEpicById(2));
        System.out.println(taskManager.getSubtaskById(3));
        System.out.println("Удаление всех задач.");
        taskManager.deleteAllTasks();
        System.out.println(taskManager.getTasks());
       // taskManager.deleteAllEpics();
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubTasks());
       // taskManager.deleteAllSubtasks();
        System.out.println(taskManager.getEpicById(4));
        System.out.println(taskManager.getEpicSubtask(epic1));
        System.out.println(taskManager.getSubTasks());
    }
}
