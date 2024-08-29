package com.koval.kanban.service;

import com.koval.kanban.model.Task;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

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