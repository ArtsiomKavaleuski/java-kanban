package com.koval.kanban.service;

import com.koval.kanban.model.Epic;
import com.koval.kanban.model.SubTask;
import com.koval.kanban.model.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTaskManager getFileBackTaskManager(File file) {
        return new FileBackedTaskManager(file);
    }

    public static FileBackedTaskManager getTestFileBackTaskManager(File file) throws ManagerSaveException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        Task task1 = new Task("Задача 1", "описание задачи 1", fileBackedTaskManager.getId(), TaskStatus.NEW,
                LocalDateTime.of(2024, Month.AUGUST, 1, 9, 0),
                Duration.ofMinutes(60));
        Task task2 = new Task("Задача 2", "описание задачи 2", fileBackedTaskManager.getId(), TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024, Month.AUGUST, 1, 10, 30),
                Duration.ofMinutes(30));
        Task task3 = new Task("Задача 3", "описание задачи 3", fileBackedTaskManager.getId(), TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024, Month.AUGUST, 19, 9, 0),
                Duration.ofMinutes(120));
        fileBackedTaskManager.addToTasks(task1);
        fileBackedTaskManager.addToTasks(task2);
        fileBackedTaskManager.addToTasks(task3);

        Epic epic1 = new Epic("Эпик 1", "описание эпика 1", fileBackedTaskManager.getId());
        Epic epic2 = new Epic("Эпик 2", "описание эпика 2", fileBackedTaskManager.getId());
        fileBackedTaskManager.addToEpics(epic1);
        fileBackedTaskManager.addToEpics(epic2);

        SubTask subTask1 = new SubTask("Подзадача 1", "описание подзадачи 1", fileBackedTaskManager.getId(),
                TaskStatus.NEW, epic2.getId(),
                LocalDateTime.of(2024, Month.AUGUST, 14, 13, 0),
                Duration.ofMinutes(30));
        SubTask subTask2 = new SubTask("Подзадача 2", "описание подзадачи 2", fileBackedTaskManager.getId(),
                TaskStatus.NEW, epic1.getId(),
                LocalDateTime.of(2024, Month.AUGUST, 15, 13, 30),
                Duration.ofMinutes(30));
        SubTask subTask3 = new SubTask("Подзадача 3", "описание подзадачи 3", fileBackedTaskManager.getId(),
                TaskStatus.NEW, epic1.getId(),
                LocalDateTime.of(2024, Month.AUGUST, 15, 9, 0),
                Duration.ofMinutes(240));
        fileBackedTaskManager.addToSubtasks(subTask1);
        fileBackedTaskManager.addToSubtasks(subTask2);
        fileBackedTaskManager.addToSubtasks(subTask3);

        fileBackedTaskManager.getTaskById(0);
        fileBackedTaskManager.getTaskById(1);
        fileBackedTaskManager.getTaskById(2);
        fileBackedTaskManager.getEpicById(3);
        fileBackedTaskManager.getEpicById(4);
        fileBackedTaskManager.getSubTaskById(5);
        fileBackedTaskManager.getSubTaskById(6);
        fileBackedTaskManager.getSubTaskById(7);

        return fileBackedTaskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
