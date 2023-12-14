import manager.InMemoryTaskManager;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

public class Main {

    public static void main(String[] args) {

        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Task task1 = new Task("Task #1", "Description");
        Epic epic = new Epic("Epic #1", "Description");
        Subtask subtask = new Subtask("New subtask", "Description",2);
        Epic epic1 = new Epic("Epic #2", "Description");
        Subtask subtask1 = new Subtask("New subtask #1", "Description",4);
        Subtask subtask2 = new Subtask("New subtask #2", "Description",4);
        inMemoryTaskManager.addTask(task1);
        //Создание.Сам объект должен передаваться в качестве параметра.
        inMemoryTaskManager.addSubtask(subtask);
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subtask);
        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.addSubtask(subtask1);
        inMemoryTaskManager.addSubtask(subtask2);
        //Удаление по идентификатору.
        //inMemoryTaskManager.deleteTaskById(1);
        //inMemoryTaskManager.deleteEpicById(4);
       inMemoryTaskManager.deleteSubtaskById(5);
        //Обновление.Новая версия объекта с верным идентификатором передаётся в виде параметра.
        task1.setTitle("TASK #100");
        task1.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateTask(task1);
        System.out.println(inMemoryTaskManager.getTaskById(1));
        epic1.setTitle("Epic #2 After Update");
        epic1.setDescription("Description After Update");
        subtask1.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(subtask2);
        inMemoryTaskManager.updateEpic(epic1);
        System.out.println(inMemoryTaskManager.getEpicSubtask(epic1));
        System.out.println(inMemoryTaskManager.getEpicById(4));
        subtask1.setTitle("New subtask #1 After Update");
        subtask1.setDescription("Description After Update");
        subtask1.setStatus(Status.DONE);
        System.out.println(inMemoryTaskManager.getSubtaskById(5));
        System.out.println("Получение списка всех задач.");
        System.out.println(inMemoryTaskManager.getTasks());
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println(inMemoryTaskManager.getSubTasks());
        System.out.println("Получение по идентификатору.");
        System.out.println(inMemoryTaskManager.getTaskById(0));
        System.out.println(inMemoryTaskManager.getTaskById(1));
        System.out.println(inMemoryTaskManager.getEpicById(2));
        System.out.println(inMemoryTaskManager.getSubtaskById(3));
        System.out.println("Удаление всех задач.");
        inMemoryTaskManager.deleteAllTasks();
        System.out.println(inMemoryTaskManager.getTasks());
       // inMemoryTaskManager.deleteAllEpics();
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println(inMemoryTaskManager.getSubTasks());
       // inMemoryTaskManager.deleteAllSubtasks();
        System.out.println(inMemoryTaskManager.getEpicById(4));
        System.out.println(inMemoryTaskManager.getEpicSubtask(epic1));
        System.out.println(inMemoryTaskManager.getSubTasks());
    }
}
