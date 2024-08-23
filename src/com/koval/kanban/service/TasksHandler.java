package com.koval.kanban.service;

public class TasksHandler extends BaseHttpHandler {

    public TasksHandler(FileBackedTaskManager fileBackedTaskManager) {
        super(fileBackedTaskManager);
    }
}
