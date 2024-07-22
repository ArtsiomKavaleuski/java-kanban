package com.koval.kanban.service;

import com.koval.kanban.model.Epic;
import com.koval.kanban.model.SubTask;
import com.koval.kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryTaskManager implements TaskManager {
    private static int taskIdCounter = 0;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, SubTask> subtasks = new HashMap<>();
    HistoryManager hm = Managers.getDefaultHistory();

    @Override
    public void addToTasks(Task task) {
        tasks.computeIfAbsent(task.getId(), k -> task);
    }

    @Override
    public void addToEpics(Epic epic) {
        epics.computeIfAbsent(epic.getId(), k -> epic);
    }

    @Override
    public void addToSubtasks(SubTask subTask) {
        if (subTask.getEpicId() != subTask.getId()) {
            subtasks.computeIfAbsent(subTask.getId(), k -> subTask);
            epics.get(subTask.getEpicId()).addSubTaskId(subTask.getId());
        }
        updateEpicStatus(subTask);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        removeSubTasksByEpic(epic);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        subtasks.put(subTask.getId(), subTask);
        updateEpicStatus(subTask);
    }

    private void updateEpicStatus(SubTask subTask) {
        int numberOfDoneStatuses = 0;
        int numberOfNewStatuses = 0;
        for (int subTaskId : epics.get(subTask.getEpicId()).getSubTaskIds()) {
            if (subtasks.get(subTaskId).getStatus() == TaskStatus.DONE) {
                numberOfDoneStatuses++;
                epics.get(subTask.getEpicId()).setStatus(TaskStatus.IN_PROGRESS);
            } else if (subtasks.get(subTaskId).getStatus() == TaskStatus.IN_PROGRESS) {
                epics.get(subTask.getEpicId()).setStatus(TaskStatus.IN_PROGRESS);
            } else if (subtasks.get(subTaskId).getStatus() == TaskStatus.NEW) {
                numberOfNewStatuses++;
            }
        }
        if (numberOfDoneStatuses == epics.get(subTask.getEpicId()).getSubTaskIds().size()) {
            epics.get(subTask.getEpicId()).setStatus(TaskStatus.DONE);
        } else if (numberOfNewStatuses == epics.get(subTask.getEpicId()).getSubTaskIds().size()) {
            epics.get(subTask.getEpicId()).setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasksByEpic(int epicId) {
        ArrayList<SubTask> subTasksListByEpic = new ArrayList<>();
        for (int subTaskId : epics.get(epicId).getSubTaskIds()) {
            subTasksListByEpic.add(subtasks.get(subTaskId));
        }
        return subTasksListByEpic;
    }

    @Override
    public Task getTaskById(int id) {
        hm.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        hm.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        hm.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void removeTasks() {
        if (!tasks.isEmpty()) {
            for (int id : tasks.keySet()) {
                hm.remove(id);
            }
            tasks.clear();
        }
    }

    @Override
    public void removeEpics() {
        if (!epics.isEmpty()) {
            for (int id : subtasks.keySet()) {
                hm.remove(id);
            }
            for (int id : epics.keySet()) {
                hm.remove(id);
            }
            subtasks.clear();
            epics.clear();
        }
    }

    @Override
    public void removeSubtasks() {
        if (!subtasks.isEmpty()) {
            for (int id : subtasks.keySet()) {
                removeSubTaskFromEpicSubTaskIds(id);
                hm.remove(id);
            }
            subtasks.clear();
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            hm.remove(id);
        } else if (epics.containsKey(id)) {
            removeSubTasksByEpic(epics.get(id));
            epics.remove(id);
            hm.remove(id);
        } else if (subtasks.containsKey(id)) {
            removeSubTaskFromEpicSubTaskIds(id);
            subtasks.get(id).resetId();
            subtasks.remove(id);
            hm.remove(id);
        }
    }

    @Override
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
        taskIdCounter = 0;
    }

    @Override
    public int getId() {
        return taskIdCounter++;
    }

    @Override
    public HistoryManager getHm() {
        return hm;
    }

    public void removeSubTaskFromEpicSubTaskIds(int subTaskId) {
        epics.get(subtasks.get(subTaskId).getEpicId()).removeSubTaskId(subTaskId);
    }

    public void removeSubTasksByEpic(Epic epic) {
        if (epics.get(epic.getId()).getSubTaskIds() != null) {
            CopyOnWriteArrayList<Integer> tempSubTasksList = new CopyOnWriteArrayList<>(epics.get(epic.getId()).getSubTaskIds());
            for (int subTaskId : tempSubTasksList) {
                removeTaskById(subTaskId);
                hm.remove(subTaskId);
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InMemoryTaskManager that = (InMemoryTaskManager) o;

        if (!Objects.equals(tasks, that.tasks)) return false;
        if (!Objects.equals(epics, that.epics)) return false;
        if (!Objects.equals(subtasks, that.subtasks)) return false;
        return Objects.equals(hm, that.hm);
    }

    @Override
    public int hashCode() {
        int result = tasks != null ? tasks.hashCode() : 0;
        result = 31 * result + (epics != null ? epics.hashCode() : 0);
        result = 31 * result + (subtasks != null ? subtasks.hashCode() : 0);
        result = 31 * result + (hm != null ? hm.hashCode() : 0);
        return result;
    }
}