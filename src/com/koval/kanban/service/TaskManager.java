package com.koval.kanban.service;

import com.koval.kanban.model.Epic;
import com.koval.kanban.model.SubTask;
import com.koval.kanban.model.Task;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    void addToTasks(Task task) throws ManagerSaveException;

    void addToEpics(Epic epic);

    void addToSubtasks(SubTask subTask) throws ManagerSaveException;

    void updateTask(Task task) throws ManagerSaveException;

    void updateEpic(Epic epic) throws ManagerSaveException;

    void updateSubTask(SubTask subTask) throws ManagerSaveException;

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

    <T extends Task> boolean isTaskOverlap(T task);

    <T extends Task> void writeSlots(T task);

    <T extends Task> void freeUpSlots(T task);

}
