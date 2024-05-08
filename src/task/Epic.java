package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    protected final ArrayList<Integer> subtaskIds;
    private LocalDateTime endTime;

    public Epic(String title, String description, LocalDateTime startTime, Duration duration, ArrayList<Integer> subtaskIds) {
        super(title, description, startTime, duration);
        this.subtaskIds = subtaskIds;
    }

    public Epic(int id, String title, String description, Status status, LocalDateTime startTime, Duration duration, ArrayList<Integer> subtaskIds, LocalDateTime endTime) {
        super(id, title, description, status, startTime, duration);
        this.subtaskIds = subtaskIds;
        this.endTime = endTime;
    }

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

    public Epic(int id, String title, String description, Status status, LocalDateTime startTime, Duration duration, LocalDateTime endTime) {
        super(id, title, description, status, startTime, duration);
        this.subtaskIds = new ArrayList<>();
        this.endTime = endTime;
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

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String getEndTimeString() {
        if (endTime == null) {
            return "null";
        }
        return getEndTime().format(formatter);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic epic)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(subtaskIds, epic.subtaskIds) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds, endTime);
    }

    @Override
    public String toString() {
        if (subtaskIds.isEmpty()) {
            return "Эпик{" + "название='" + title + '\'' + ", описание='" + description + '\'' + ", id='" + id + '\'' + ", статус='" + status + '}' + '\'';
        } else {
            return "Эпик{" + "название='" + title + '\'' + ", описание='" + description + '\'' + ", id='" + id + '\'' + ", статус='" + status + '\'' + ", дата начала='" + getStartTimeString() + '\'' + ", продолжительность='" + duration + '\'' + ", id подзадач(и)='" + subtaskIds + '}' + '\'';
        }
    }
}
