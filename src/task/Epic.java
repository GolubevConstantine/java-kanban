package task;

import java.util.ArrayList;

public class Epic extends Task {

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    private ArrayList<Integer> subtaskIds;

    public Epic( String name, String description) {
        super( name, description);
        this.subtaskIds = new ArrayList<>();
    }
    public void addSubtaskId(int subtaskId){
        subtaskIds.add(subtaskId);
    }
    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

}
