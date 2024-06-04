package com.koval.kanban.service;

import com.koval.kanban.model.Epic;
import com.koval.kanban.model.SubTask;
import com.koval.kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int taskIdCounter = 0;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, SubTask> subtasks = new HashMap<>();

    public void addToTasks(Task task) {
        tasks.computeIfAbsent(task.getId(), k -> task);
    }

    public void addToEpics(Epic epic) {
        epics.computeIfAbsent(epic.getId(), k -> epic);
    }

    public void addToSubtasks(SubTask subTask) {
        subtasks.computeIfAbsent(subTask.getId(), k -> subTask);
        epics.get(subTask.getEpicId()).addSubTaskId(subTask.getId());
        updateEpicStatus(subTask);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    /*
    Пересмотрел ТЗ и решил, что если при обновлении на вход подаётся новый объект (эпик), значит вместе с его
    созданием создаётся и новый пустой список подзадач.
    Обновление статуса эпика будет происходить только после добавления и обновления новых подзадач.
    Подзадачи старого эпика можно либо оставить (но тогда, как я вижу, они продолжат ссылаться на новый эпик,
    который не будет знать, что это его подзадачи, и его статус больше не будет обновляться при обновлении статуса
    этих подзадач). Либо, чтобы не терять данные, подзадачи можно перевести в обычные задачи.
     */
    public void updateEpic(Epic epic) {
        for(int subTaskId : epics.get(epic.getId()).getSubTaskIdList()) {
            removeTaskById(subTaskId);
        }
        epics.put(epic.getId(), epic);
    }

    public void updateSubTask(SubTask subTask) {
        subtasks.put(subTask.getId(), subTask);
        updateEpicStatus(subTask);
    }

    // добавил приватный метод updateEpicStatus, чтобы не дублировать код
    private void updateEpicStatus(SubTask subTask) {
        int numberOfDoneStatuses = 0;
        int numberOfNewStatuses = 0;
        for (int subTaskId : epics.get(subTask.getEpicId()).getSubTaskIdList()) {
            if (subtasks.get(subTaskId).getStatus() == TaskStatus.DONE) {
                numberOfDoneStatuses++;
                epics.get(subTask.getEpicId()).setStatus(TaskStatus.IN_PROGRESS);
            } else if (subtasks.get(subTaskId).getStatus() == TaskStatus.IN_PROGRESS) {
                epics.get(subTask.getEpicId()).setStatus(TaskStatus.IN_PROGRESS);
            } else if (subtasks.get(subTaskId).getStatus() == TaskStatus.NEW) {
                numberOfNewStatuses++;
            }
        }
        if (numberOfDoneStatuses == epics.get(subTask.getEpicId()).getSubTaskIdList().size()) {
            epics.get(subTask.getEpicId()).setStatus(TaskStatus.DONE);
        } else if (numberOfNewStatuses == epics.get(subTask.getEpicId()).getSubTaskIdList().size()) {
            epics.get(subTask.getEpicId()).setStatus(TaskStatus.NEW);
        }
    }

    public ArrayList<Task> getTasks() { return new ArrayList<>(tasks.values()); }

    public ArrayList<Epic> getEpics() { return new ArrayList<>(epics.values());}

    public ArrayList<SubTask> getSubTasks() { return new ArrayList<>(subtasks.values()); }

    public ArrayList<SubTask> getSubTasksByEpic(int epicId) {
        ArrayList<SubTask> subTasksListByEpic = new ArrayList<>();
        for (int subTaskId : epics.get(epicId).getSubTaskIdList()) {
            subTasksListByEpic.add(subtasks.get(subTaskId));
        }
        return subTasksListByEpic;
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subtasks.get(id);
    }

    public void removeTasks() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        }
    }

    public void removeEpics() {
        if (!epics.isEmpty()) {
            subtasks.clear();
            epics.clear();
        }
    }

    public void removeSubtasks() {
        if (!subtasks.isEmpty()) {
            subtasks.clear();
        }
    }

    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (epics.containsKey(id)) {
            for (int subTaskId : epics.get(id).getSubTaskIdList()) {removeTaskById(subTaskId);}
            epics.remove(id);
        } else if (subtasks.containsKey(id)) {
            subtasks.remove(id);
        }
    }

    public void removeAllTasks() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        }
        if (!epics.isEmpty()) {
            epics.clear();
        }
        if (!subtasks.isEmpty()) {
            subtasks.clear();
        }
    }

    public int getId() {
        return taskIdCounter++;
    }
}
