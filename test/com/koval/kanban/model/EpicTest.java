package com.koval.kanban.model;

import com.koval.kanban.service.CSVutils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    @Test
    public void theSameEpicsWithTheSameIDsShouldBeEqual() {
        String name = "Epic1";
        String description = "description1";
        int id = 0;
        Epic epic1 = new Epic(name, description, id);
        Epic epic2 = new Epic(name, description, id);
        assertEquals(CSVutils.taskToJson(epic1), CSVutils.taskToJson(epic2), "задачи не равны");
    }

}