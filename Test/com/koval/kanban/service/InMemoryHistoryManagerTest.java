package com.koval.kanban.service;

import com.koval.kanban.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    public void shouldSaveTheOldTaskInHistory() {
        TaskManager tm = Managers.getDefault();
        Task task1 = new Task("task1", "task1description", 0, TaskStatus.NEW);
        Task task1upd = new Task("task1", "task1description", 0, TaskStatus.IN_PROGRESS);
        tm.addToTasks(task1);
        Task taskToSee = tm.getTaskById(task1.getId());
        tm.updateTask(task1upd);
        taskToSee = tm.getTaskById(task1.getId());
        HistoryManager hm = tm.getHm();
        assertEquals(hm.getHistory().getFirst(), task1, "задачи не равны");
        assertEquals(hm.getHistory().getLast(), task1upd, "задачи не равны");
    }
}