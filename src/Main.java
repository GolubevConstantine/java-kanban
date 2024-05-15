
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.HttpTaskManager;
import http.HttpTaskServer;
import task.*;

import http.TaskHttpServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;


public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        TaskHttpServer taskHttpServer = new TaskHttpServer();
        taskHttpServer.start();

        HttpTaskManager taskManager = new HttpTaskManager();

        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.start();
        Duration duration = Duration.ofMinutes(100);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        Task task1 = new Task("Задача", "description1",
                LocalDateTime.of(2023, 1, 1, 0, 0), duration);
        Epic epic2 = new Epic("Эпик", "description2");
        Subtask subtask3 = new Subtask("Подзадача", "description3", 2,
                LocalDateTime.of(2023, 1, 2, 0, 0), duration);
        Subtask subtask4 = new Subtask("Подзадача", "description4", 2,
                LocalDateTime.of(2023, 1, 3, 0, 0), duration);

        taskManager.addTask(task1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask3);
        taskManager.addSubtask(subtask4);

        taskManager.getEpicById(2);
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(4);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task task = new Task("Задача", "description5",
                LocalDateTime.of(2023, 1, 4, 0, 0), Duration.ofDays(1000));
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(body);
        System.out.println(json);

        taskHttpServer.stop();
        server.stop();

    }
}

