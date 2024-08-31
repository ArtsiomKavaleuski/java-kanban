package com.koval.kanban.service;

import com.koval.kanban.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

public class HttpTaskManagerTasksTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer;

    {
        try {
            taskServer = new HttpTaskServer(8080, taskManager);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpTaskManagerTasksTest() throws IOException {

    }

    @BeforeEach
    public void setUp() {
        taskManager.removeAllTasks();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void shouldReturnTasksListAndSuccessCode() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "task1 description", taskManager.getId(), TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(90));
        Task task2 = new Task("task2", "task2 description", taskManager.getId(), TaskStatus.NEW, LocalDateTime.of(2024, Month.AUGUST, 27, 15,30), Duration.ofMinutes(90));
        String task1Json = CSVutils.taskToJson(task1);
        String task2Json = CSVutils.taskToJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task1Json)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task2Json)).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response3.statusCode());

        List<Task> tasksFromManagers = taskManager.getTasks();

        Assertions.assertNotNull(tasksFromManagers, "Задачи не возвращаются");
        Assertions.assertEquals(2, tasksFromManagers.size(), "Некорректное количество задач");
        Assertions.assertEquals("task1", tasksFromManagers.get(0).getName(), "Некорректное имя задачи");
        Assertions.assertEquals("task2", tasksFromManagers.get(1).getName(), "Некорректное имя задачи");
    }

    @Test
    public void shouldReturnTaskByIdAndSuccessCode() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "task1 description", taskManager.getId(), TaskStatus.NEW, LocalDateTime.of(2024, Month.AUGUST, 26, 15, 0), Duration.ofMinutes(90));
        Task task2 = new Task("task2", "task2 description", taskManager.getId(), TaskStatus.NEW, LocalDateTime.of(2024, Month.AUGUST, 27, 15,30), Duration.ofMinutes(90));
        String task1Json = CSVutils.taskToJson(task1);
        String task2Json = CSVutils.taskToJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/tasks");
        URI url2 = URI.create("http://localhost:8080/tasks/" + task1.getId());
        URI url3 = URI.create("http://localhost:8080/tasks/" + task2.getId());
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(task1Json)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(task2Json)).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpRequest request4 = HttpRequest.newBuilder().uri(url3).GET().build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response3.statusCode());
        Assertions.assertEquals(200, response4.statusCode());

        Task task1respond = CSVutils.jsonToTask(response3.body());
        Task task2respond = CSVutils.jsonToTask(response4.body());

        Assertions.assertEquals(task1, task1respond, "Задачи не равны");
        Assertions.assertEquals(task2, task2respond, "Задачи не равны");
    }

    @Test
    public void shouldReturnNotFoundCodeWhenGetTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + 1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldReturnSuccessCodeAfterWhenAddTask() throws IOException, InterruptedException {
        Task task = new Task("task1", "task1 description", taskManager.getId(), TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(90));
        String taskJson = CSVutils.taskToJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Task> tasksFromManagers = taskManager.getTasks();

        Assertions.assertNotNull(tasksFromManagers, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManagers.size(), "Некорректное количество задач");
        Assertions.assertEquals("task1", tasksFromManagers.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void shouldReturnFailCodeWhenTaskIsOverlap() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "task1 description", taskManager.getId(), TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(60));
        Task task2 = new Task("task2", "task2 description", taskManager.getId(), TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(90));
        String task1Json = CSVutils.taskToJson(task1);
        String task2Json = CSVutils.taskToJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task1Json)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task2Json)).build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response2.statusCode());

        List<Task> tasksFromManagers = taskManager.getTasks();

        Assertions.assertNotNull(tasksFromManagers, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManagers.size(), "Некорректное количество задач");
        Assertions.assertEquals("task1", tasksFromManagers.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void shouldReturnSuccessCodeAndUpdateTask() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "task1 description", taskManager.getId(),
                TaskStatus.NEW, LocalDateTime.of(2024, Month.AUGUST, 29,15,0),
                Duration.ofMinutes(60));
        Task taskUpdated = new Task("task1Updatedd", "task1 description", task1.getId(),
                TaskStatus.IN_PROGRESS, LocalDateTime.of(2024, Month.AUGUST, 30,15,0),
                Duration.ofMinutes(90));
        String task1Json = CSVutils.taskToJson(task1);
        String taskUpdatedJson = CSVutils.taskToJson(taskUpdated);

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/tasks");
        URI url2 = URI.create("http://localhost:8080/tasks/" + task1.getId());
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(task1Json)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(taskUpdatedJson)).build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response2.statusCode());


        Assertions.assertEquals(taskUpdated, taskManager.getTasks().getFirst(), "Задачи не равны");
    }

    @Test
    public void shouldReturnFailCodeWhenUpdateOverlapedTask() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "task1 description", taskManager.getId(),
                TaskStatus.NEW, LocalDateTime.of(2024, Month.AUGUST, 29,15,0),
                Duration.ofMinutes(60));
        Task task2 = new Task("task2", "task2 description", taskManager.getId(),
                TaskStatus.NEW, LocalDateTime.of(2024, Month.AUGUST, 30,15,0),
                Duration.ofMinutes(60));
        Task taskUpdated = new Task("task1Updated", "task1 description", task1.getId(),
                TaskStatus.IN_PROGRESS, LocalDateTime.of(2024, Month.AUGUST, 30,15,30),
                Duration.ofMinutes(90));
        String task1Json = CSVutils.taskToJson(task1);
        String task2Json = CSVutils.taskToJson(task2);
        String taskUpdatedJson = CSVutils.taskToJson(taskUpdated);

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/tasks");
        URI url2 = URI.create("http://localhost:8080/tasks/" + task1.getId());
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(task1Json)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(task2Json)).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(taskUpdatedJson)).build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response3.statusCode());
    }

    @Test
    public void shouldReturnSuccessCodeWhenDeleteTaskById() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "task1 description", taskManager.getId(),
                TaskStatus.NEW, LocalDateTime.of(2024, Month.AUGUST, 29,15,0),
                Duration.ofMinutes(60));
        Task task2 = new Task("task2", "task2 description", taskManager.getId(),
                TaskStatus.NEW, LocalDateTime.of(2024, Month.AUGUST, 30,15,0),
                Duration.ofMinutes(60));

        String task1Json = CSVutils.taskToJson(task1);
        String task2Json = CSVutils.taskToJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/tasks");
        URI url2 = URI.create("http://localhost:8080/tasks/" + 1);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(task1Json)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(task2Json)).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url2).DELETE().build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response3.statusCode());
    }

    @Test
    public void shouldReturnFailCodeWhenDeletedTaskIsAbsent() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/"  + 1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }
}
