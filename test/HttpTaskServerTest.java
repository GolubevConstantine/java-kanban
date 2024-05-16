
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import http.*;
import task.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    static TaskHttpServer taskHttpServer;
    static HttpTaskManager taskManager;
    static HttpTaskServer taskServer;

    protected Duration DURATION = Duration.ofMinutes(100);

    static Gson gson;

    @BeforeEach
    void setUp() throws IOException {
        taskHttpServer = new TaskHttpServer();
        taskHttpServer.start();
        taskManager = new HttpTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
        Task task1 = new Task("Задача", "description1",
                LocalDateTime.of(2024, 1, 1, 0, 0), DURATION);
        Epic epic2 = new Epic("Эпик", "description2");
        Subtask subtask3 = new Subtask("Подзадача", "description3", 2,
                LocalDateTime.of(2024, 1, 2, 0, 0), DURATION);
        Subtask subtask4 = new Subtask("Подзадача", "description4", 2,
                LocalDateTime.of(2024, 2, 4, 0, 0), DURATION);
        taskServer.start();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        taskManager.addTask(task1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask3);
        taskManager.addSubtask(subtask4);
        taskManager.getEpicById(2);
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(3);

    }

    @AfterEach
    void stopServer() {
        taskHttpServer.stop();
        taskServer.stop();
    }

    @Test
    void getAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasksList = gson.fromJson(response.body(), taskType);

        assertEquals(HttpStatus.SC_OK, response.statusCode(), "Код ответа не 200");
        assertNotNull(tasksList, "Список задач не получен");
        assertEquals(taskManager.getTasks().toString(), tasksList.toString(), "Получен неверный список задач");
    }

    @Test
    void getAllEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type epicType = new TypeToken<List<Epic>>() {
        }.getType();
        List<Epic> epicsList = gson.fromJson(response.body(), epicType);

        assertEquals(HttpStatus.SC_OK, response.statusCode(), "Код ответа не 200");
        assertNotNull(epicsList, "Список эпиков не получен");
        assertEquals(taskManager.getEpics().toString(), epicsList.toString(), "Получен неверный список эпиков");
    }

    @Test
    void getAllSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type subtaskType = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> subtasksList = gson.fromJson(response.body(), subtaskType);

        assertEquals(HttpStatus.SC_OK, response.statusCode(), "Код ответа не 200");
        assertNotNull(subtasksList, "Список подзадач не получен");
        assertEquals(taskManager.getSubTasks().toString(), subtasksList.toString(), "Получен неверный список подзадач");
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskDeserialized = gson.fromJson(response.body(), Task.class);

        assertEquals(HttpStatus.SC_OK, response.statusCode(), "Код ответа не 200");
        assertNotNull(taskDeserialized, "Задача не получена");
        assertEquals(taskManager.getTasks().get(0).toString(), taskDeserialized.toString(), "Получена неверная задача");
    }

    @Test
    void getTaskIncorrectId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=a");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.SC_BAD_REQUEST, response.statusCode(), "Код ответа не 400");
        assertEquals("Некорректный id", response.body(), "Ответ сервера не совпадает");
    }

    @Test
    void getTaskWrongId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.SC_NOT_FOUND, response.statusCode(), "Код ответа не 404");
        assertEquals("Задача с id 3 не найдена", response.body(), "Ответ сервера не совпадает");
    }

    @Test
    void getEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epicDeserialized = gson.fromJson(response.body(), Epic.class);

        assertEquals(HttpStatus.SC_OK, response.statusCode(), "Код ответа не 200");
        assertNotNull(epicDeserialized, "Эпик не получен");
        assertEquals(taskManager.getEpics().get(0).toString(), epicDeserialized.toString(), "Получен неверный эпик");
    }

    @Test
    void getSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask subtaskDeserialized = gson.fromJson(response.body(), Subtask.class);

        assertEquals(HttpStatus.SC_OK, response.statusCode(), "Код ответа не 200");
        assertNotNull(subtaskDeserialized, "Подзадача не получена");
        assertEquals(taskManager.getSubTasks().get(0).toString(), subtaskDeserialized.toString(), "Получена неверная подзадача");
    }

    @Test
    void getSubtasksByOneEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type subtaskType = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> subtasksList = gson.fromJson(response.body(), subtaskType);

        assertEquals(HttpStatus.SC_OK, response.statusCode(), "Код ответа не 200");
        assertNotNull(subtasksList, "Список подзадач не получен");
        assertEquals(taskManager.getEpicSubtask(taskManager.getEpicById(2)).toString(), subtasksList.toString(), "Получен неверный список подзадач");
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> history = gson.fromJson(response.body(), taskType);

        assertEquals(HttpStatus.SC_OK, response.statusCode(), "Код ответа не 200");
        assertNotNull(history, "Список истории не получен");
        assertEquals(3, history.size(), "Длина списка истории не 3");
        assertEquals(taskManager.getHistory().get(0).getId(), history.get(0).getId(),
                "Id первого элемента списка не совпадает");
        assertEquals(taskManager.getHistory().get(1).getId(), history.get(1).getId(),
                "Id второго элемента списка не совпадает");
        assertEquals(taskManager.getHistory().get(2).getId(), history.get(2).getId(),
                "Id третьего элемента списка не совпадает");
    }

    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> priority = gson.fromJson(response.body(), taskType);

        assertEquals(HttpStatus.SC_OK, response.statusCode(), "Код ответа не 200");
        assertNotNull(priority, "Список приоритетных задач не получен");
        assertEquals(3, priority.size(), "Длина списка приоритетных задач не 3");
        assertEquals(taskManager.getPrioritizedTasks().get(0).getId(), priority.get(0).getId(),
                "Id первого элемента списка не совпадает");
        assertEquals(taskManager.getPrioritizedTasks().get(1).getId(), priority.get(1).getId(),
                "Id второго элемента списка не совпадает");
        assertEquals(taskManager.getPrioritizedTasks().get(2).getId(), priority.get(2).getId(),
                "Id третьего элемента списка не совпадает");
    }

    @Test
    void removeTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.SC_OK, response.statusCode(), "Код ответа не 200");
        assertTrue(taskManager.getTasks().isEmpty(), "Задача не удалена");
        assertNull(taskManager.getTaskById(1), "Задача не удалена");
    }

    @Test
    void removeEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.SC_OK, response.statusCode(), "Код ответа не 200");
        assertTrue(taskManager.getEpics().isEmpty(), "Эпик не удален");
        assertNull(taskManager.getEpicById(2), "Эпик не удален");
    }

    @Test
    void removeSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.SC_OK, response.statusCode(), "Код ответа не 200");
        assertEquals(1, taskManager.getSubTasks().size(), "Подзадача не удалена");
        assertNull(taskManager.getSubtaskById(3), "Подзадача не удалена");
    }

    @Test
    void removeAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.SC_OK, response.statusCode(), "Код ответа не 200");
        assertTrue(taskManager.getTasks().isEmpty(), "Задачи не удалены");
        assertEquals("Все задачи удалены", response.body(), "Неверный ответ от сервера");
    }

    @Test
    void removeAllEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.SC_OK, response.statusCode(), "Код ответа не 200");
        assertTrue(taskManager.getEpics().isEmpty(), "Эпики не удалены");
        assertEquals("Все эпики удалены", response.body(), "Неверный ответ от сервера");
    }

    @Test
    void removeAllSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.SC_OK, response.statusCode(), "Код ответа не 200");
        assertTrue(taskManager.getSubTasks().isEmpty(), "Подзадачи не удалены");
        assertEquals("Все подзадачи удалены", response.body(), "Неверный ответ от сервера");
    }

    @Test
    void addNewTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task task = new Task("Задача", "description5",
                LocalDateTime.of(2024, 1, 4, 0, 0),DURATION);
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.SC_CREATED, response.statusCode(), "Код ответа не 201");
        assertEquals(5, taskManager.getTasks().get(1).getId(), "Id новой задачи не совпадает");
        assertEquals(2, taskManager.getTasks().size(), "Новая задача не добавлена");
        assertEquals("Задача добавлена", response.body(), "Новая задача не добавлена");
    }

    @Test
    void addNewTaskEmptyBody() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.SC_BAD_REQUEST, response.statusCode(), "Код ответа не 400");
        assertEquals("Необходимо заполнить все поля задачи", response.body(), "Ответ сервера не совпадает");
    }


    @Test
    void updateTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Task task = new Task(1, "Задача", "description1", Status.IN_PROGRESS,
                LocalDateTime.of(2024, 1, 4, 0, 0), Duration.ofDays(1000));
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.SC_CREATED, response.statusCode(), "Код ответа не 201");
        assertEquals(1, taskManager.getTasks().get(0).getId(), "Id задачи не совпадает");
        assertEquals(1, taskManager.getTasks().size(), "Список состоит не из одной задачи");
        assertEquals("Задача обновлена", response.body(), "Задача не обновлена");
        assertEquals(task.toString(), taskManager.getTaskById(1).toString(), "Задача не обновлена");
    }

    @Test
    void addNewEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic5 = new Epic("Эпик", "description5");
        String json = gson.toJson(epic5);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.SC_CREATED, response.statusCode(), "Код ответа не 201");
        assertEquals(5, taskManager.getEpics().get(1).getId(), "Id нового эпика не совпадает");
        assertEquals(2, taskManager.getEpics().size(), "Новый эпик не добавлен");
        assertEquals("Эпик добавлен", response.body(), "Новый эпик не добавлен");
    }

    @Test
    void updateEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Epic epic2 = new Epic(2, "Эпик", "d", Status.NEW,
                LocalDateTime.of(2023, 1, 3, 0, 0), Duration.ofDays(1000),
                LocalDateTime.of(2023, 1, 3, 16, 40));
        String json = gson.toJson(epic2);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.SC_CREATED, response.statusCode(), "Код ответа не 201");
        assertEquals(2, taskManager.getEpics().get(0).getId(), "Id эпика не совпадает");
        assertEquals(1, taskManager.getEpics().size(), "Список состоит не из одного эпика");
        assertEquals("Эпик обновлен", response.body(), "Эпик не обновлен");
        assertEquals(epic2.toString(), taskManager.getEpicById(2).toString(), "Эпик не обновлен");
    }

    @Test
    void addNewSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        Subtask subtask5 = new Subtask("Подзадача", "description5", 2,
                LocalDateTime.of(2023, 1, 4, 0, 0), DURATION);
        String json = gson.toJson(subtask5);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.SC_CREATED, response.statusCode(), "Код ответа не 201");
        assertEquals(5, taskManager.getSubTasks().get(2).getId(), "Id новой подзадачи не совпадает");
        assertEquals(3, taskManager.getSubTasks().size(), "Новая подзадача не добавлена");
        assertEquals("Подзадача добавлена", response.body(), "Новая подзадача не добавлена");
    }

    @Test
    void updateSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        Subtask subtask3 = new Subtask(3, "Подзадача", "description3", Status.NEW, 2,
                LocalDateTime.of(2024, 4, 2, 0, 0), Duration.ofDays(500));
        String json = gson.toJson(subtask3);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatus.SC_CREATED, response.statusCode(), "Код ответа не 201");
        assertEquals(3, taskManager.getSubTasks().get(0).getId(), "Id подзадачи не совпадает");
        assertEquals(2, taskManager.getSubTasks().size(), "Список состоит не из двух подзадач");
        assertEquals("Подзадача обновлена", response.body(), "Подзадача не обновлена");
        assertEquals(subtask3.toString(), taskManager.getSubtaskById(3).toString(), "Подзадача не обновлена");
    }


}