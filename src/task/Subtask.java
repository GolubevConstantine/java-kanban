package task;

public class Subtask extends Task{

    private final int epicId;

    public Integer getEpicId() {
        return epicId;
    }

    public Subtask( String name, String description, Integer epicId) {
        super(name, description);
        this.epicId = epicId;
    }
}
