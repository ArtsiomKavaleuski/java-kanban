package com.koval.kanban.service;

import com.koval.kanban.model.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    Map<Integer, Task> historyMap = new LinkedHashMap<>();

    @Override

    public <T extends Task> void add(T task) {
        if (historyMap.containsKey(task.getId())) {
            historyMap.remove(task.getId());
            historyMap.put(task.getId(), task);
        } else {
            historyMap.put(task.getId(), task);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>(historyMap.values());
        return historyList;
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            historyMap.remove(id);
        }
    }
}
