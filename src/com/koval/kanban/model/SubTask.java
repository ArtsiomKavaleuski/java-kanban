package com.koval.kanban.model;

import com.koval.kanban.service.TaskStatus;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String name, String description, int id, TaskStatus status, int epicId) {
        super(name, description, id, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "com.koval.kanban.model.SubTask{name='" + super.name +
                "', description='" + super.description +
                "', id='" + super.id +
                "', status='" + super.status +
                "', epicId='" + epicId +
                "'}";
    }
}