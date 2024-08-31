package com.koval.kanban.service;

import com.koval.kanban.model.Epic;
import com.koval.kanban.model.Task;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;

public class EpicsHandler extends BaseHttpHandler {
    public EpicsHandler(TaskManager fileBackedTaskManager) {
        super(fileBackedTaskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String[] path = httpExchange.getRequestURI().getPath().split("/");
        try {
            switch (method) {
                case "GET":
                    if (path.length == 2 && path[1].equals("epics")) {
                        List<? extends Task> epics = fileBackedTaskManager.getEpics();
                        String response = CSVutils.tasksListToJson(epics);
                        sendText(httpExchange, response);
                    } else if (path.length == 3 && path[1].equals("epics")
                            && !fileBackedTaskManager.getEpics().stream().filter(t -> t.getId() == Integer.parseInt(path[2])).toList().isEmpty()) {
                        int id = Integer.parseInt(path[2]);
                        Epic epic = fileBackedTaskManager.getEpicById(id);
                        String response = CSVutils.taskToJson(epic);
                        sendText(httpExchange, response);
                    } else if (path.length == 4 && path[1].equals("epics")
                            && !fileBackedTaskManager.getEpics().stream().filter(t -> t.getId() == Integer.parseInt(path[2])).toList().isEmpty() && path[3].equals("subtasks")) {
                        int id = Integer.parseInt(path[2]);
                        List<? extends Task> epicSubTasks = fileBackedTaskManager.getSubTasksByEpic(id);
                        String response = CSVutils.tasksListToJson(epicSubTasks);
                        sendText(httpExchange, response);
                    } else {
                        sendNotFound(httpExchange, "Эпик с указанным ID не найден.");
                    }
                    break;
                case "POST":
                    if (path.length == 2 && path[1].equals("epics")) {
                        InputStream inputStream = httpExchange.getRequestBody();
                        String requestEpic = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        Task epic = CSVutils.jsonToTask(requestEpic);
                        if (fileBackedTaskManager.getEpics().stream().filter(t -> t.getId() == epic.getId()).toList().isEmpty()) {
                            fileBackedTaskManager.addToEpics((Epic) epic);
                            if (!fileBackedTaskManager.getEpics().stream().filter(t -> t.equals(epic)).toList().isEmpty()) {
                                httpExchange.sendResponseHeaders(201, 0);
                                httpExchange.close();
                            } else {
                                sendHasInteractions(httpExchange, "Время переданной задачи пересекается с другими.");
                            }
                        } else {
                            sendHasInteractions(httpExchange, "ID указанного эпика занят.");
                        }
                    } else {
                        httpExchange.sendResponseHeaders(500, 0);
                        httpExchange.close();
                    }
                    break;
                case "DELETE":
                    if (path.length == 3 && path[1].equals("epics")) {
                        int id = Integer.parseInt(path[2]);
                        if (!fileBackedTaskManager.getEpics().stream().filter(t -> t.getId() == id).toList().isEmpty()) {
                            fileBackedTaskManager.removeTaskById(id);
                            sendText(httpExchange, "Задача успешно удалена.");
                        } else {
                            sendNotFound(httpExchange, "Эпика с указанным ID не существует. Поэтому он не может быть удален.");
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