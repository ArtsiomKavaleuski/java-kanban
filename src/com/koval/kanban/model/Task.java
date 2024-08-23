package com.koval.kanban.model;

import com.koval.kanban.service.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected int id;
    protected TaskTypes taskType = TaskTypes.TASK;
    protected final String name;
    protected TaskStatus status;
    protected final String description;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(String name, String description, int taskId, TaskStatus status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.id = taskId;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        return startTime == null ? null : startTime.plusMinutes(duration.toMinutes());
    }

    @Override
    public String toString() {
        return "Task{name='" + name +
                "', description='" + description +
                "', id='" + id +
                "', status='" + status +
                "', startTime='" + startTime +
                "', duration='" + duration + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description,
                task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(description);
        result = 31 * result + id;
        result = 31 * result + Objects.hashCode(status);
        result = 31 * result + Objects.hashCode(startTime);
        result = 31 * result + Objects.hashCode(duration);
        return result;
    }
}

