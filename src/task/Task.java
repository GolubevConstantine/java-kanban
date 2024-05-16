package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {

    protected int id;
    protected String title;
    protected String description;
    protected Status status;
    protected LocalDateTime startTime;
    protected Duration duration;

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public Task(String title, String description, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String title, String description, Status status, LocalDateTime startTime,
                Duration duration) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String title, String description, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskType getTaskType() {
        return TaskType.TASK;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getStartTimeString() {
        if (startTime == null) {
            return "null";
        }
        return startTime.format(formatter);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plusMinutes(duration.toMinutes());
        }
        return null;
    }

    public String getEndTimeString() {
        if (startTime != null) {
            return getEndTime().format(formatter);
        }
        return null;
    }

    public Integer getEpicId() {
        return null;
    }

    @Override
    public String toString() {
        return "Задача{" +
                "название='" + title + '\'' +
                ", описание='" + description + '\'' +
                ", id='" + id + '\'' +
                ", статус='" + status + '\'' +
                ", дата начала='" + getStartTimeString() + '\'' +
                ", продолжительность='" + duration + '}' + '\'';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(title, task.title) &&
                Objects.equals(description, task.description) &&
                (id == task.id) &&
                Objects.equals(status, task.status) &&
                Objects.equals(startTime, task.startTime) &&
                (duration == task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status, startTime, duration);
    }
}
