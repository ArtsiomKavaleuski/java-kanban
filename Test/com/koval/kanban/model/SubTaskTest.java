package com.koval.kanban.model;

import com.koval.kanban.service.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    @Test
    public void theSameSubTasksWithTheSameIDsShouldBeEqual() {
        String name = "SubTask1";
        String description = "description1";
        int id = 0;
        int epicId = 0;
        TaskStatus taskStatus = TaskStatus.NEW;
        SubTask subTask1 = new SubTask(name, description, id, taskStatus, epicId);
        SubTask subTask2 = new SubTask(name, description, id, taskStatus, epicId);
        assertEquals(subTask1, subTask2, "задачи не равны");
    }

}