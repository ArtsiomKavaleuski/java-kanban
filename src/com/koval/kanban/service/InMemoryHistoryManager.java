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
        historyMap.computeIfPresent(task.getId(), (a, b) -> b = task);
        historyMap.putIfAbsent(task.getId(), task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyMap.values());
    }

    @Override
    public void remove(int id) {
        historyMap.remove(id);
    }
}
