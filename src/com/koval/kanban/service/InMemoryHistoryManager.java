package com.koval.kanban.service;

import com.koval.kanban.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_LIST_CAPACITY = 10;

    List<Task> historyList = new ArrayList<>(HISTORY_LIST_CAPACITY);

    @Override
    public <T extends Task> void add(T task) {
        if (historyList.size() < HISTORY_LIST_CAPACITY) {
            historyList.addLast(task);
        } else {
            historyList.removeFirst();
            historyList.addLast(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
