package com.koval.kanban.model;

import com.koval.kanban.service.TaskStatus;

import java.util.Objects;

public class Task {
    protected final String name;
    protected final String description;
    protected int id;
    protected TaskStatus status;

    public Task(String name, String description, int taskId, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.id = taskId;
        this.status = status;
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

    @Override
    public String toString() {
        return "Task{name='" + name +
                "', description='" + description +
                "', id='" + id +
                "', status='" + status + "'}";
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
        return Objects.hash(name, description, id, status);
    }
}

