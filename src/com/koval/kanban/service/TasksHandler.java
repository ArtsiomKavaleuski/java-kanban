package com.koval.kanban.service;

import com.koval.kanban.model.Task;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;

public class TasksHandler extends BaseHttpHandler {
    public TasksHandler(TaskManager fileBackedTaskManager) {
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
                        List<Task> tasks = fileBackedTaskManager.getTasks();
                        String response = CSVutils.tasksListToJson(tasks);
                        sendText(httpExchange, response);
                    } else if (path.length == 3 && path[1].equals("tasks")
                            && !fileBackedTaskManager.getTasks().stream().filter(t -> t.getId() == Integer.parseInt(path[2])).toList().isEmpty()) {
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
                        Task task = CSVutils.jsonToTask(requestTask);
                        if (fileBackedTaskManager.getTasks().stream().filter(t -> t.getId() == task.getId()).toList().isEmpty()) {
                            fileBackedTaskManager.addToTasks(task);
                            if (fileBackedTaskManager.getTasks().contains(task)) {
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
                        Task task = CSVutils.jsonToTask(requestBody);
                        if (!fileBackedTaskManager.getTasks().stream().filter(t -> t.getId() == task.getId()).toList().isEmpty()) {
                            fileBackedTaskManager.updateTask(task);
                            if (fileBackedTaskManager.getTaskById(taskId).equals(task)) {
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
                        if (!fileBackedTaskManager.getTasks().stream().filter(t -> t.getId() == taskId).toList().isEmpty()) {
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