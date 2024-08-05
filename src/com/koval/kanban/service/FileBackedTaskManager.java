package com.koval.kanban.service;

import com.koval.kanban.model.Epic;
import com.koval.kanban.model.SubTask;
import com.koval.kanban.model.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    @Override
    public void addToTasks(Task task) {
        super.addToTasks(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
    }

    @Override
    public void addToEpics(Epic epic) {
        super.addToEpics(epic);try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
    }

    @Override
    public void addToSubtasks(SubTask subTask) {
        super.addToSubtasks(subTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            e.getMessage();
        }
    }

    public <T extends Task> String taskToString(T task) {
        String taskString;
        if (task.getClass().equals(SubTask.class)) {
            taskString = String.format("%d,%s,%s,%s,%s,%d", task.getId(),
                    TaskTypes.SUBTASK.toString().toUpperCase(), task.getName(), task.getStatus(),
                    task.getDescription(), ((SubTask) task).getEpicId());
        } else if (task.getClass().equals(Epic.class)) {
            taskString = String.format("%d,%s,%s,%s,%s,", task.getId(),
                    TaskTypes.EPIC.toString().toUpperCase(), task.getName(), task.getStatus(),
                    task.getDescription());
        } else {
            taskString = String.format("%d,%s,%s,%s,%s,", task.getId(),
                    TaskTypes.TASK.toString().toUpperCase(), task.getName(), task.getStatus(),
                    task.getDescription());
        }
        return taskString;
    }

    public void save() throws ManagerSaveException {
        File dir = new File("src/com/koval/kanban/resources");
        if(!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(dir, "TaskManager.csv");

        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task : super.getTasks()) {
                fileWriter.write(taskToString(task) + "\n");
            }
            for (Epic epic : super.getEpics()) {
                fileWriter.write(taskToString(epic) + "\n");
            }
            for (SubTask subTask : super.getSubTasks()) {
                fileWriter.write(taskToString(subTask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи в файл.", e);
        }
    }

    public static void main(String[] args) throws IOException {
        TaskManager fb = new FileBackedTaskManager();
        Task task1 = new Task("Задача 1", "описание задачи 1", fb.getId(), TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "описание задачи 2", fb.getId(), TaskStatus.IN_PROGRESS);
        fb.addToTasks(task1);
        fb.addToTasks(task2);

        Epic epic1 = new Epic("Эпик 1", "описание эпика 1", fb.getId());
        Epic epic2 = new Epic("Эпик 2", "описание эпика 2", fb.getId());
        fb.addToEpics(epic1);
        fb.addToEpics(epic2);

        SubTask subTask1 = new SubTask("Подзадача 1", "описание подзадачи 1", fb.getId(),
                TaskStatus.NEW, epic2.getId());
        SubTask subTask2 = new SubTask("Подзадача 2", "описание подзадачи 2", fb.getId(),
                TaskStatus.NEW, epic1.getId());
        SubTask subTask3 = new SubTask("Подзадача 3", "описание подзадачи 3", fb.getId(),
                TaskStatus.NEW, epic1.getId());
        fb.addToSubtasks(subTask1);
        fb.addToSubtasks(subTask2);
        fb.addToSubtasks(subTask3);
    }

}
