package manager;

import exceptions.ManagerSaveException;
import task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager {

    public static void main(String[] args) {
        TaskManager fileBackedTasksManager = Managers.getDefaultFile();
        Task task1 = new Task("Task #1", "Description");
        Task task2 = new Task("Task #2", "Description");
        Task task3 = new Task("Task #3", "Description");
        Epic epic = new Epic("Epic #1", "Description");
        Subtask subtask = new Subtask("New subtask", "Description", 2);
        Epic epic1 = new Epic("Epic #2", "Description");
        Subtask subtask1 = new Subtask("New subtask #1", "Description", 4);
        Subtask subtask2 = new Subtask("New subtask #2", "Description", 4);
        fileBackedTasksManager.addTask(task1);
        fileBackedTasksManager.addTask(task2);
        fileBackedTasksManager.addTask(task3);
        fileBackedTasksManager.addEpic(epic);
        fileBackedTasksManager.addSubtask(subtask);
        fileBackedTasksManager.addEpic(epic1);
        fileBackedTasksManager.addSubtask(subtask1);
        fileBackedTasksManager.addSubtask(subtask2);

        fileBackedTasksManager.getTaskById(task1.getId());
        fileBackedTasksManager.getEpicById(epic1.getId());
        fileBackedTasksManager.getSubtaskById(subtask2.getId());
        fileBackedTasksManager.getSubtaskById(subtask1.getId());

        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile(
                new File("./resources/kanban.csv"));
        for (Map.Entry<Integer, Task> entry : fileManager.tasks.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        for (Map.Entry<Integer, Epic> entry : fileManager.epics.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        for (Map.Entry<Integer, Subtask> entry : fileManager.subtasks.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        System.out.println(fileManager.getHistory());
    }

    private final File file;
    private static final String FILE_HEADER = "id,type,name,status,description,epic";

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    @Override
    public Integer addTask(Task newTask) {
        super.addTask(newTask);
        save();
        return newTask.getId();
    }

    @Override
    public void addSubtask(Subtask newSubtask) {
        super.addSubtask(newSubtask);
        save();
    }

    @Override
    public void addEpic(Epic newEpic) {
        super.addEpic(newEpic);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    @Override
    public void deleteSubtaskById(Integer subtaskIdForRemove) {
        super.deleteSubtaskById(subtaskIdForRemove);
        save();
    }

    @Override
    public void updateTask(Task updateTask) {
        super.updateTask(updateTask);
        save();
    }

    @Override
    public void updateEpic(Epic updateEpic) {
        super.updateEpic(updateEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask updateSubtask) {
        super.updateSubtask(updateSubtask);
        save();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }

    private static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(file);
        Map<Integer, Task> fileHistory = new HashMap<>();
        List<Integer> idsHistory = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            List<String> taskLines = reader.lines().toList();
            for (int i = 1; i < taskLines.size(); i++) {
                if (taskLines.get(i).isEmpty() && !taskLines.get(i + 1).isEmpty()) {
                    idsHistory = historyFromString(taskLines.get(i + 1));
                    break;
                }
                String[] line = taskLines.get(i).split(",");
                Task task = fromString(line);
                fileHistory.put(task.getId(), task);
                switch (task.getTaskType()) {
                    case TASK:
                        fileManager.tasks.put(task.getId(), task);
                        break;
                    case EPIC:
                        fileManager.epics.put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        fileManager.subtasks.put(task.getId(), (Subtask) task);
                        int epicId = task.getEpicId();
                        List<Integer> subtaskIds = fileManager.epics.get(epicId).getSubtaskIds();
                        subtaskIds.add(task.getId());
                        break;
                }
                if (task.getId() > fileManager.generatorId) {
                    fileManager.generatorId = task.getId();
                }
            }
            for (Integer id : idsHistory) {
                fileManager.historyManager.add(fileHistory.get(id));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Can't read from file: " + file.getName(), e);
        }
        return fileManager;
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> idsHistory = new ArrayList<>();
        String[] line = value.split(",");
        for (String id : line) {
            idsHistory.add(Integer.valueOf(id));
        }
        return idsHistory;
    }

    private static Task fromString(String[] line) {
        int id = Integer.parseInt(line[0]);
        TaskType taskType = TaskType.valueOf(line[1]);
        String title = line[2];
        Status status = Status.valueOf(line[3]);
        String description = line[4];
        return switch (taskType) {
            case TASK -> new Task(id, title, description, status);
            case EPIC -> new Epic(id, title, description, status);
            case SUBTASK -> {
                int epicId = Integer.parseInt(line[5]);
                yield new Subtask(id, title, description, status, epicId);
            }
        };
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(FILE_HEADER);
            writer.newLine();
            addTasksToFile(writer);
            writer.newLine();
            List<String> ids = new ArrayList<>();
            for (Task task : getHistory()) {
                ids.add(String.valueOf(task.getId()));
            }
            writer.write(String.join(",", ids));
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private void addTasksToFile(BufferedWriter writer) throws IOException {
        for (Task task : getTasks()) {
            writer.write(task.toString(task));
            writer.newLine();
        }
        for (Epic epic : getEpics()) {
            writer.write(epic.toString(epic));
            writer.newLine();
        }
        for (Subtask subtask : getSubTasks()) {
            writer.write(subtask.toString(subtask));
            writer.newLine();
        }
    }
}

