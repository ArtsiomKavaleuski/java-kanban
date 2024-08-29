package com.koval.kanban.service;

import com.koval.kanban.model.Epic;
import com.koval.kanban.model.SubTask;
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

public class HttpTaskManagerEpicsTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer;

    {
        try {
            taskServer = new HttpTaskServer(8080, taskManager);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpTaskManagerEpicsTest() throws IOException {
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
    public void shouldReturnEpicsListAndSuccessCode() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic1 description", taskManager.getId());
        Epic epic2 = new Epic("epic2", "epic2 description", taskManager.getId());
        String epic1Json = CSVutils.taskToJson(epic1);
        String epic2Json = CSVutils.taskToJson(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epic1Json)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epic2Json)).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response3.statusCode());

        List<Epic> epics = (List<Epic>) taskManager.getEpics();

        Assertions.assertNotNull(epics, "Задачи не возвращаются");
        Assertions.assertEquals(2, epics.size(), "Некорректное количество задач");
        Assertions.assertEquals("epic1", epics.get(0).getName(), "Некорректное имя задачи");
        Assertions.assertEquals("epic2", epics.get(1).getName(), "Некорректное имя задачи");
    }

    @Test
    public void shouldReturnEpicsByIdAndSuccessCode() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic1 description", taskManager.getId());
        Epic epic2 = new Epic("epic2", "epic2 description", taskManager.getId());
        String epic1Json = CSVutils.taskToJson(epic1);
        String epic2Json = CSVutils.taskToJson(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/epics");
        URI url2 = URI.create("http://localhost:8080/epics/" + epic1.getId());
        URI url3 = URI.create("http://localhost:8080/epics/" + epic2.getId());
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epic1Json)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epic2Json)).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpRequest request4 = HttpRequest.newBuilder().uri(url3).GET().build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response3.statusCode());
        Assertions.assertEquals(200, response4.statusCode());
    }

    @Test
    public void shouldReturnNotFoundCodeWhenGetEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + 1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldReturnEpicsSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic description", taskManager.getId());
        SubTask subtask1 = new SubTask("subtask1", "subtask1 description", taskManager.getId(),
                TaskStatus.NEW, 0,
                LocalDateTime.of(2024, Month.AUGUST, 26, 15,30), Duration.ofMinutes(90));
        String epic1Json = CSVutils.taskToJson(epic1);
        String subtask1Json = CSVutils.taskToJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/epics");
        URI url2 = URI.create("http://localhost:8080/subtasks");
        URI url3 = URI.create("http://localhost:8080/epics/" + epic1.getId() + "/subtasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epic1Json)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtask1Json)).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        SubTask subtaskFromManager = (SubTask) CSVutils.JsonToTask(response3.body()
                .replace("[", "").replace("]", ""));

        Assertions.assertEquals(200, response3.statusCode());
        Assertions.assertEquals(subtask1, subtaskFromManager);
    }

    @Test
    public void shouldReturnNotFoundCodeIfEpicsSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + 1 + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldReturnSuccessCodeAfterAddEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic1 description", taskManager.getId());
        String epic1Json = CSVutils.taskToJson(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epic1Json)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Epic> epics = (List<Epic>) taskManager.getEpics();

        Assertions.assertNotNull(epics, "Задачи не возвращаются");
        Assertions.assertEquals(1, epics.size(), "Некорректное количество задач");
        Assertions.assertEquals("epic1", epics.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void shouldReturnSuccessCodeWhenDeleteEpicById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic1 description", taskManager.getId());
        String epic1Json = CSVutils.taskToJson(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/epics");
        URI url2 = URI.create("http://localhost:8080/epics/" + epic1.getId());
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epic1Json)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).DELETE().build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url1).GET().build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response2.statusCode());
        Assertions.assertTrue(response3.body().equals("[]"));
    }

    @Test
    public void shouldReturnFailCodeWhenDeletedTaskIsAbsent() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/"  + 1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }
}
