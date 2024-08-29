package com.koval.kanban.service;

import com.koval.kanban.model.SubTask;
import com.koval.kanban.model.Task;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;

public class SubtaskHandler extends BaseHttpHandler {
    public SubtaskHandler(TaskManager fileBackedTaskManager) {
        super(fileBackedTaskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String[] path = httpExchange.getRequestURI().getPath().split("/");
        try {
            switch (method) {
                case "GET":
                    if (path.length == 2 && path[1].equals("subtasks")) {
                        List<? extends Task> subTasks = fileBackedTaskManager.getSubTasks().stream().toList();
                        String response = CSVutils.tasksListToJson(subTasks);
                        sendText(httpExchange, response);
                    } else if (path.length == 3 && path[1].equals("subtasks")
                            && !fileBackedTaskManager.getSubTasks().stream().filter(t -> t.getId() == Integer.parseInt(path[2])).toList().isEmpty()) {
                        int id = Integer.parseInt(path[2]);
                        SubTask subTask = fileBackedTaskManager.getSubTaskById(id);
                        String response = CSVutils.taskToJson(subTask);
                        sendText(httpExchange, response);
                    } else {
                        sendNotFound(httpExchange, "Задача с указанным ID не найдена.");
                    }
                    break;
                case "POST":
                    if (path.length == 2 && path[1].equals("subtasks")) {
                        InputStream inputStream = httpExchange.getRequestBody();
                        String requestSubTask = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        Task subTask = CSVutils.jsonToTask(requestSubTask);
                        if (fileBackedTaskManager.getSubTasks().stream().filter(t -> t.getId() == subTask.getId()).toList().isEmpty()) {
                            fileBackedTaskManager.addToSubtasks((SubTask) subTask);
                            if (!fileBackedTaskManager.getSubTasks().stream().filter(t -> t.equals(subTask)).toList().isEmpty()) {
                                httpExchange.sendResponseHeaders(201, 0);
                                httpExchange.close();
                            } else {
                                sendHasInteractions(httpExchange, "Время переданной задачи пересекается с другими.");
                            }
                        } else {
                            sendHasInteractions(httpExchange, "ID указанной заадачи уже существует.");
                        }
                    } else if (path.length == 3 && path[1].equals("subtasks")) {
                        int subTaskId = Integer.parseInt(path[2]);
                        String requestBody = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        SubTask subTask = (SubTask) CSVutils.jsonToTask(requestBody);
                        if (!fileBackedTaskManager.getSubTasks().stream().filter(t -> t.getId() == subTask.getId()).toList().isEmpty()) {
                            fileBackedTaskManager.updateSubTask(subTask);
                            if (!fileBackedTaskManager.getSubTasks().stream().filter(t -> t.equals(subTask)).toList().isEmpty()) {
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
                    if (path.length == 3 && path[1].equals("subtasks")) {
                        int subTaskId = Integer.parseInt(path[2]);
                        if (!fileBackedTaskManager.getSubTasks().stream().filter(t -> t.getId() == subTaskId).toList().isEmpty()) {
                            fileBackedTaskManager.removeTaskById(subTaskId);
                            sendText(httpExchange, "Подзадача успешно удалена.");
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