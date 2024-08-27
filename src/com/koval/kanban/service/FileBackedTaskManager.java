package com.koval.kanban.service;

import com.koval.kanban.model.Epic;
import com.koval.kanban.model.SubTask;
import com.koval.kanban.model.Task;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.koval.kanban.service.CSVutils.stringToTask;
import static com.koval.kanban.service.CSVutils.taskToString;


public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    File autoSave;

    public static Logger getLog() {
        return log;
    }

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
                return;
            }
            fileWriter.write("id,type,name,status,description,epic,startTime,duration\n");
            for (Task task : super.getTasks()) {
                fileWriter.write(taskToString(task) + "\n");
            }
            for (Task epic : super.getEpics()) {
                fileWriter.write(taskToString(epic) + "\n");
            }
            for (Task subTask : super.getSubTasks()) {
                fileWriter.write(taskToString(subTask) + "\n");
            }
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, "Ошибка: ", e);
            throw new ManagerSaveException("Ошибка записи в файл. Указанный файл не существует.", e);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Ошибка: ", e);
            throw new ManagerSaveException("Ошибка записи в файл.", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTaskManager fbTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            if (!fileReader.ready()) {
                throw new ManagerSaveException("Файл пуст или не существует.");
            }
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                String[] split = line.split(",");
                String type = split[1];
                switch (type) {
                    case "TASK" -> fbTaskManager.addToTasks(stringToTask(line));
                    case "EPIC" -> fbTaskManager.addToEpics((Epic) stringToTask(line));
                    case "SUBTASK" -> fbTaskManager.addToSubtasks((SubTask) stringToTask(line));
                }
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Ошибка: ", e);
            throw new ManagerSaveException("Файл пуст или не существует.", e);
        }
        return fbTaskManager;
    }


}
