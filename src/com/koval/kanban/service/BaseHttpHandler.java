package com.koval.kanban.service;

import com.koval.kanban.model.Task;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class BaseHttpHandler implements HttpHandler {
    public FileBackedTaskManager fileBackedTaskManager;

    public BaseHttpHandler(FileBackedTaskManager fileBackedTaskManager) {
        this.fileBackedTaskManager = fileBackedTaskManager;
    }

    protected void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(200, response.length);
        httpExchange.getResponseBody().write(response);
        httpExchange.close();
    }

    protected void sendNotFound(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(404, response.length);
        httpExchange.getResponseBody().write(response);
        httpExchange.close();
    }

    protected void sendHasInteractions(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(406, response.length);
        httpExchange.getResponseBody().write(response);
        httpExchange.close();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(200, 0);
        httpExchange.close();
    }

}

class TasksHandler extends BaseHttpHandler {
    public TasksHandler(FileBackedTaskManager fileBackedTaskManager) {
        super(fileBackedTaskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String[] path = httpExchange.getRequestURI().getPath().split("/");
        try {
            switch (method) {
                case "GET":
                    if (path.length == 2 && path[1].equals("tasks")) {
                        ArrayList<Task> tasks = fileBackedTaskManager.getTasks();
                        String response = CSVutils.tasksListToJson(tasks);
                        sendText(httpExchange, response);
                    } else if (path.length == 3 && path[1].equals("tasks") && fileBackedTaskManager.tasks.containsKey(Integer.parseInt(path[2]))) {
                        int id = Integer.parseInt(path[2]);
                        Task task = fileBackedTaskManager.getTaskById(id);
                        String response = CSVutils.taskToJson(task);
                        sendText(httpExchange, response);
                    } else {
                        sendNotFound(httpExchange, "Задача с указанным ID не найдена.");
                    }
                    break;
                case "POST":
                    if (path.length == 2 && path[1].equals("tasks")) {
                        InputStream inputStream = httpExchange.getRequestBody();
                        String requestTask = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        Task task = CSVutils.JsonToTask(requestTask);
                        if (!fileBackedTaskManager.tasks.containsKey(task.getId())) {
                            fileBackedTaskManager.addToTasks(task);
                            if (fileBackedTaskManager.tasks.containsKey(task.getId())) {
                                httpExchange.sendResponseHeaders(201, 0);
                                httpExchange.close();
                            } else {
                                sendHasInteractions(httpExchange, "Время переданной задачи пересекается с другими.");
                            }
                        } else {
                            sendHasInteractions(httpExchange, "ID указанной заадачи уже существует.");
                        }
                    } else if (path.length == 3 && path[1].equals("tasks")) {
                        int taskId = Integer.parseInt(path[2]);
                        String requestBody = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Task task = CSVutils.JsonToTask(requestBody);
                        if (fileBackedTaskManager.tasks.containsKey(taskId)) {
                            fileBackedTaskManager.updateTask(task);
                            if (fileBackedTaskManager.tasks.get(taskId).equals(task)) {
                                httpExchange.sendResponseHeaders(201, 0);
                                httpExchange.close();
                            } else {
                                sendHasInteractions(httpExchange, "Новое время переданной задачи пересекается с другими.");
                            }
                        } else {
                            sendNotFound(httpExchange, "Задачи с указанным ID не существует. Поэтому она не может быть обновлена");
                        }
                    } else {
                        httpExchange.sendResponseHeaders(500, 0);
                        httpExchange.close();
                    }
                    break;
                case "DELETE":
                    if (path.length == 3 && path[1].equals("tasks")) {
                            int taskId = Integer.parseInt(path[2]);
                            if (fileBackedTaskManager.tasks.containsKey(taskId)) {
                                fileBackedTaskManager.removeTaskById(taskId);
                                sendText(httpExchange, "Задача успешно удалена.");
                            } else {
                                sendNotFound(httpExchange, "Задачи с указанным ID не существует. Поэтому она не может быть удалена.");
                            }
                    }
                    break;
                default:
                    httpExchange.sendResponseHeaders(500, 0);
                    httpExchange.close();
            }
        } catch (NumberFormatException e) {
            FileBackedTaskManager.getLog().log(Level.SEVERE, "Ошибка: ", e);
            httpExchange.sendResponseHeaders(500, 0);
            httpExchange.close();
        }
    }
}


class EpicsHandler extends BaseHttpHandler {
    public EpicsHandler(FileBackedTaskManager fileBackedTaskManager) {
        super(fileBackedTaskManager);
    }
}

class SubtaskHandler extends BaseHttpHandler {
    public SubtaskHandler(FileBackedTaskManager fileBackedTaskManager) {
        super(fileBackedTaskManager);
    }
}

class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(FileBackedTaskManager fileBackedTaskManager) {
        super(fileBackedTaskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        List<Task> history = fileBackedTaskManager.getHm().getHistory();
        String text = CSVutils.tasksListToJson(history);
        sendText(httpExchange, text);
    }
}

class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(FileBackedTaskManager fileBackedTaskManager) {
        super(fileBackedTaskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        List<Task> sortedTasks = fileBackedTaskManager.getPrioritizedTasks().stream().toList();
        String text = CSVutils.tasksListToJson(sortedTasks);
        sendText(httpExchange, text);
    }
}

