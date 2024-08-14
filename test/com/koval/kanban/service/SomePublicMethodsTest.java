package com.koval.kanban.service;

import com.koval.kanban.model.Epic;
import com.koval.kanban.model.SubTask;
import com.koval.kanban.model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static com.koval.kanban.service.FileBackedTaskManager.loadFromFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SomePublicMethodsTest {

    @Test
    void shouldCalculateEpicStatusIfAllNew() {
        InMemoryTaskManager tm = new InMemoryTaskManager();
        Epic epic1 = new Epic("epic1", "epic1 description", 0);
        tm.addToEpics(epic1);
        SubTask subtask1 = new SubTask("subtask1", "subtask1 description", 1, TaskStatus.NEW,
                0,
                LocalDateTime.of(2024, Month.AUGUST, 15, 14, 0),
                Duration.ofMinutes(30));
        SubTask subtask2 = new SubTask("subtask1", "subtask1 description", 2, TaskStatus.NEW,
                0,
                LocalDateTime.of(2024, Month.AUGUST, 16, 14, 0),
                Duration.ofMinutes(30));
        tm.addToSubtasks(subtask1);
        tm.addToSubtasks(subtask2);
        Assertions.assertEquals(TaskStatus.NEW, tm.getEpicById(0).getStatus());

    }

    @Test
    void shouldCalculateEpicStatusIfAllDone() {
        InMemoryTaskManager tm = new InMemoryTaskManager();
        Epic epic1 = new Epic("epic1", "epic1 description", 0);
        tm.addToEpics(epic1);
        SubTask subtask1 = new SubTask("subtask1", "subtask1 description", 1, TaskStatus.DONE,
                0,
                LocalDateTime.of(2024, Month.AUGUST, 15, 14, 0),
                Duration.ofMinutes(30));
        SubTask subtask2 = new SubTask("subtask1", "subtask1 description", 2, TaskStatus.DONE,
                0,
                LocalDateTime.of(2024, Month.AUGUST, 16, 14, 0),
                Duration.ofMinutes(30));
        tm.addToSubtasks(subtask1);
        tm.addToSubtasks(subtask2);
        Assertions.assertEquals(TaskStatus.DONE, tm.getEpicById(0).getStatus());

    }

    @Test
    void shouldCalculateEpicStatusIfOneDoneAndOneNew() {
        InMemoryTaskManager tm = new InMemoryTaskManager();
        Epic epic1 = new Epic("epic1", "epic1 description", 0);
        tm.addToEpics(epic1);
        SubTask subtask1 = new SubTask("subtask1", "subtask1 description", 1, TaskStatus.DONE,
                0,
                LocalDateTime.of(2024, Month.AUGUST, 15, 14, 0),
                Duration.ofMinutes(30));
        SubTask subtask2 = new SubTask("subtask1", "subtask1 description", 2, TaskStatus.NEW,
                0,
                LocalDateTime.of(2024, Month.AUGUST, 16, 14, 0),
                Duration.ofMinutes(30));
        tm.addToSubtasks(subtask1);
        tm.addToSubtasks(subtask2);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, tm.getEpicById(0).getStatus());

    }

    @Test
    void shouldCalculateEpicStatusIfAllInProgress() {
        InMemoryTaskManager tm = new InMemoryTaskManager();
        Epic epic1 = new Epic("epic1", "epic1 description", 0);
        tm.addToEpics(epic1);
        SubTask subtask1 = new SubTask("subtask1", "subtask1 description", 1,
                TaskStatus.IN_PROGRESS,
                0,
                LocalDateTime.of(2024, Month.AUGUST, 15, 14, 0),
                Duration.ofMinutes(30));
        SubTask subtask2 = new SubTask("subtask1", "subtask1 description", 2,
                TaskStatus.IN_PROGRESS,
                0,
                LocalDateTime.of(2024, Month.AUGUST, 16, 14, 0),
                Duration.ofMinutes(30));
        tm.addToSubtasks(subtask1);
        tm.addToSubtasks(subtask2);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, tm.getEpicById(0).getStatus());

    }

    @Test
    void checkOfOverlap() {
        TaskManager tm = new InMemoryTaskManager();
        SubTask subTask1 = new SubTask("subTask1", "subtask1 description", 1, TaskStatus.NEW,
                0,
                LocalDateTime.of(2024, Month.AUGUST, 14, 10, 0),
                Duration.ofMinutes(120));
        SubTask subTask2 = new SubTask("subTask2", "subtask2 description", 2, TaskStatus.NEW,
                0,
                LocalDateTime.of(2024, Month.AUGUST, 14, 11, 0),
                Duration.ofMinutes(30));
        SubTask subTask3 = new SubTask("subTask2", "subtask2 description", 2, TaskStatus.NEW,
                0,
                LocalDateTime.of(2024, Month.AUGUST, 13, 11, 0),
                Duration.ofMinutes(30));
        Assertions.assertTrue(tm.isTasksOverlap(subTask1, subTask2));
        Assertions.assertFalse(tm.isTasksOverlap(subTask2, subTask3));
    }

    @Test
    public void shouldRemoveFromHistoryTaskInTheMiddleThatWasRemovedButSaveTheOrder() {
        TaskManager tm = Managers.getDefault();
        Task task1 = new Task("task1", "task1description", 0, TaskStatus.NEW,
                LocalDateTime.of(2024, Month.AUGUST, 14, 15, 0),
                Duration.ofMinutes(30));
        Task task2 = new Task("task2", "task1description", 1, TaskStatus.NEW,
                LocalDateTime.of(2024, Month.AUGUST, 15, 15, 0),
                Duration.ofMinutes(30));
        Task task3 = new Task("task3", "task1description", 2, TaskStatus.NEW,
                LocalDateTime.of(2024, Month.AUGUST, 16, 15, 0),
                Duration.ofMinutes(30));
        tm.addToTasks(task1);
        tm.addToTasks(task2);
        tm.addToTasks(task3);
        tm.getTaskById(task1.getId());
        tm.getTaskById(task2.getId());
        tm.getTaskById(task3.getId());
        tm.removeTaskById(task2.getId());
        HistoryManager hm = tm.getHm();
        assertEquals(hm.getHistory().get(1), task3, "порядок нарушен");
        assertEquals(hm.getHistory().get(0), task1, "порядок нарушен");
        assertFalse(hm.getHistory().contains(task2), "просмотр не был удаален из истории");
    }

    @Test
    public void shouldRemoveFromHistoryTaskInTheBeginningThatWasRemovedButSaveTheOrder() {
        TaskManager tm = Managers.getDefault();
        Task task1 = new Task("task1", "task1description", 0, TaskStatus.NEW,
                LocalDateTime.of(2024, Month.AUGUST, 14, 15, 0),
                Duration.ofMinutes(30));
        Task task2 = new Task("task2", "task1description", 1, TaskStatus.NEW,
                LocalDateTime.of(2024, Month.AUGUST, 15, 15, 0),
                Duration.ofMinutes(30));
        Task task3 = new Task("task3", "task1description", 2, TaskStatus.NEW,
                LocalDateTime.of(2024, Month.AUGUST, 16, 15, 0),
                Duration.ofMinutes(30));
        tm.addToTasks(task1);
        tm.addToTasks(task2);
        tm.addToTasks(task3);
        tm.getTaskById(task1.getId());
        tm.getTaskById(task2.getId());
        tm.getTaskById(task3.getId());
        tm.removeTaskById(task1.getId());
        HistoryManager hm = tm.getHm();
        assertEquals(hm.getHistory().get(1), task3, "порядок нарушен");
        assertEquals(hm.getHistory().get(0), task2, "порядок нарушен");
        assertFalse(hm.getHistory().contains(task1), "просмотр не был удаален из истории");
    }

    @Test
    public void shouldRemoveFromHistoryTaskInTheEndThatWasRemovedButSaveTheOrder() {
        TaskManager tm = Managers.getDefault();
        Task task1 = new Task("task1", "task1description", 0, TaskStatus.NEW,
                LocalDateTime.of(2024, Month.AUGUST, 14, 15, 0),
                Duration.ofMinutes(30));
        Task task2 = new Task("task2", "task1description", 1, TaskStatus.NEW,
                LocalDateTime.of(2024, Month.AUGUST, 15, 15, 0),
                Duration.ofMinutes(30));
        Task task3 = new Task("task3", "task1description", 2, TaskStatus.NEW,
                LocalDateTime.of(2024, Month.AUGUST, 16, 15, 0),
                Duration.ofMinutes(30));
        tm.addToTasks(task1);
        tm.addToTasks(task2);
        tm.addToTasks(task3);
        tm.getTaskById(task1.getId());
        tm.getTaskById(task2.getId());
        tm.getTaskById(task3.getId());
        tm.removeTaskById(task3.getId());
        HistoryManager hm = tm.getHm();
        assertEquals(hm.getHistory().get(1), task2, "порядок нарушен");
        assertEquals(hm.getHistory().get(0), task1, "порядок нарушен");
        assertFalse(hm.getHistory().contains(task3), "просмотр не был удаален из истории");
    }

    @Test
    void shouldThrowAnExceptionWhenSave() {

        try {
            File file = File.createTempFile("test", "load");
            TaskManager fb = new FileBackedTaskManager(file);
            Assertions.assertThrows(ManagerSaveException.class, fb::save);
            Task task1 = new Task("task1", "task1description", 0, TaskStatus.NEW,
                    LocalDateTime.of(2024, Month.AUGUST, 14, 15, 0),
                    Duration.ofMinutes(30));
            fb.addToTasks(task1);
            Assertions.assertDoesNotThrow(fb::save);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldThrowAnExceptionWhenLoadEmptyFile() {
        Assertions.assertThrows(ManagerSaveException.class, () -> {
            File file = File.createTempFile("test", "load");
            TaskManager fbTaskManager = loadFromFile(file);
        });
    }

}
