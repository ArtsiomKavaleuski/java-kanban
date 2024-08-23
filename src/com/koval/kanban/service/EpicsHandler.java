package com.koval.kanban.service;

public class EpicsHandler extends BaseHttpHandler {
    public EpicsHandler(FileBackedTaskManager fileBackedTaskManager) {
        super(fileBackedTaskManager);
    }
}
