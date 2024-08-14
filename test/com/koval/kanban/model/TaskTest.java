package com.koval.kanban.model;

import com.koval.kanban.service.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TaskTest {

    @Test
    public void theSameTasksWithTheSameIDsShouldBeEqual() {
        String name = "Task1";
        String description = "description1";
        int id = 0;
        TaskStatus taskStatus = TaskStatus.NEW;
        LocalDateTime dateTime = LocalDateTime.of(2024, Month.AUGUST, 15, 15,0);
        Duration duration = Duration.ofMinutes(30);
        Task task1 = new Task(name, description, id, taskStatus, dateTime, duration);
        Task task2 = new Task(name, description, id, taskStatus, dateTime, duration);
        assertEquals(task1, task2, "задачи не равны");
    }

    @Test
    public void taskCantBeEqualToEpicEvenIfTheSameId() {
        String name = "Task1";
        String description = "description1";
        int id = 0;
        TaskStatus taskStatus = TaskStatus.NEW;
        LocalDateTime dateTime = LocalDateTime.of(2024, Month.AUGUST, 15, 15,0);
        Duration duration = Duration.ofMinutes(30);
        Task task1 = new Task(name, description, id, taskStatus, dateTime, duration);
        Epic epic1 = new Epic(name, description, id);
        assertNotEquals(task1, epic1, "задачи равны");
    }

    @Test
    public void taskCantBeEqualToSubTaskEvenIfTheSameId() {
        String name = "Task1";
        String description = "description1";
        int id = 0;
        TaskStatus taskStatus = TaskStatus.NEW;
        LocalDateTime dateTime = LocalDateTime.of(2024, Month.AUGUST, 15, 15,0);
        Duration duration = Duration.ofMinutes(30);
        Task task1 = new Task(name, description, id, taskStatus, dateTime, duration);
        SubTask subTask1 = new SubTask(name, description, id, taskStatus, 0, dateTime, duration);
        assertNotEquals(task1, subTask1, "задачи равны");
    }

}