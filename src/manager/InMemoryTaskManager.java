package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;
import exceptions.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected int generatorId = 0;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    public final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    HistoryManager historyManager;

    static final Comparator<Task> COMPARATOR = Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId);

    protected Set<Task> prioritizedTasks = new TreeSet<>(COMPARATOR);

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public int getGeneratorId() {
        return generatorId;
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Integer addTask(Task task) {
        generatorId++;
        validate(task);
        task.setId(generatorId);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return generatorId;
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (epics.get(subtask.getEpicId()) == null) {
            return;
        }
        generatorId++;
        subtask.setId(generatorId);
        validate(subtask);
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
        setEpicDateTime(subtask.getEpicId());
        prioritizedTasks.add(subtask);
    }

    @Override
    public void addEpic(Epic epic) {
        generatorId++;
        epic.setId(generatorId);
        epics.put(epic.getId(), epic);
        setEpicDateTime(epic.getId());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic value : epics.values()) {
            value.getSubtaskIds().clear();
            setEpicDateTime(value.getEpicId());
        }
        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }


    @Override
    public void updateTask(Task task) {
        if (tasks.get(task.getId()) == null) {
            return;
        }
        validate(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.get(epic.getId()) == null) {
            return;
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.get(subtask.getId()) == null) {
            return;
        }
        validate(subtask);
        prioritizedTasks.remove(subtask);
        subtasks.put(subtask.getId(), subtask);

        updateEpicStatus(epics.get(subtask.getEpicId()));
        setEpicDateTime(subtask.getEpicId());
        prioritizedTasks.add(subtask);
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void deleteTaskById(int id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        if (!epics.get(id).getSubtaskIds().isEmpty()) {
            for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }
        epics.remove(id);
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtaskIds().remove(id);
        updateEpicStatus(epics.get(subtask.getEpicId()));
        prioritizedTasks.remove(subtask);
        setEpicDateTime(epic.getId());
    }

    @Override
    public ArrayList<Subtask> getEpicSubtask(Epic epic) {
        ArrayList<Subtask> subtasksFromEpic = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {

            subtasksFromEpic.add(subtasks.get(subtaskId));
        }
        return subtasksFromEpic;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    protected void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        Status status = null;
        for (Integer subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (status == null) {
                status = subtask.getStatus();
                continue;
            }
            if (status.equals(subtask.getStatus())) {
                continue;
            }
            epic.setStatus(Status.IN_PROGRESS);
            return;
        }
        epic.setStatus(status);
    }

    public void setEpicDateTime(int epicId) {
        List<Integer> subtaskIds = epics.get(epicId).getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epics.get(epicId).setDuration(Duration.ZERO);
            epics.get(epicId).setStartTime(null);
            epics.get(epicId).setEndTime(null);
            return;
        }
        LocalDateTime epicStartTime = null;
        LocalDateTime epicEndTime = null;
        Duration epicDuration = Duration.ZERO;
        for (Integer subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            LocalDateTime subtaskStartTime = subtask.getStartTime();
            LocalDateTime subtaskEndTime = subtask.getEndTime();
            if (subtaskStartTime != null) {
                if (epicStartTime == null || subtaskStartTime.isBefore(epicStartTime)) {
                    epicStartTime = subtaskStartTime;
                }
            }
            if (subtaskEndTime != null) {
                if (epicEndTime == null || subtaskEndTime.isAfter(epicEndTime)) {
                    epicEndTime = subtaskEndTime;
                }
            }
            epicDuration = epicDuration.plus(subtasks.get(subtaskId).getDuration());
        }
        epics.get(epicId).setStartTime(epicStartTime);
        epics.get(epicId).setEndTime(epicEndTime);
        epics.get(epicId).setDuration(epicDuration);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void validate(Task newTask) {
        List<Task> prioritizedTasks = getPrioritizedTasks();
        for (Task existTask : prioritizedTasks) {
            if (newTask.getStartTime() == null || existTask.getStartTime() == null) {
                return;
            }
            if (newTask.getId() == existTask.getId()) {
                continue;
            }
            if ((!newTask.getEndTime().isAfter(existTask.getStartTime())) || (!newTask.getStartTime().isBefore(existTask.getEndTime()))) {
                continue;
            }
            throw new CollisionTaskException("Время выполнения задачи пересекается со временем уже существующей "+ existTask + "задачи. Выберите другую дату.");
        }
    }
}
