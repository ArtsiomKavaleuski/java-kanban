package com.koval.kanban.service;

import com.koval.kanban.model.Task;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager fileBackedTaskManager) {
        super(fileBackedTaskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String[] path = httpExchange.getRequestURI().getPath().split("/");
        try {
            switch (method) {
                case "GET":
                    if (path.length == 2 && path[1].equals("history")) {
                        List<Task> history = fileBackedTaskManager.getHm().getHistory();
                        String text = CSVutils.tasksListToJson(history);
                        sendText(httpExchange, text);
                    } else {
                        httpExchange.sendResponseHeaders(500, 0);
                        httpExchange.close();
                    }
                    break;
                default:
                    httpExchange.sendResponseHeaders(500, 0);
                    httpExchange.close();
            }
        } catch (IOException e) {
            FileBackedTaskManager.getLog().log(Level.SEVERE, "Ошибка: ", e);
            httpExchange.sendResponseHeaders(500, 0);
            httpExchange.close();
        }
    }
}