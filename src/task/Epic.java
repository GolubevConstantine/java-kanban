package task;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds;

    public Epic( String name, String description) {
        super( name, description);
        this.subtaskIds = new ArrayList<>();
    }
    public void addSubtaskId(int subtaskId){
        subtaskIds.add(subtaskId);
    }

    @Override
    public void setStatus(Status status) {

    }
}
