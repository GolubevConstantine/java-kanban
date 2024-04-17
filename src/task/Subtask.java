package task;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String name, String description, Integer epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(int id, String title, String description, Status status, int epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public String toString(Subtask subtask) {
        return subtask.getId() + "," + subtask.getTaskType() + "," + subtask.getTitle() + "," + subtask.getStatus() +
                "," + subtask.getDescription() + "," + subtask.getEpicId();
    }

    public Integer getEpicId() {
        return epicId;
    }
}
