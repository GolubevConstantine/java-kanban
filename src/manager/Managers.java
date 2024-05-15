package manager;

import http.HttpTaskManager;

import java.io.File;

public class Managers {

    public static TaskManager getDefault(){
        return new HttpTaskManager();
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultFile() {
        return new FileBackedTasksManager(new File("./resources/kanban.csv"));
    }
}
