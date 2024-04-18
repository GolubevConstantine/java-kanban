package task;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds;

    public Epic(String name, String description) {
        super(name, description);
        this.subtaskIds = new ArrayList<>();
    }

    public Epic(int id, String title, String description, Status status) {
        super(id, title, description, status);
        this.subtaskIds = new ArrayList<>();
    }

    public Epic(String title, String description, ArrayList<Integer> subtaskIds) {
        super(title, description);
        this.subtaskIds = subtaskIds;
    }

    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

}
