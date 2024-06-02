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

    public void updateTask(int id, Task task) {
        tasks.put(id, task);
    }

    public void updateEpic(int id, Epic epic) {
        epics.put(id, epic);
        int numberOfDoneStatuses = 0;
        int numberOfNewStatuses = 0;
        for (int subTaskId : epics.get(id).getSubTaskIdList()) {
            if (subtasks.get(subTaskId).getStatus() == TaskStatus.DONE) {
                numberOfDoneStatuses++;
                epics.get(id).setStatus(TaskStatus.IN_PROGRESS);
            } else if (subtasks.get(subTaskId).getStatus() == TaskStatus.IN_PROGRESS) {
                epics.get(id).setStatus(TaskStatus.IN_PROGRESS);
            } else if (subtasks.get(subTaskId).getStatus() == TaskStatus.NEW) {
                numberOfNewStatuses++;
            }
        }
        if (numberOfDoneStatuses == epics.get(id).getSubTaskIdList().size()) {
            epics.get(id).setStatus(TaskStatus.DONE);
        } else if (numberOfNewStatuses == epics.get(id).getSubTaskIdList().size()) {
            epics.get(id).setStatus(TaskStatus.NEW);
        }
    }

    public void updateSubTask(int id, SubTask subTask) {
        subtasks.put(id, subTask);
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

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();
        if (!tasks.isEmpty()) {
            for (Task task : tasks.values()) tasksList.add(task);
        }
        return tasksList;
    }

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicsList = new ArrayList<>();
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) epicsList.add(epic);
        }
        return epicsList;
    }

    public ArrayList<SubTask> getSubTasks() {
        ArrayList<SubTask> subTasksList = new ArrayList<>();
        if (!subtasks.isEmpty()) {
            for (SubTask subTask : subtasks.values()) subTasksList.add(subTask);
        }
        return subTasksList;
    }

    public ArrayList<SubTask> getSubTasksByEpic(int userEpicId) {
        ArrayList<SubTask> subTasksListByEpic = new ArrayList<>();
        for (int subTaskId : epics.get(userEpicId).getSubTaskIdList()) {
            subTasksListByEpic.add(subtasks.get(subTaskId));
        }
        return subTasksListByEpic;
    }

    public Task getTaskById(int userTaskId) {
        return tasks.get(userTaskId);
    }

    public Epic getEpicById(int userEpicId) {
        return epics.get(userEpicId);
    }

    public SubTask getSubTaskById(int userSubtaskId) {
        return subtasks.get(userSubtaskId);
    }

    public void removeTasks() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        }
    }

    public void removeEpics() {
        if (!epics.isEmpty()) {
            epics.clear();
        }
    }

    public void removeSubtasks() {
        if (!subtasks.isEmpty()) {
            subtasks.clear();
        }
    }

    public void removeTaskById(int userTaskId) {
        if (tasks.containsKey(userTaskId)) {
            tasks.remove(userTaskId);
        } else if (epics.containsKey(userTaskId)) {
            epics.remove(userTaskId);
        } else if (subtasks.containsKey(userTaskId)) {
            subtasks.remove(userTaskId);
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
