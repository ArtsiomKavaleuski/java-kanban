package com.koval.kanban.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    public void shouldReturnExampleOfInMemoryTaskManagerClass() {
        TaskManager tm = Managers.getDefault();
        TaskManager tm1 = new InMemoryTaskManager();
        assertEquals(tm.getClass(), tm1.getClass(), "экземпляры не равны");
    }

    @Test
    public void shouldReturnInitialisedExampleOfInMemoryTaskManagerClassWithInitialisedHashMaps() {
        TaskManager tm = Managers.getDefault();
        assertNotNull(tm.getTasks());
        assertNotNull(tm.getEpics());
        assertNotNull(tm.getSubTasks());
    }

    @Test
    public void shouldReturnExampleOfInMemoryHistoryManagerClass() {
        HistoryManager hm = Managers.getDefaultHistory();
        HistoryManager hm1 = new InMemoryHistoryManager();
        assertEquals(hm.getClass(), hm1.getClass(), "экземпляры не равны");
    }

    @Test
    public void shouldReturnInitialisedListWhenInitialisedExampleOfInMemoryHistoryManagerClass() {
        HistoryManager hm = Managers.getDefaultHistory();
        assertNotNull(hm.getHistory());
    }
}