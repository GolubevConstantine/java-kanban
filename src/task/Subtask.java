package task;

public class Subtask extends Task{

    private int epicId;

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public Subtask( String name, String description) {
        super(name, description);
    }
}
