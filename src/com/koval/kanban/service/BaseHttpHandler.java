package com.koval.kanban.service;

import com.koval.kanban.model.Epic;
import com.koval.kanban.model.SubTask;
import com.koval.kanban.model.Task;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;

public class BaseHttpHandler implements HttpHandler {
    public TaskManager fileBackedTaskManager;

    public BaseHttpHandler(TaskManager fileBackedTaskManager) {
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

class SubtaskHandler extends BaseHttpHandler {
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

class EpicsHandler extends BaseHttpHandler {
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

class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager fileBackedTaskManager) {
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
    public PrioritizedHandler(TaskManager fileBackedTaskManager) {
        super(fileBackedTaskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        List<Task> sortedTasks = fileBackedTaskManager.getPrioritizedTasks().stream().toList();
        String text = CSVutils.tasksListToJson(sortedTasks);
        sendText(httpExchange, text);
    }
}

