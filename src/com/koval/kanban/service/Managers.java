package com.koval.kanban.service;

public class Managers {
    public static TaskManager getDefault() {
        TaskManager tm = new InMemoryTaskManager();
        return tm;
    }

    public static HistoryManager getDefaultHistory() {
        HistoryManager hm = new InMemoryHistoryManager();
        return hm;
    }
}
