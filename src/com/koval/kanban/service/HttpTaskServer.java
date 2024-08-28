package com.koval.kanban.service;

import com.koval.kanban.model.Epic;
import com.koval.kanban.model.SubTask;
import com.koval.kanban.model.Task;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.logging.Level;

public class HttpTaskServer {
    FileBackedTaskManager fileBackedTaskManager;
    HttpServer httpServer;
    int port;

    public HttpTaskServer(int port, FileBackedTaskManager fileBackedTaskManager) throws IOException {

        this.fileBackedTaskManager = fileBackedTaskManager;
        this.port = port;

        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/tasks", new TasksHandler(fileBackedTaskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(fileBackedTaskManager));
        httpServer.createContext("/epics", new EpicsHandler(fileBackedTaskManager));
        httpServer.createContext("/history", new HistoryHandler(fileBackedTaskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(fileBackedTaskManager));
        httpServer.start();
    }

    public static void main(String[] args) throws IOException {
        try {
            File dir = new File("src/com/koval/kanban/resources");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, "TaskManager.csv");
            HttpTaskServer httpTaskServer = new HttpTaskServer(8080, Managers.getTestFileBackTaskManager(file));
        }  catch (IOException e) {
            FileBackedTaskManager.getLog().log(Level.SEVERE, "Ошибка: ", e);
        }
    }
}
