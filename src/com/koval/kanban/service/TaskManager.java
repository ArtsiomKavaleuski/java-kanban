package com.koval.kanban.service;

import com.koval.kanban.model.Epic;
import com.koval.kanban.model.SubTask;
import com.koval.kanban.model.Task;

import java.util.ArrayList;
import java.util.TreeSet;

public interface TaskManager {

    void addToTasks(Task task);

    void addToEpics(Epic epic);

    void addToSubtasks(SubTask subTask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<SubTask> getSubTasks();

    ArrayList<SubTask> getSubTasksByEpic(int epicId);

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

    TreeSet<Task> getPrioritizedTasks();

    <T extends Task> boolean isTasksOverlap(T task1, Task task2);
}
