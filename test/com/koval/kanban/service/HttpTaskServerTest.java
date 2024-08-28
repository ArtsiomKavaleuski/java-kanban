package com.koval.kanban.service;

import java.io.IOException;

public class HttpTaskServerTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer;

    {
        try {
            taskServer = new HttpTaskServer(8080, taskManager);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
