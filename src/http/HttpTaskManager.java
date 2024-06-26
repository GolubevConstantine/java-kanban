package http;

import com.google.gson.*;
import manager.Managers;
import task.*;
import manager.FileBackedTasksManager;

import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {

    private static final String TASKS = "/tasks";
    private static final String EPICS = "/epics";
    private static final String SUBTASKS = "/subtasks";
    private static final String HISTORY = "/history";

    private final HttpTaskClient client = new HttpTaskClient();

    private final  Gson gson = Managers.getGson();

    public HttpTaskManager(boolean toLoad) {
        if (toLoad) {
            load();
        }
    }

    public HttpTaskManager() {
        this(false);
    }


    @Override
    public void save() {
        client.put(TASKS, gson.toJson(getTasks()));
        client.put(EPICS, gson.toJson(getEpics()));
        client.put(SUBTASKS, gson.toJson(getSubTasks()));
        client.put(HISTORY, gson.toJson(getHistory().stream().map(Task::getId).collect(Collectors.toList())));
    }

    private void load() {
        JsonElement jsonTasks = JsonParser.parseString(client.load(TASKS));
        JsonArray tasksArray = jsonTasks.getAsJsonArray();
        for (JsonElement jsonTask : tasksArray) {
            Task task = gson.fromJson(jsonTask, Task.class);
            int id = task.getId();
            tasks.put(id, task);
            prioritizedTasks.add(task);
            if (id > generatorId) {
                generatorId = id;
            }
        }

        JsonElement jsonEpics = JsonParser.parseString(client.load(EPICS));
        JsonArray epicsArray = jsonEpics.getAsJsonArray();
        for (JsonElement jsonEpic : epicsArray) {
            Epic epic = gson.fromJson(jsonEpic, Epic.class);
            int id = epic.getId();
            epics.put(id, epic);
            if (id > generatorId) {
                generatorId = id;
            }
        }

        JsonElement jsonSubtasks = JsonParser.parseString(client.load(SUBTASKS));
        JsonArray subtasksArray = jsonSubtasks.getAsJsonArray();
        for (JsonElement jsonSubtask : subtasksArray) {
            Subtask subtask = gson.fromJson(jsonSubtask, Subtask.class);
            int id = subtask.getId();
            subtasks.put(id, subtask);
            prioritizedTasks.add(subtask);
            if (id > generatorId) {
                generatorId = id;
            }
        }

        JsonElement historyList = JsonParser.parseString(client.load(HISTORY));
        JsonArray historyArray = historyList.getAsJsonArray();
        for (JsonElement jsonId : historyArray) {
            int id = jsonId.getAsInt();
            if (tasks.containsKey(id)) {
                historyManager.add(tasks.get(id));
            } else if (epics.containsKey(id)) {
                historyManager.add(epics.get(id));
            } else if (subtasks.containsKey(id)) {
                historyManager.add(subtasks.get(id));
            }
        }
    }
}
