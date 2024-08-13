package com.koval.kanban.service;

import com.koval.kanban.model.Task;

import java.util.Comparator;

public class TaskByDateComparator implements Comparator<Task> {

    @Override
    public int compare(Task o1, Task o2) {
        if (o1.equals(o2)) {
            return 0;
        } else if (o1.getStartTime().isAfter(o2.getStartTime())) {
            return 1;
        } else if (o1.getStartTime().isBefore(o2.getStartTime())) {
            return -1;
        } else if (o1.getStartTime().equals(o2.getStartTime())) {
            if (o1.getId() < o2.getId()) {
                return -1;
            }
        }
        return 1;

    }

}
