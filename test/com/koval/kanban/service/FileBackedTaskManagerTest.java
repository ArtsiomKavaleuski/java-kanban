package com.koval.kanban.service;

import com.koval.kanban.model.Task;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import static com.koval.kanban.service.FileBackedTaskManager.loadFromFile;
import static com.koval.kanban.service.CSVutils.stringToTask;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {

    @Test
    void shouldSaveEmptyFile() throws IOException {
        try {
            File file = File.createTempFile("test", "save");
            FileBackedTaskManager fbTaskManager = new FileBackedTaskManager(file);
            fbTaskManager.save();
            BufferedReader fileReader = new BufferedReader(new FileReader(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ManagerSaveException e) {
            e.getMessage();
            assertEquals("Был сохранен пустой файл.", e.getMessage());
        }
    }

    @Test
    void shouldLoadFromEmptyFile() throws IOException {
        try {
            File file = File.createTempFile("test", "load");
            TaskManager fbTaskManager = loadFromFile(file);
            assertEquals(fbTaskManager.getTasks().isEmpty() && fbTaskManager.getEpics().isEmpty() &&
                    fbTaskManager.getSubTasks().isEmpty(), true);
        } catch (ManagerSaveException e) {
            e.getMessage();
            assertEquals("Файл пуст.", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldSaveTwoTasksToFile() throws IOException {
        try {
            File file = File.createTempFile("test", "save");
            FileBackedTaskManager fbTaskManager = new FileBackedTaskManager(file);
            Task task1 = new Task("Задача 1", "описание задачи 1", fbTaskManager.getId(),
                    TaskStatus.NEW,
                    LocalDateTime.of(2024, Month.AUGUST, 14,15,0),
                    Duration.ofMinutes(30));
            Task task2 = new Task("Задача 2", "описание задачи 2", fbTaskManager.getId(),
                    TaskStatus.IN_PROGRESS,
                    LocalDateTime.of(2024, Month.AUGUST, 15,15,0),
                    Duration.ofMinutes(30));
            fbTaskManager.addToTasks(task1);
            fbTaskManager.addToTasks(task2);
            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            ArrayList<Task> tasks = new ArrayList<>();
            while(fileReader.ready()) {
                String line = fileReader.readLine();
                if(!line.equals("id,type,name,status,description,epic,startTime,duration")) {
                    tasks.add(stringToTask(line));
                }
            }
            assertEquals(fbTaskManager.getTasks(), tasks);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldLoadTwoTasksFromFile() throws IOException {
        try {
            File file1 = File.createTempFile("test", "save");
            FileBackedTaskManager fbTaskManager = new FileBackedTaskManager(file1);
            Task task1 = new Task("Задача 1", "описание задачи 1", fbTaskManager.getId(),
                    TaskStatus.NEW,
                    LocalDateTime.of(2024, Month.AUGUST, 14,15,0),
                    Duration.ofMinutes(30));
            Task task2 = new Task("Задача 2", "описание задачи 2", fbTaskManager.getId(),
                    TaskStatus.IN_PROGRESS,
                    LocalDateTime.of(2024, Month.AUGUST, 15,15,0),
                    Duration.ofMinutes(30));
            fbTaskManager.addToTasks(task1);
            fbTaskManager.addToTasks(task2);
            FileBackedTaskManager loadFromFileTaskManager = loadFromFile(file1);
            assertEquals(fbTaskManager.getTasks(), loadFromFileTaskManager.getTasks());
        } catch (ManagerSaveException e) {
            e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}