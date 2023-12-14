package manager;

import task.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> ids;

    public InMemoryHistoryManager() {
        this.ids = new ArrayList<>();
    }

    @Override
    public void add(Task id) {

        if (ids.size() < 10) {
            ids.add(id);
        } else {
            ids.remove(0);
            ids.add(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return Collections.unmodifiableList(ids);
    }
}
