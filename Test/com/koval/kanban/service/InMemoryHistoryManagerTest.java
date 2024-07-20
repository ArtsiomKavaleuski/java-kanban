package com.koval.kanban.service;

import com.koval.kanban.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    @Test
    public void shouldContainsOnlyLatestHistory() {
        TaskManager tm = Managers.getDefault();
        Task task1 = new Task("task1", "task1description", 0, TaskStatus.NEW);
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
        Task task1 = new Task("task1", "task1description", 0, TaskStatus.NEW);
        Task task2 = new Task("task2", "task1description", 1, TaskStatus.NEW);
        Task task3 = new Task("task3", "task1description", 2, TaskStatus.NEW);
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

    @Test
    public void shouldRemoveFromHistoryTaskThatWasRemovedButSaveTheOrder() {
        TaskManager tm = Managers.getDefault();
        Task task1 = new Task("task1", "task1description", 0, TaskStatus.NEW);
        Task task2 = new Task("task2", "task1description", 1, TaskStatus.NEW);
        Task task3 = new Task("task3", "task1description", 2, TaskStatus.NEW);
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
        assertEquals(hm.getHistory().contains(task2), false, "просмотр не был удаален из истории");
    }
}