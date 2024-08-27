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

public class HttpTaskServer {
    public static void main(String[] args) throws IOException {
        File dir = new File("src/com/koval/kanban/resources");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "TaskManager.csv");
        FileBackedTaskManager fb = Managers.getFileBackTaskManager(file);
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/tasks", new TasksHandler(fb));
        httpServer.createContext("/subtasks", new SubtaskHandler(fb));
        httpServer.createContext("/epics", new EpicsHandler(fb));
        httpServer.createContext("/history", new HistoryHandler(fb));
        httpServer.createContext("/prioritized", new PrioritizedHandler(fb));
        httpServer.start();




        Task task1 = new Task("Задача 1", "описание задачи 1", fb.getId(), TaskStatus.NEW,
                LocalDateTime.of(2024, Month.AUGUST, 13, 13, 0),
                Duration.ofMinutes(60));
        Task task2 = new Task("Задача 2", "описание задачи 2", fb.getId(), TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024, Month.AUGUST, 13, 14, 30),
                Duration.ofMinutes(30));
        Task task3 = new Task("Задача 3", "описание задачи 3", fb.getId(), TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024, Month.AUGUST, 19, 9, 0),
                Duration.ofMinutes(120));
        fb.addToTasks(task1);
        fb.addToTasks(task2);
        fb.addToTasks(task3);

        Task newTask1 = new Task("Задача 1", "описание задачи 1", task1.getId(), TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024, Month.AUGUST, 13, 13, 0),
                Duration.ofMinutes(60));
        fb.updateTask(newTask1);


        Epic epic1 = new Epic("Эпик 1", "описание эпика 1", fb.getId());
        Epic epic2 = new Epic("Эпик 2", "описание эпика 2", fb.getId());
        fb.addToEpics(epic1);
        fb.addToEpics(epic2);

        SubTask subTask1 = new SubTask("Подзадача 1", "описание подзадачи 1", fb.getId(),
                TaskStatus.NEW, epic2.getId(),
                LocalDateTime.of(2024, Month.AUGUST, 14, 13, 0),
                Duration.ofMinutes(30));

        SubTask subTask2 = new SubTask("Подзадача 2", "описание подзадачи 2", fb.getId(),
                TaskStatus.NEW, epic1.getId(),
                LocalDateTime.of(2024, Month.AUGUST, 15, 13, 30),
                Duration.ofMinutes(30));
        SubTask subTask3 = new SubTask("Подзадача 3", "описание подзадачи 3", fb.getId(),
                TaskStatus.NEW, epic1.getId(),
                LocalDateTime.of(2024, Month.AUGUST, 15, 9, 0),
                Duration.ofMinutes(240));
        fb.addToSubtasks(subTask1);
        fb.addToSubtasks(subTask2);
        fb.addToSubtasks(subTask3);

        for (Task task : fb.getPrioritizedTasks()) {
            System.out.println(task);
        }
        System.out.println();

        SubTask testTask = new SubTask("Подзадача Test", "описание подзадачи Test", 7,
                TaskStatus.DONE, 3,
                LocalDateTime.of(2024, Month.AUGUST, 19, 11, 10),
                Duration.ofMinutes(60));
        fb.updateSubTask(testTask);

        for (Task task : fb.getPrioritizedTasks()) {
            System.out.println(task);
        }
        fb.getTaskById(2);
        fb.getTaskById(1);
        fb.getTaskById(0);
        fb.getEpicById(4);
        fb.getEpicById(3);
        fb.getSubTaskById(5);
        fb.getSubTaskById(6);

        System.out.println();
        System.out.println(CSVutils.taskToJson(task1));
        System.out.println(CSVutils.taskToJson(subTask1));
        System.out.println(CSVutils.taskToJson(epic1));

        System.out.println();
        System.out.println(CSVutils.JsonToTask(CSVutils.taskToJson(task1)));
        System.out.println(CSVutils.JsonToTask(CSVutils.taskToJson(subTask1)));
        System.out.println(CSVutils.JsonToTask(CSVutils.taskToJson(epic1)));
    }

}
