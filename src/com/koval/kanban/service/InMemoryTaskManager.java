package com.koval.kanban.service;

import com.koval.kanban.model.Epic;
import com.koval.kanban.model.SubTask;
import com.koval.kanban.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private static int taskIdCounter = 0;

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, SubTask> subtasks = new HashMap<>();
    TreeSet<Task> sortedTasks = new TreeSet<>(new TaskByDateComparator());
    HistoryManager hm = Managers.getDefaultHistory();
    IntervalsTimeTable intervals = new IntervalsTimeTable();
    TreeMap<LocalDateTime, Boolean> slots = intervals.getTimeIntervals();

    @Override
    public <T extends Task> boolean checkOverlap(T task) {
        boolean isOverlapped = false;
        if (!task.getClass().equals(Epic.class) && task.getStartTime() != null) {
            LocalDateTime tempDateTime = task.getStartTime();
            while(tempDateTime.isBefore(task.getEndTime())) {
                tempDateTime = tempDateTime.plusMinutes(intervals.MINUTES_INTERVAL);
                if(slots.get(tempDateTime)) {
                    isOverlapped = true;
                    break;
                }
            }
        }
        return isOverlapped;
    }

    @Override
    public <T extends Task> void writeSlots(T task) {
        if (!task.getClass().equals(Epic.class) && task.getStartTime() != null) {
            LocalDateTime tempDateTime = task.getStartTime();
            while(true) {
                if(tempDateTime.isBefore(task.getEndTime())) {
                    slots.put(tempDateTime, true);
                    tempDateTime = tempDateTime.plusMinutes(intervals.MINUTES_INTERVAL);
                } else {
                    break;
                }
            }
        }
    }

    @Override
    public <T extends Task> void freeUpSlots(T task) {
        if (!task.getClass().equals(Epic.class) && task.getStartTime() != null) {
            LocalDateTime tempDateTime = task.getStartTime();
            while(true) {
                if(tempDateTime.isBefore(task.getEndTime())) {
                    slots.put(tempDateTime, false);
                    tempDateTime = tempDateTime.plusMinutes(intervals.MINUTES_INTERVAL);
                } else {
                    break;
                }
            }
        }
    }


    @Override
    public void addToTasks(Task task) throws ManagerSaveException {
        if (!checkOverlap(task)) {
            tasks.computeIfAbsent(task.getId(), k -> task);
            writeSlots(task);
            addToSortedTasks(task);
        } else {
            throw new ManagerSaveException("Добавляемая задача пересекается по времени выполнения");
        }
    }

    @Override
    public void addToEpics(Epic epic) {
        epics.computeIfAbsent(epic.getId(), k -> epic);
        addToSortedTasks(epic);
    }

    @Override
    public void addToSubtasks(SubTask subTask) throws ManagerSaveException {
        if (!checkOverlap(subTask)) {
            if (subTask.getEpicId() != subTask.getId()) {
                subtasks.computeIfAbsent(subTask.getId(), k -> subTask);
                epics.get(subTask.getEpicId()).addSubTaskId(subTask.getId());
            }
            updateEpicStatusAndTime(subTask);
            addToSortedTasks(epics.get(subTask.getEpicId()));
            addToSortedTasks(subTask);
            writeSlots(subTask);
        } else {
            throw new ManagerSaveException("Добавляемая подзадача пересекается по времени выполнения");
        }
    }

    @Override
    public void updateTask(Task task) throws ManagerSaveException {
        if (!checkOverlap(task)) {
            sortedTasks.remove(tasks.get(task.getId()));
            freeUpSlots(task);
            tasks.put(task.getId(), task);
            addToSortedTasks(task);
            writeSlots(task);
        } else {
            throw new ManagerSaveException("Обновленное время задачи пересекается по времени выполнения");
        }
    }

    @Override
    public void updateEpic(Epic epic) throws ManagerSaveException {
        sortedTasks.remove(tasks.get(epic.getId()));
        removeSubTasksByEpic(epic);
        epics.put(epic.getId(), epic);
        addToSortedTasks(epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) throws ManagerSaveException {
        if (!checkOverlap(subTask)) {
            addToSortedTasks(subTask);
            sortedTasks.remove(subtasks.get(subTask.getId()));
            sortedTasks.remove(epics.get(subTask.getEpicId()));
            freeUpSlots(subTask);
            subtasks.put(subTask.getId(), subTask);
            updateEpicStatusAndTime(subTask);
            addToSortedTasks(epics.get(subTask.getEpicId()));
            writeSlots(subTask);
        } else {
            throw new ManagerSaveException("Обновленное время подзадачи пересекается по времени выполнения");
        }
    }

    private void updateEpicStatusAndTime(SubTask subTask) {
        int numberOfDoneStatuses = 0;
        int numberOfNewStatuses = 0;
        LocalDateTime tempStartTime = subTask.getStartTime();
        LocalDateTime tempEndTime = subTask.getEndTime();
        Duration tempDuration = Duration.ZERO;
        for (int subTaskId : epics.get(subTask.getEpicId()).getSubTaskIds()) {

            if (subtasks.get(subTaskId).getStatus() == TaskStatus.DONE) {
                numberOfDoneStatuses++;
                epics.get(subTask.getEpicId()).setStatus(TaskStatus.IN_PROGRESS);
            } else if (subtasks.get(subTaskId).getStatus() == TaskStatus.IN_PROGRESS) {
                epics.get(subTask.getEpicId()).setStatus(TaskStatus.IN_PROGRESS);
            } else if (subtasks.get(subTaskId).getStatus() == TaskStatus.NEW) {
                numberOfNewStatuses++;
            }

            if (numberOfDoneStatuses == epics.get(subTask.getEpicId()).getSubTaskIds().size()) {
                epics.get(subTask.getEpicId()).setStatus(TaskStatus.DONE);
            } else if (numberOfNewStatuses == epics.get(subTask.getEpicId()).getSubTaskIds().size()) {
                epics.get(subTask.getEpicId()).setStatus(TaskStatus.NEW);
            }

            if (subtasks.get(subTaskId).getStartTime().isBefore(tempStartTime)) {
                tempStartTime = subtasks.get(subTaskId).getStartTime();
            }
            if (subtasks.get(subTaskId).getEndTime().isAfter(tempEndTime)) {
                tempEndTime = subtasks.get(subTaskId).getEndTime();
            }
            tempDuration = tempDuration.plus(subtasks.get(subTaskId).getDuration());

        }
        epics.get(subTask.getEpicId()).setStartTime(tempStartTime);
        epics.get(subTask.getEpicId()).setDuration(tempDuration);
        epics.get(subTask.getEpicId()).setEndTime(tempEndTime);

    }

    @Override
    public List<Task> getTasks() {
        return tasks.values().stream().toList();
    }

    @Override
    public List<? extends Task> getEpics() {
        return epics.values().stream().toList();
    }

    @Override
    public List<? extends Task> getSubTasks() {
        return subtasks.values().stream().toList();
    }

    @Override
    public List<? extends Task> getSubTasksByEpic(int epicId) {
        return epics.get(epicId).getSubTaskIds().stream()
                .map(i -> subtasks.get(i))
                .collect(Collectors.toCollection(ArrayList::new));
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
            tasks.keySet().stream().forEach(i -> hm.remove(i));
            tasks.clear();
        }
    }

    @Override
    public void removeEpics() {
        if (!epics.isEmpty()) {
            subtasks.keySet().stream().forEach(i -> hm.remove(i));
            epics.keySet().stream().forEach(i -> hm.remove(i));
            subtasks.clear();
            epics.clear();
        }
    }

    @Override
    public void removeSubtasks() {
        if (!subtasks.isEmpty()) {
            subtasks.keySet().stream().forEach(i -> hm.remove(i));
            subtasks.clear();
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            freeUpSlots(tasks.get(id));
            sortedTasks.remove(tasks.get(id));
            tasks.remove(id);
            hm.remove(id);
        } else if (epics.containsKey(id)) {
            removeSubTasksByEpic(epics.get(id));
            epics.remove(id);
            hm.remove(id);
        } else if (subtasks.containsKey(id)) {
            removeSubTaskFromEpicSubTaskIds(id);
            freeUpSlots(subtasks.get(id));
            sortedTasks.remove(subtasks.get(id));
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
        sortedTasks.clear();
    }

    @Override
    public int getId() {
        return taskIdCounter++;
    }

    @Override
    public HistoryManager getHm() {
        return hm;
    }

    @Override
    public void save() throws ManagerSaveException {

    }

    public void removeSubTaskFromEpicSubTaskIds(int subTaskId) {
        epics.get(subtasks.get(subTaskId).getEpicId()).removeSubTaskId(subTaskId);
    }

    public void removeSubTasksByEpic(Epic epic) {
        if (epics.get(epic.getId()).getSubTaskIds() != null) {
            CopyOnWriteArrayList<Integer> tempSubTasksList = new CopyOnWriteArrayList<>(epics.get(epic.getId()).getSubTaskIds());
            tempSubTasksList.stream().forEach(i -> {
                removeTaskById(i);
            });
        }
    }

    public void addToSortedTasks(Task task) {
        if (task.getStartTime() != null) {
            sortedTasks.add(task);
        }
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return sortedTasks;
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