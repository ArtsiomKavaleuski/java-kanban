package com.koval.kanban.service;

import com.google.gson.Gson;
import com.koval.kanban.model.Task;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(FileBackedTaskManager fileBackedTaskManager) {
        super(fileBackedTaskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        List<Task> sortedTasks = fileBackedTaskManager.getPrioritizedTasks().stream().toList();
        Gson gson = new Gson();

        httpExchange.sendResponseHeaders(200, 0);
        httpExchange.close();
    }
}
