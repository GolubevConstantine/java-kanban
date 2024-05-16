package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.HttpTaskManager;
import task.DurationAdapter;
import task.LocalDateAdapter;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {

    public static TaskManager getDefault() {
        return new HttpTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultFile() {
        return new FileBackedTasksManager(new File("./resources/kanban.csv"));
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }
}
