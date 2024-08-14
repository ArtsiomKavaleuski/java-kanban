package com.koval.kanban.service;

import com.koval.kanban.model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    @Test
    public void shouldAddTaskToHistory() {
        TaskManager tm = Managers.getDefault();
        HistoryManager hm = tm.getHm();
        Task task1 = new Task("Задача 1", "описание задачи 1", 0,
                TaskStatus.NEW,
                LocalDateTime.of(2024, Month.AUGUST, 14,15,0),
                Duration.ofMinutes(30));
        tm.addToTasks(task1);
        tm.getTaskById(task1.getId());
        assertEquals(hm.getHistory().get(0), task1, "задача не была добавлена в историю");

    }

    @Test
    public void shouldHaveNoDuplicates() {
        TaskManager tm = Managers.getDefault();
        HistoryManager hm = tm.getHm();
        Task task1 = new Task("Задача 1", "описание задачи 1", 0,
                TaskStatus.NEW,
                LocalDateTime.of(2024, Month.AUGUST, 14,15,0),
                Duration.ofMinutes(30));
        tm.addToTasks(task1);
        tm.getTaskById(task1.getId());
        tm.getTaskById(task1.getId());
        assertEquals(1, hm.getHistory().size(), "в истории дубликат просмотра задачи");

    }

    @Test
    public void shouldContainsOnlyLatestHistory() {
        TaskManager tm = Managers.getDefault();
        Task task1 = new Task("Задача 1", "описание задачи 1", 0,
                TaskStatus.NEW,
                LocalDateTime.of(2024, Month.AUGUST, 14,15,0),
                Duration.ofMinutes(30));
        tm.addToTasks(task1);
        Task taskToSee = tm.getTaskById(task1.getId());
        taskToSee = tm.getTaskById(task1.getId());
        HistoryManager hm = tm.getHm();
        assertEquals(hm.getHistory().getFirst(), task1, "задачи не равны");
        assertEquals(hm.getHistory().size(), 1, "задачи не равны");
    }

    @Test
    public void shouldSaveTheOrderOfHistoryWatching() {
        TaskManager tm = Managers.getDefault();
        Task task1 = new Task("task1", "task1description", 0, TaskStatus.NEW,
                LocalDateTime.of(2024, Month.AUGUST, 14,15,0),
                Duration.ofMinutes(30));
        Task task2 = new Task("task2", "task1description", 1, TaskStatus.NEW,
                LocalDateTime.of(2024, Month.AUGUST, 15,15,0),
                Duration.ofMinutes(30));
        Task task3 = new Task("task3", "task1description", 2, TaskStatus.NEW,
                LocalDateTime.of(2024, Month.AUGUST, 16,15,0),
                Duration.ofMinutes(30));
        tm.addToTasks(task1);
        tm.addToTasks(task2);
        tm.addToTasks(task3);
        tm.getTaskById(task1.getId());
        tm.getTaskById(task2.getId());
        tm.getTaskById(task3.getId());
        HistoryManager hm = tm.getHm();
        assertEquals(hm.getHistory().getLast(), task3, "порядок нарушен");
        assertEquals(hm.getHistory().get(1), task2, "порядок нарушен");
        assertEquals(hm.getHistory().get(0), task1, "порядок нарушен");
    }


}