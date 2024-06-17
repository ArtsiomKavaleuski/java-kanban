package com.koval.kanban.service;

public class Managers {
    public static TaskManager getDefault() {
        TaskManager tm = new InMemoryTaskManager();
        return tm;
    }
}
