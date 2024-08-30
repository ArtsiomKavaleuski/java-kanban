package com.koval.kanban.service;

import com.koval.kanban.model.Epic;
import com.koval.kanban.model.SubTask;
import com.koval.kanban.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    TaskManager tm = Managers.getDefault();

    @AfterEach
    void AfterEach() {
        tm.removeAllTasks();
    }

    @Test
    void shouldAddNewTaskToTasks() throws ManagerSaveException {
        Task task1 = new Task("task1Name", "task1Description", tm.getId(), TaskStatus.NEW,
                LocalDateTime.of(2024, Month.AUGUST, 14, 15, 0),
                Duration.ofMinutes(30));
        tm.addToTasks(task1);
        Task taskActual = tm.getTaskById(task1.getId());
        assertEquals(task1, taskActual, "разные задачи");
    }

    @Test
    void shouldAddNewEpicToEpics() {
        Epic epic1 = new Epic("epic1Name", "epic1Description", tm.getId());
        tm.addToEpics(epic1);
        Epic epicActual = tm.getEpicById(epic1.getId());
        assertEquals(epic1, epicActual, "разные эпики");
    }

    @Test
    void shouldAddNewSubTaskToSubtasks() throws ManagerSaveException {
        Epic epic1 = new Epic("epic1Name", "epic1Description", tm.getId());
        tm.addToEpics(epic1);
        SubTask subTask1 = new SubTask("subTask1Name", "subTask1Description", tm.getId(),
                TaskStatus.NEW,
                epic1.getId(),
                LocalDateTime.of(2024, Month.AUGUST, 14, 15, 0),
                Duration.ofMinutes(30));
        tm.addToSubtasks(subTask1);
        SubTask subTaskActual = tm.getSubTaskById(subTask1.getId());
        assertEquals(subTask1, subTaskActual, "разные подзадачи");
    }

    @Test
    void tasksWithGeneratedAndEnteredIdShouldNotHaveConflict() throws ManagerSaveException {
        int enteredSecondId = 1;
        int firstIdExpected = 0;
        Task task1 = new Task("task1Name", "task1Description", tm.getId(), TaskStatus.NEW,
                LocalDateTime.of(2024, Month.AUGUST, 14, 15, 0),
                Duration.ofMinutes(30));
        Task task2 = new Task("task2Name", "task2Description", enteredSecondId, TaskStatus.NEW,
                LocalDateTime.of(2024, Month.AUGUST, 15, 15, 0),
                Duration.ofMinutes(30));
        tm.addToTasks(task1);
        tm.addToTasks(task2);
        int firstIdActual = tm.getTasks().getFirst().getId();
        int secondIdActual = tm.getTasks().getLast().getId();
        assertEquals(firstIdExpected, firstIdActual, "Id не совпадают");
        assertEquals(enteredSecondId, secondIdActual, "Id не совпадают");
    }

    @Test
    void taskShouldStayTheSameAfterSaving() throws ManagerSaveException {
        String name = "task1Name";
        String description = "task1Description";
        int taskId = 1;
        TaskStatus taskStatus = TaskStatus.NEW;
        LocalDateTime dateTime = LocalDateTime.of(2024, Month.AUGUST, 14, 15, 0);
        Duration duration = Duration.ofMinutes(30);

        Task task1 = new Task(name, description, taskId, taskStatus, dateTime, duration);
        tm.addToTasks(task1);

        String nameActual = tm.getTasks().getFirst().getName();
        String descriptionActual = tm.getTasks().getFirst().getDescription();
        int idActual = tm.getTasks().getFirst().getId();
        TaskStatus taskStatusActual = tm.getTasks().getFirst().getStatus();

        assertEquals(name, nameActual, "именя задачи не соответствует первоначальному");
        assertEquals(description, descriptionActual, "описание задачи не соответствует первоначальному");
        assertEquals(taskId, idActual, "id задачи не соответствует первоначальному");
        assertEquals(taskStatus, taskStatusActual, "статус задачи не соответствует первоначальному");
    }

    @Test
    void epicShouldNotBeAddedAsHisOwnSubTask() throws ManagerSaveException {
        int epicId = 1;
        Epic epic1 = new Epic("epic1Name", "epic1Description", epicId);
        tm.addToEpics(epic1);
        SubTask subTask = new SubTask("subTaskName", "subTaskDescription", epicId,
                TaskStatus.NEW,
                epicId,
                LocalDateTime.of(2024, Month.AUGUST, 14, 15, 0),
                Duration.ofMinutes(30));
        tm.addToSubtasks(subTask);
        assertEquals(tm.getEpicById(1).getSubTaskIds().size(), 0, "эпик был добавлен к себе в подзадачу");
    }

    @Test
    void deletedSubTasksShouldNotKeepOldId() throws ManagerSaveException {
        Epic epic1 = new Epic("epic1", "epic1 description", 0);
        SubTask subTask1 = new SubTask("subTask1", "subtask1 description", 1, TaskStatus.NEW,
                0,
                LocalDateTime.of(2024, Month.AUGUST, 14, 15, 0),
                Duration.ofMinutes(30));
        SubTask subTask2 = new SubTask("subTask2", "subtask2 description", 2, TaskStatus.NEW,
                0,
                LocalDateTime.of(2024, Month.AUGUST, 15, 15, 0),
                Duration.ofMinutes(30));
        tm.addToEpics(epic1);
        tm.addToSubtasks(subTask1);
        tm.addToSubtasks(subTask2);
        tm.removeTaskById(subTask2.getId());
        assertNotEquals(subTask2.getId(), 2, "после удаления подзадача продолжаает хранить внутри себя старый ID");
    }

    @Test
    void epicShouldNotKeepIdsOfDDeletedSubTasks() throws ManagerSaveException {
        Epic epic1 = new Epic("epic1", "epic1 description", 0);
        SubTask subTask1 = new SubTask("subTask1", "subtask1 description", 3, TaskStatus.NEW,
                0,
                LocalDateTime.of(2024, Month.AUGUST, 14, 15, 0),
                Duration.ofMinutes(30));
        SubTask subTask2 = new SubTask("subTask2", "subtask2 description", 4, TaskStatus.NEW,
                0,
                LocalDateTime.of(2024, Month.AUGUST, 15, 15, 0),
                Duration.ofMinutes(30));
        tm.addToEpics(epic1);
        tm.addToSubtasks(subTask1);
        tm.addToSubtasks(subTask2);
        tm.removeTaskById(subTask2.getId());
        assertFalse(tm.getEpicById(0).getSubTaskIds().contains(subTask2.getId()), "в эпике хранится id удаленной подзадачи");
    }
}