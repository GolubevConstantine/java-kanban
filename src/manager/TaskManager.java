package manager;

import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int generatorId = 1;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    public ArrayList<Task> getTasks() {

        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {

        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubTasks() {

        return new ArrayList<>(subtasks.values());
    }

    public void addTask(Task task) {
        task.setId(generatorId);
        tasks.put(task.getId(), task);
        generatorId++;
    }

    public void updateTask(Task task) {
        if (tasks.get(task.getId()) == null) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        if (epics.get(epic.getId()) == null) {
            return;
        }
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.get(subtask.getId()) == null) {
            return;
        }
        subtasks.put(subtask.getId(), subtask);

        updateEpicStatus(epics.get(subtask.getEpicId()));
    }

    public void addSubtask(Epic epic, Subtask subtask) {
        if (epic.getId() == 0) {
            return;
        }
        subtask.setId(generatorId);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask.getId());
        subtask.setEpicId(epic.getId());
        generatorId++;
    }

    public void addEpic(Epic epic) {
        epic.setId(generatorId);
        epics.put(epic.getId(), epic);
        generatorId++;
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteAllSubtasks() {
        ArrayList<Integer> subtaskWithoutIds = new ArrayList<>();
        for (Epic value : epics.values()) {
            value.setSubtaskIds(subtaskWithoutIds);
        }
        subtasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        if (!epics.get(id).getSubtaskIds().isEmpty()) {
            for (Integer subtaskId : epics.get(id).getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }
        epics.remove(id);
    }

    public void deleteSubtaskById(int id) {
        ArrayList<Integer> subtaskIdsAfter = new ArrayList<>();
        for (Integer subtaskId : epics.get(subtasks.get(id).getEpicId()).getSubtaskIds()) {
            if (subtaskId != id) {
                subtaskIdsAfter.add(subtaskId);
            }
        }
        epics.get(subtasks.get(id).getEpicId()).setSubtaskIds(subtaskIdsAfter);
        subtasks.remove(id);
    }

    public ArrayList<Subtask> getEpicSubtask(Epic epic) {
        ArrayList<Subtask> subtasksFromEpic = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {

            subtasksFromEpic.add(subtasks.get(subtaskId));
        }
        return subtasksFromEpic;
    }

    private void updateEpicStatus(Epic epic) {
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
}
