package com.koval.kanban.service;

import com.koval.kanban.model.Task;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
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