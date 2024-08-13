package com.koval.kanban.service;

import com.koval.kanban.model.Epic;
import com.koval.kanban.model.SubTask;
import com.koval.kanban.model.Task;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.koval.kanban.service.CSVutils.stringToTask;
import static com.koval.kanban.service.CSVutils.taskToString;


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
            fileWriter.write("id,type,name,status,description,epic,startTime,duration\n");
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

        Task task1 = new Task("Задача 1", "описание задачи 1", fb.getId(), TaskStatus.NEW,
                LocalDateTime.of(2024, Month.AUGUST, 13, 13, 0),
                Duration.ofMinutes(30));
        Task task2 = new Task("Задача 2", "описание задачи 2", fb.getId(), TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024, Month.AUGUST, 15, 13, 0),
                Duration.ofMinutes(30));
        Task task3 = new Task("Задача 3", "описание задачи 3", fb.getId(), TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024, Month.AUGUST, 13, 17, 0),
                Duration.ofMinutes(120));
        fb.addToTasks(task1);
        fb.addToTasks(task2);
        fb.addToTasks(task3);

        Epic epic1 = new Epic("Эпик 1", "описание эпика 1", fb.getId());
        Epic epic2 = new Epic("Эпик 2", "описание эпика 2", fb.getId());
        fb.addToEpics(epic1);
        fb.addToEpics(epic2);


        SubTask subTask1 = new SubTask("Подзадача 1", "описание подзадачи 1", fb.getId(),
                TaskStatus.NEW, epic2.getId(),
                LocalDateTime.of(2024, Month.AUGUST, 14, 13, 0),
                Duration.ofMinutes(30));

        SubTask subTask2 = new SubTask("Подзадача 2", "описание подзадачи 2", fb.getId(),
                TaskStatus.NEW, epic1.getId(),
                LocalDateTime.of(2024, Month.AUGUST, 15, 13, 0),
                Duration.ofMinutes(30));
        SubTask subTask3 = new SubTask("Подзадача 3", "описание подзадачи 3", fb.getId(),
                TaskStatus.NEW, epic1.getId(),
                LocalDateTime.of(2024, Month.AUGUST, 15, 10, 0),
                Duration.ofMinutes(60));
        fb.addToSubtasks(subTask1);
        fb.addToSubtasks(subTask2);
        fb.addToSubtasks(subTask3);

        for (Task task : fb.getPrioritizedTasks()) {
            System.out.println(task);
        }
        System.out.println();

        Task testTask = new SubTask("Подзадача Test", "описание подзадачи Test", 7,
                TaskStatus.DONE, 3,
                LocalDateTime.of(2024, Month.AUGUST, 19, 10, 0),
                Duration.ofMinutes(60));
        fb.updateSubTask((SubTask) testTask);
        //System.out.println(testTask);

        for (Task task : fb.getPrioritizedTasks()) {
            System.out.println(task);
        }
        System.out.println();

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
