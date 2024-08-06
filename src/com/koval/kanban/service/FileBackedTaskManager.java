package com.koval.kanban.service;

import com.koval.kanban.model.Epic;
import com.koval.kanban.model.SubTask;
import com.koval.kanban.model.Task;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.koval.kanban.service.TaskStringConverter.stringToTask;
import static com.koval.kanban.service.TaskStringConverter.taskToString;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    File autoSave;
    static Logger log = new TaskManagerLogger(FileBackedTaskManager.class.getName()).getLogger();

    public FileBackedTaskManager(File file) {
        this.autoSave = file;
    }

    @Override
    public void addToTasks(Task task) {
        super.addToTasks(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            log.log(Level.SEVERE, "Ошибка: ", e);
        }
    }

    @Override
    public void addToEpics(Epic epic) {
        super.addToEpics(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            log.log(Level.SEVERE, "Ошибка: ", e);
        }
    }

    @Override
    public void addToSubtasks(SubTask subTask) {
        super.addToSubtasks(subTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            log.log(Level.SEVERE, "Ошибка: ", e);
        }
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        try {
            save();
        } catch (ManagerSaveException e) {
            log.log(Level.SEVERE, "Ошибка: ", e);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        try {
            save();
        } catch (ManagerSaveException e) {
            log.log(Level.SEVERE, "Ошибка: ", e);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        try {
            save();
        } catch (ManagerSaveException e) {
            log.log(Level.SEVERE, "Ошибка: ", e);
        }
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            log.log(Level.SEVERE, "Ошибка: ", e);
        }
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        try {
            save();
        } catch (ManagerSaveException e) {
            log.log(Level.SEVERE, "Ошибка: ", e);
        }
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            log.log(Level.SEVERE, "Ошибка: ", e);
        }
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        try {
            save();
        } catch (ManagerSaveException e) {
            log.log(Level.SEVERE, "Ошибка: ", e);
        }
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        try {
            save();
        } catch (ManagerSaveException e) {
            log.log(Level.SEVERE, "Ошибка: ", e);
        }
    }

    public void save() throws ManagerSaveException {
        try (Writer fileWriter = new FileWriter(autoSave)) {
            if (tasks.isEmpty() && epics.isEmpty() && subtasks.isEmpty()) {
                fileWriter.write("");
                throw new ManagerSaveException("Был сохранен пустой файл.");
            }
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
            log.log(Level.SEVERE, "Ошибка: ", e);
            throw new ManagerSaveException("Ошибка записи в файл.", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException, ManagerSaveException {
        FileBackedTaskManager fbTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                if (line == null) {
                    throw new ManagerSaveException("Файл пуст или не существует.");
                }
                String[] split = line.split(",");
                String type = split[1];
                if (type.equals("TASK")) {
                    fbTaskManager.addToTasks(stringToTask(line));
                } else if (type.equals("EPIC")) {
                    fbTaskManager.addToEpics((Epic) stringToTask(line));
                } else if (type.equals("SUBTASK")) {
                    fbTaskManager.addToSubtasks((SubTask) stringToTask(line));
                }
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Ошибка: ", e);
            throw new ManagerSaveException("Файл пуст или не существует.", e);
        } catch (ManagerSaveException e) {
            log.log(Level.SEVERE, "Ошибка: ", e);
            throw new ManagerSaveException("Ошибка записи в файл.", e);
        }
        return fbTaskManager;
    }

    public static void main(String[] args) throws IOException {
        File dir = new File("src/com/koval/kanban/resources");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "TaskManager.csv");

        TaskManager fb = new FileBackedTaskManager(file);

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

        try {
            FileBackedTaskManager fbTaskManagerFromFile = loadFromFile(file);
            System.out.println("---".repeat(30));
            System.out.println("Задачи сохраненные в файл были загружены в новый менеджер: " +
                    fb.getTasks().equals(fbTaskManagerFromFile.getTasks()));
            System.out.println("Эпики сохраненные в файл были загружены в новый менеджер: " +
                    fb.getEpics().equals(fbTaskManagerFromFile.getEpics()));
            System.out.println("Подзадачи сохраненные в файл были загружены в новый менеджер: " +
                    fb.getSubTasks().equals(fbTaskManagerFromFile.getSubTasks()));
        } catch (ManagerSaveException e) {
            log.log(Level.SEVERE, "Ошибка: ", e);
        }
    }
}
