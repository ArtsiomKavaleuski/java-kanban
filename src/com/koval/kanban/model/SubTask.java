package com.koval.kanban.model;

import com.koval.kanban.service.TaskStatus;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String description, int id, TaskStatus status, int epicId) {
        super(name, description, id, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void resetId() {this.id = -1;
    }

    @Override
    public String toString() {
        return "SubTask{name='" + super.name +
                "', description='" + super.description +
                "', id='" + super.id +
                "', status='" + super.status +
                "', epicId='" + epicId +
                "'}";
    }
}
