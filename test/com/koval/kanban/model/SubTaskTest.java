package com.koval.kanban.model;

import com.koval.kanban.service.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubTaskTest {
    @Test
    public void theSameSubTasksWithTheSameIDsShouldBeEqual() {
        String name = "SubTask1";
        String description = "description1";
        int id = 0;
        int epicId = 0;
        LocalDateTime dateTime = LocalDateTime.of(2024, Month.AUGUST, 15, 15,0);
        Duration duration = Duration.ofMinutes(30);
        TaskStatus taskStatus = TaskStatus.NEW;
        SubTask subTask1 = new SubTask(name, description, id, taskStatus, epicId, dateTime, duration);
        SubTask subTask2 = new SubTask(name, description, id, taskStatus, epicId, dateTime, duration);
        assertEquals(subTask1, subTask2, "задачи не равны");
    }

}