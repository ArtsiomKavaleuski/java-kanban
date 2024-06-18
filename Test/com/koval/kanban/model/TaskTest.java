package com.koval.kanban.model;

import com.koval.kanban.service.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TaskTest {

    @Test
    public void theSameTasksWithTheSameIDsShouldBeEqual() {
        String name = "Task1";
        String description = "description1";
        int id = 0;
        TaskStatus taskStatus = TaskStatus.NEW;
        Task task1 = new Task(name, description, id, taskStatus);
        Task task2 = new Task(name, description, id, taskStatus);
        assertEquals(task1, task2, "задачи не равны");
    }

    @Test
    public void taskCantBeEqualToEpicEvenIfTheSameId() {
        String name = "Task1";
        String description = "description1";
        int id = 0;
        TaskStatus taskStatus = TaskStatus.NEW;
        Task task1 = new Task(name, description, id, taskStatus);
        Epic epic1 = new Epic(name, description, id);
        assertNotEquals(task1, epic1, "задачи равны");
    }

    @Test
    public void taskCantBeEqualToSubTaskEvenIfTheSameId() {
        String name = "Task1";
        String description = "description1";
        int id = 0;
        TaskStatus taskStatus = TaskStatus.NEW;
        Task task1 = new Task(name, description, id, taskStatus);
        SubTask subTask1 = new SubTask(name, description, id, taskStatus, 0);
        assertNotEquals(task1, subTask1, "задачи равны");
    }

}