package com.koval.kanban.service;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(FileBackedTaskManager fileBackedTaskManager) {
        super(fileBackedTaskManager);
    }
}
