import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.lang.Integer;

public class TaskManager {
    private int taskIdCounter = 0;
    HashMap<Integer, Task> tasks = new HashMap();
    HashMap<Integer, Epic> epics = new HashMap();
    HashMap<Integer, SubTask> subtasks = new HashMap();

    public void addToTasks(Task task) {
        tasks.computeIfAbsent(task.getTaskId(), k -> task);
    }

    public void addToEpics(Epic epic) {
        epics.computeIfAbsent(epic.getTaskId(), k -> epic);
    }

    public void addToSubtasks(SubTask subTask) {
        subtasks.computeIfAbsent(subTask.getTaskId(), k -> subTask);
        epics.get(subTask.getEpicId()).setSubTaskId(subTask.taskId);
        updateEpicStatus(subTask);
    }

    public void updateTask(int taskId, Task task) {
        tasks.put(taskId, task);
    }

    public void updateSubTask(int taskId, SubTask subTask) {
        subtasks.put(taskId, subTask);
        updateEpicStatus(subTask);
    }

    private void updateEpicStatus(SubTask subTask) {
        int numberOfDoneStatuses = 0;
        for(int subTaskId : epics.get(subTask.getEpicId()).getSubTaskIds()) {
            if (subtasks.get(subTaskId).getTaskStatus() != TaskStatus.NEW ) {
                epics.get(subTask.getEpicId()).setTaskStatus(TaskStatus.IN_PROGRESS);
            } else if (subtasks.get(subTaskId).getTaskStatus() != TaskStatus.IN_PROGRESS) {
                numberOfDoneStatuses++;
            } else {
                epics.get(subTask.getEpicId()).setTaskStatus(TaskStatus.NEW);
            }
            /*
            if (subtasks.get(subTaskId).getTaskStatus() != TaskStatus.IN_PROGRESS) {
                epics.get(subTask.getEpicId()).setTaskStatus(TaskStatus.IN_PROGRESS);
            } else if (subtasks.get(subTaskId).getTaskStatus() != TaskStatus.NEW) {
                numberOfDoneStatuses++;
                epics.get(subTask.getEpicId()).setTaskStatus(TaskStatus.IN_PROGRESS);
            }
            */
        }
        if (numberOfDoneStatuses == epics.get(subTask.getEpicId()).getSubTaskIds().size()) {
            epics.get(subTask.getEpicId()).setTaskStatus(TaskStatus.DONE);
        }
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasksList = new ArrayList<>();
        if (!tasks.isEmpty()) {
            for (Task task : tasks.values()) tasksList.add(task);
        }
        return tasksList;
    }

    public ArrayList<Task> getEpics() {
        ArrayList<Task> epicsList = new ArrayList<>();
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) epicsList.add(epic);
        }
        return epicsList;
    }

    public ArrayList<Task> getSubTasks() {
        ArrayList<Task> subTasksList = new ArrayList<>();
        if (!subtasks.isEmpty()) {
            for (SubTask subTask : subtasks.values()) subTasksList.add(subTask);
        }
        return subTasksList;
    }

    public ArrayList<SubTask> getSubTasksByEpic (int userEpicId) {
        ArrayList<SubTask> subTasksListByEpic = new ArrayList<>();
        for (int subTaskId : epics.get(userEpicId).getSubTaskIds()) {
            subTasksListByEpic.add(subtasks.get(subTaskId));
        }
        return subTasksListByEpic;
    }

    public Task getTaskById(int userTaskId) {
        return tasks.get(userTaskId);
    }

    public Epic getEpicById (int userEpicId) {
        return epics.get(userEpicId);
    }

    public SubTask getSubTaskById (int userSubtaskId) {
        return subtasks.get(userSubtaskId);
    }

    public void removeTasks() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        }
    }

    public void removeEpics() {
        if(!epics.isEmpty()) {
            epics.clear();
        }
    }

    public void removeSubtasks () {
        if(!subtasks.isEmpty()) {
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
