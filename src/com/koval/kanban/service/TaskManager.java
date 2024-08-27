package com.koval.kanban.service;

import com.koval.kanban.model.Epic;
import com.koval.kanban.model.SubTask;
import com.koval.kanban.model.Task;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    void addToTasks(Task task);

    void addToEpics(Epic epic);

    void addToSubtasks(SubTask subTask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    List<Task> getTasks();

    List<? extends Task> getEpics();

    List<? extends Task> getSubTasks();

    List<? extends Task> getSubTasksByEpic(int epicId);

    Task getTaskById(int id);

    Epic getEpicById(int id);

    SubTask getSubTaskById(int id);

    void removeTasks();

    void removeEpics();

    void removeSubtasks();

    void removeTaskById(int id);

    void removeAllTasks();

    int getId();

    HistoryManager getHm();

    void save() throws ManagerSaveException;

    TreeSet<Task> getPrioritizedTasks();

    <T extends Task> boolean isTasksOverlap(T task1, T task2);

}
