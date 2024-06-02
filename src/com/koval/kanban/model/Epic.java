package com.koval.kanban.model;

import com.koval.kanban.service.TaskStatus;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIdList = new ArrayList<>();

    public Epic(String name, String description, int id) {
        super(name, description, id, TaskStatus.NEW);
    }

    public Epic(String name, String description, int id, ArrayList<Integer> subTaskIdList) {
        super(name, description, id, TaskStatus.NEW);
        this.subTaskIdList = subTaskIdList;
    }

    public ArrayList<Integer> getSubTaskIdList() {
        return subTaskIdList;
    }

    public void addSubTaskId(int subTaskId) {
        this.subTaskIdList.add(subTaskId);
    }

    @Override
    public String toString() {
        return "com.koval.kanban.model.Epic{name='" + super.name +
                "', description='" + super.description +
                "', id='" + super.id +
                "', status='" + super.status +
                "', subTaskIdList=" + subTaskIdList +
                "}";
    }
}
