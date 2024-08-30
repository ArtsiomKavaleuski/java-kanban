package com.koval.kanban.model;

import com.koval.kanban.service.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private LocalDateTime endTime;
    private ArrayList<Integer> subTaskIdList = new ArrayList<>();

    public Epic(String name, String description, int id) {
        super(name, description, id, TaskStatus.NEW, null, Duration.ZERO);
        taskType = TaskTypes.EPIC;
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIdList;
    }

    public void addSubTaskId(int subTaskId) {
        this.subTaskIdList.add(subTaskId);
    }

    public void removeSubTaskId(int subTaskId) {
        subTaskIdList.remove(subTaskId);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Epic{name='" + super.name +
                "', description='" + super.description +
                "', id='" + super.id +
                "', status='" + super.status +
                "', subTaskIdList=" + subTaskIdList +
                "', startTime='" + startTime +
                "', duration='" + duration + "'}";
    }
}
