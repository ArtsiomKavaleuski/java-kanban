package com.koval.kanban.service;

import com.koval.kanban.model.Task;

import java.util.List;

public interface HistoryManager {
    <T extends Task> void add(T task);

    List<Task> getHistory();
}
