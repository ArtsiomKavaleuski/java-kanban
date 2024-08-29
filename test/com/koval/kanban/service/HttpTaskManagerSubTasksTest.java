package com.koval.kanban.service;

import com.koval.kanban.model.Epic;
import com.koval.kanban.model.SubTask;
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

public class HttpTaskManagerSubTasksTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer;

    {
        try {
            taskServer = new HttpTaskServer(8080, taskManager);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpTaskManagerSubTasksTest() throws IOException {
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
    public void shouldReturnSubTasksListAndSuccessCode() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic description", taskManager.getId());
        SubTask subtask1 = new SubTask("subtask1", "subtask1 description", taskManager.getId(), TaskStatus.NEW, 0,
                LocalDateTime.of(2024, Month.AUGUST, 26, 15,30), Duration.ofMinutes(90));
        SubTask subtask2 = new SubTask("subtask2", "subtask2 description", taskManager.getId(), TaskStatus.NEW, 0,
                LocalDateTime.of(2024, Month.AUGUST, 27, 15,30), Duration.ofMinutes(90));
        String epicToJson = CSVutils.taskToJson(epic1);
        String subtask1Json = CSVutils.taskToJson(subtask1);
        String subtask2Json = CSVutils.taskToJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url0 = URI.create("http://localhost:8080/epics");
        URI url1 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url0).POST(HttpRequest.BodyPublishers.ofString(epicToJson)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(subtask1Json)).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(subtask2Json)).build();
        HttpRequest request4 = HttpRequest.newBuilder().uri(url1).GET().build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response4.statusCode());

        List<SubTask> subTasks = (List<SubTask>) taskManager.getSubTasks();

        Assertions.assertNotNull(subTasks, "Задачи не возвращаются");
        Assertions.assertEquals(2, subTasks.size(), "Некорректное количество задач");
        Assertions.assertEquals("subtask1", subTasks.get(0).getName(), "Некорректное имя задачи");
        Assertions.assertEquals("subtask2", subTasks.get(1).getName(), "Некорректное имя задачи");
    }

    @Test
    public void shouldReturnSubTaskByIdAndSuccessCode() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic description", taskManager.getId());
        SubTask subtask1 = new SubTask("subtask1", "subtask1 description", taskManager.getId(),
                TaskStatus.NEW, 0,
                LocalDateTime.of(2024, Month.AUGUST, 26, 15,30), Duration.ofMinutes(90));
        SubTask subtask2 = new SubTask("subtask2", "subtask2 description", taskManager.getId(),
                TaskStatus.NEW, 0,
                LocalDateTime.of(2024, Month.AUGUST, 27, 15,30), Duration.ofMinutes(90));
        String epicToJson = CSVutils.taskToJson(epic1);
        String subtask1Json = CSVutils.taskToJson(subtask1);
        String subtask2Json = CSVutils.taskToJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url0 = URI.create("http://localhost:8080/epics");
        URI url1 = URI.create("http://localhost:8080/subtasks");
        URI url2 = URI.create("http://localhost:8080/subtasks/" + subtask1.getId());
        URI url3 = URI.create("http://localhost:8080/subtasks/" + subtask2.getId());
        HttpRequest request1 = HttpRequest.newBuilder().uri(url0).POST(HttpRequest.BodyPublishers.ofString(epicToJson)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(subtask1Json)).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(subtask2Json)).build();
        HttpRequest request4 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpRequest request5 = HttpRequest.newBuilder().uri(url3).GET().build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response4.statusCode());
        Assertions.assertEquals(200, response5.statusCode());

        Task subtask1respond = CSVutils.JsonToTask(response4.body());
        Task subtask2respond = CSVutils.JsonToTask(response5.body());

        Assertions.assertEquals(subtask1, subtask1respond, "Задачи не равны");
        Assertions.assertEquals(subtask2, subtask2respond, "Задачи не равны");
    }

    @Test
    public void shouldReturnNotFoundCodeWhenGetSubTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/subtasks/" + 1);
        HttpRequest request3 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response3.statusCode());
    }

    @Test
    public void shouldReturnSuccessCodeAfterAddSubTask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic description", taskManager.getId());
        SubTask subtask1 = new SubTask("subtask1", "subtask1 description", taskManager.getId(),
                TaskStatus.NEW, 0,
                LocalDateTime.of(2024, Month.AUGUST, 26, 15,30), Duration.ofMinutes(90));

        String epicToJson = CSVutils.taskToJson(epic1);
        String subtask1Json = CSVutils.taskToJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url0 = URI.create("http://localhost:8080/epics");
        URI url1 = URI.create("http://localhost:8080/subtasks");
        URI url2 = URI.create("http://localhost:8080/subtasks/" + subtask1.getId());
        HttpRequest request1 = HttpRequest.newBuilder().uri(url0).POST(HttpRequest.BodyPublishers.ofString(epicToJson)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(subtask1Json)).build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response2.statusCode());

        List<SubTask> subTasks = (List<SubTask>) taskManager.getSubTasks();

        Assertions.assertNotNull(subTasks, "Задачи не возвращаются");
        Assertions.assertEquals(1, subTasks.size(), "Некорректное количество задач");
        Assertions.assertEquals("subtask1", subTasks.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void shouldReturnFailCodeWhenTaskIsOverlap() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic description", taskManager.getId());
        SubTask subtask1 = new SubTask("subtask1", "subtask1 description", taskManager.getId(),
                TaskStatus.NEW, 0,
                LocalDateTime.of(2024, Month.AUGUST, 26, 15,30), Duration.ofMinutes(90));
        SubTask subtask2 = new SubTask("subtask2", "subtask2 description", taskManager.getId(),
                TaskStatus.NEW, 0,
                LocalDateTime.of(2024, Month.AUGUST, 27, 15,30), Duration.ofMinutes(90));
        SubTask subtask3 = new SubTask("subtask3", "subtask3 description", subtask2.getId(),
                TaskStatus.NEW, 0,
                LocalDateTime.of(2024, Month.AUGUST, 26, 16,30), Duration.ofMinutes(90));
        String epicToJson = CSVutils.taskToJson(epic1);
        String subtask1Json = CSVutils.taskToJson(subtask1);
        String subtask2Json = CSVutils.taskToJson(subtask2);
        String subtask3Json = CSVutils.taskToJson(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI url0 = URI.create("http://localhost:8080/epics");
        URI url1 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url0).POST(HttpRequest.BodyPublishers.ofString(epicToJson)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(subtask1Json)).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(subtask2Json)).build();
        HttpRequest request4 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(subtask3Json)).build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response4.statusCode());
    }

    @Test
    public void shouldReturnSuccessCodeAndUpdateTask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic description", taskManager.getId());
        SubTask subtask1 = new SubTask("subtask1", "subtask1 description", taskManager.getId(),
                TaskStatus.NEW, 0,
                LocalDateTime.of(2024, Month.AUGUST, 26, 15,30), Duration.ofMinutes(90));
        SubTask subtask2 = new SubTask("subtask2", "subtask2 description", taskManager.getId(),
                TaskStatus.NEW, 0,
                LocalDateTime.of(2024, Month.AUGUST, 27, 15,30), Duration.ofMinutes(90));
        SubTask subtask3 = new SubTask("subtask3", "subtask3 description", subtask2.getId(),
                TaskStatus.NEW, 0,
                LocalDateTime.of(2024, Month.AUGUST, 28, 16,30), Duration.ofMinutes(90));
        String epicToJson = CSVutils.taskToJson(epic1);
        String subtask1Json = CSVutils.taskToJson(subtask1);
        String subtask2Json = CSVutils.taskToJson(subtask2);
        String subtask3Json = CSVutils.taskToJson(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI url0 = URI.create("http://localhost:8080/epics");
        URI url1 = URI.create("http://localhost:8080/subtasks");
        URI url2 = URI.create("http://localhost:8080/subtasks/" + subtask2.getId());
        HttpRequest request1 = HttpRequest.newBuilder().uri(url0).POST(HttpRequest.BodyPublishers.ofString(epicToJson)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(subtask1Json)).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(subtask2Json)).build();
        HttpRequest request4 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtask3Json)).build();
        HttpRequest request5 = HttpRequest.newBuilder().uri(url2).GET().build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response4.statusCode());
        Assertions.assertEquals(subtask3, CSVutils.JsonToTask(response5.body()), "Задача не была изменена.");
    }

    @Test
    public void shouldReturnFailCodeWhenUpdateOverlapedTask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic description", taskManager.getId());
        SubTask subtask1 = new SubTask("subtask1", "subtask1 description", taskManager.getId(),
                TaskStatus.NEW, 0,
                LocalDateTime.of(2024, Month.AUGUST, 26, 15,30), Duration.ofMinutes(90));
        SubTask subtask2 = new SubTask("subtask2", "subtask2 description", taskManager.getId(),
                TaskStatus.NEW, 0,
                LocalDateTime.of(2024, Month.AUGUST, 27, 15,30), Duration.ofMinutes(90));
        SubTask subtask3 = new SubTask("subtask3", "subtask3 description", subtask2.getId(),
                TaskStatus.NEW, 0,
                LocalDateTime.of(2024, Month.AUGUST, 26, 16,30), Duration.ofMinutes(90));
        String epicToJson = CSVutils.taskToJson(epic1);
        String subtask1Json = CSVutils.taskToJson(subtask1);
        String subtask2Json = CSVutils.taskToJson(subtask2);
        String subtask3Json = CSVutils.taskToJson(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI url0 = URI.create("http://localhost:8080/epics");
        URI url1 = URI.create("http://localhost:8080/subtasks");
        URI url2 = URI.create("http://localhost:8080/subtasks/" + subtask2.getId());
        HttpRequest request1 = HttpRequest.newBuilder().uri(url0).POST(HttpRequest.BodyPublishers.ofString(epicToJson)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(subtask1Json)).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(subtask2Json)).build();
        HttpRequest request4 = HttpRequest.newBuilder().uri(url2).POST(HttpRequest.BodyPublishers.ofString(subtask3Json)).build();
        HttpRequest request5 = HttpRequest.newBuilder().uri(url2).GET().build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response4.statusCode());
        Assertions.assertEquals(subtask2, CSVutils.JsonToTask(response5.body()), "Задача была изменена.");
    }

    @Test
    public void shouldReturnSuccessCodeWhenDeleteSubTaskById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("epic1", "epic description", taskManager.getId());
        SubTask subtask1 = new SubTask("subtask1", "subtask1 description", taskManager.getId(),
                TaskStatus.NEW, 0,
                LocalDateTime.of(2024, Month.AUGUST, 26, 15,30), Duration.ofMinutes(90));

        String epicToJson = CSVutils.taskToJson(epic1);
        String subtask1Json = CSVutils.taskToJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url0 = URI.create("http://localhost:8080/epics");
        URI url1 = URI.create("http://localhost:8080/subtasks");
        URI url2 = URI.create("http://localhost:8080/subtasks/" + subtask1.getId());
        HttpRequest request1 = HttpRequest.newBuilder().uri(url0).POST(HttpRequest.BodyPublishers.ofString(epicToJson)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(subtask1Json)).build();
        HttpRequest request3 = HttpRequest.newBuilder().uri(url2).DELETE().build();
        HttpRequest request4 = HttpRequest.newBuilder().uri(url1).GET().build();

        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response3.statusCode());
        Assertions.assertTrue(response4.body().equals("[]"));
    }

    @Test
    public void shouldReturnFailCodeWhenDeletedTaskIsAbsent() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + 1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }







}
