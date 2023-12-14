package manager;

import task.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task id);

    List<Task> getHistory();

}
