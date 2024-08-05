package com.koval.kanban.service;

import java.io.File;

public class Managers {
    public static TaskManager getDefault() {
        TaskManager tm = new InMemoryTaskManager();
        return tm;
    }

    public static TaskManager getFileBackTaskManager(File file) {
        TaskManager fbTaskManager = new FileBackedTaskManager(file);
        return fbTaskManager;
    }

    public static HistoryManager getDefaultHistory() {
        HistoryManager hm = new InMemoryHistoryManager();
        return hm;
    }
}
