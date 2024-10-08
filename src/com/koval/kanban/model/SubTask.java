package com.koval.kanban.model;

import com.koval.kanban.service.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private int epicId;

    public SubTask(String name, String description, int id, TaskStatus status, int epicId, LocalDateTime startTime, Duration duration) {
        super(name, description, id, status, startTime, duration);
        this.epicId = epicId;
        taskType = TaskTypes.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void resetId() {
        this.id = -1;
    }

    @Override
    public String toString() {
        return "SubTask{name='" + super.name +
                "', description='" + super.description +
                "', id='" + super.id +
                "', status='" + super.status +
                "', epicId='" + epicId +
                "', startTime='" + startTime +
                "', duration='" + duration + "'}";
    }
}
