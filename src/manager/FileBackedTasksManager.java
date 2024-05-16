package manager;

import exceptions.ManagerSaveException;
import task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager {

    protected final File file = new File("./resources/kanban.csv");
    private static final String FILE_HEADER = "id,type,name,status,description,epic,startTime,endTime,duration";


    public FileBackedTasksManager(File file) {
    }

    public FileBackedTasksManager() {
    }

    public static FileBackedTasksManager loadFromFile(File file) {
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
                        fileManager.prioritizedTasks.add(task);
                        break;
                    case EPIC:
                        fileManager.epics.put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        fileManager.subtasks.put(task.getId(), (Subtask) task);
                        int epicId = task.getEpicId();
                        List<Integer> subtaskIds = fileManager.epics.get(epicId).getSubtaskIds();
                        subtaskIds.add(task.getId());
                        fileManager.prioritizedTasks.add(task);
                        fileManager.updateEpicStatus(fileManager.epics.get(epicId));
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

    @Override
    public void setEpicDateTime(int epicId) {
        super.setEpicDateTime(epicId);
        save();
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
        LocalDateTime startTime;
        LocalDateTime endTime;
        if (!line[5].equals("null")) {
            startTime = LocalDateTime.parse(line[5], Task.formatter);
            endTime = LocalDateTime.parse(line[6], Task.formatter);
        } else {
            startTime = null;
            endTime = null;
        }
        Duration duration = Duration.parse(line[7]);
        return switch (taskType) {
            case TASK -> new Task(id, title, description, status, startTime, duration);
            case EPIC -> new Epic(id, title, description, status, startTime, duration, endTime);
            case SUBTASK -> {
                int epicId = Integer.parseInt(line[8]);
                yield new Subtask(id, title, description, status, epicId, startTime, duration);
            }
        };
    }

    protected void save() {
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
            writer.write(toString(task));
            writer.newLine();
        }
        for (Epic epic : getEpics()) {
            writer.write(toString(epic));
            writer.newLine();
        }
        for (Subtask subtask : getSubTasks()) {
            writer.write(toString(subtask));
            writer.newLine();
        }
    }

    private String toString(Task task) {
        return task.getId() + "," + task.getTaskType() + "," + task.getTitle() + "," + task.getStatus() + "," +
                task.getDescription() + "," + task.getStartTimeString() + "," + task.getEndTimeString() + "," +
                task.getDuration();
    }

    private String toString(Epic epic) {
        return epic.getId() + "," + epic.getTaskType() + "," + epic.getTitle() + "," + epic.getStatus() + "," +
                epic.getDescription() + "," + epic.getStartTimeString() + "," + epic.getEndTimeString() + "," +
                epic.getDuration();
    }

    private String toString(Subtask subtask) {
        return subtask.getId() + "," + subtask.getTaskType() + "," + subtask.getTitle() + "," + subtask.getStatus() +
                "," + subtask.getDescription() + "," + subtask.getStartTimeString() + "," + subtask.getEndTimeString() +
                "," + subtask.getDuration() + "," + subtask.getEpicId();
    }

}

