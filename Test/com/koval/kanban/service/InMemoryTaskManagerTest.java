package com.koval.kanban.service;

import com.koval.kanban.model.Epic;
import com.koval.kanban.model.SubTask;
import com.koval.kanban.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    TaskManager tm = Managers.getDefault();

    @AfterEach
    void AfterEach() {
        tm.removeAllTasks();
    }

    @Test
    void shouldAddNewTaskToTasks() {
        Task task1 = new Task("task1Name", "task1Description", tm.getId(), TaskStatus.NEW);
        tm.addToTasks(task1);
        Task taskExpected = task1;
        Task taskActual = tm.getTaskById(task1.getId());
        assertEquals(taskExpected, taskActual, "разные задачи");
    }

    @Test
    void shouldAddNewEpicToEpics() {
        Epic epic1 = new Epic("epic1Name", "epic1Description", tm.getId());
        tm.addToEpics(epic1);
        Epic epicExpected = epic1;
        Epic epicActual = tm.getEpicById(epic1.getId());
        assertEquals(epicExpected, epicActual, "разные эпики");
    }

    @Test
    void shouldAddNewSubTaskToSubtasks() {
        Epic epic1 = new Epic("epic1Name", "epic1Description", tm.getId());
        tm.addToEpics(epic1);
        SubTask subTask1 = new SubTask("subTask1Name", "subTask1Description", tm.getId(),
                TaskStatus.NEW, epic1.getId());
        tm.addToSubtasks(subTask1);
        SubTask subTaskExpected = subTask1;
        SubTask subTaskActual = tm.getSubTaskById(subTask1.getId());
        assertEquals(subTaskExpected, subTaskActual, "разные подзадачи");
    }

    @Test
    void tasksWithGeneratedAndEnteredIdShouldNotHaveConflict() {
        int enteredSecondId = 1;
        int firstIdExpected = 0;
        Task task1 = new Task("task1Name", "task1Description", tm.getId(), TaskStatus.NEW);
        Task task2 = new Task("task2Name", "task2Description", enteredSecondId, TaskStatus.NEW);
        tm.addToTasks(task1);
        tm.addToTasks(task2);
        int firstIdActual = tm.getTasks().getFirst().getId();
        int secondIdActual = tm.getTasks().getLast().getId();
        assertEquals(firstIdExpected, firstIdActual, "Id не совпадают");
        assertEquals(enteredSecondId, secondIdActual, "Id не совпадают");
    }

    @Test
    void taskShouldStayTheSameAfterSaving() {
        String name = "task1Name";
        String description = "task1Description";
        int taskId = 1;
        TaskStatus taskStatus = TaskStatus.NEW;

        Task task1 = new Task(name, description, taskId, taskStatus);
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
    void epicShouldNotBeAddedAsHisOwnSubTask() {
        int epicId = 1;
        Epic epic1 = new Epic("epic1Name", "epic1Description", epicId);
        tm.addToEpics(epic1);
        SubTask subTask = new SubTask("subTaskName", "subTaskDescription", epicId,
                TaskStatus.NEW, epicId);
        tm.addToSubtasks(subTask);
        assertNull(tm.getSubTaskById(epic1.getId()), "эпик был добален к себе в подзадачу");
    }

    @Test
    void subTaskShouldNotBeMadeHisOwnEpic() {
        int epicId = 1;
        int subTaskId = 2;
        Epic epic1 = new Epic("epic1Name", "epic1Description", epicId);
        tm.addToEpics(epic1);
        SubTask subTask = new SubTask("subTaskName", "subTaskDescription", subTaskId,
                TaskStatus.NEW, epicId);
        tm.addToSubtasks(subTask);
        tm.updateEpic(new Epic(subTask.getName(), subTask.getDescription(), subTask.getEpicId()));
        assertNotEquals(epic1.getSubTaskIdList(), tm.getEpics().getFirst().getSubTaskIdList(),
                "подзадача была сделана своим же эпиком");
        assertNotEquals(tm.getEpics().getFirst().getSubTaskIdList().size(), subTaskId,
                "список ID подзадач эпика содержит ID самого эпика");
    }

    @Test
    void deletedSubTasksShouldNotKeepOldId () {
        Epic epic1 = new Epic("epic1","epic1 description",0);
        SubTask subTask1 = new SubTask("subTask1", "subtask1 description", 1, TaskStatus.NEW, 0);
        SubTask subTask2 = new SubTask("subTask2", "subtask2 description", 2, TaskStatus.NEW, 0);
        tm.addToEpics(epic1);
        tm.addToSubtasks(subTask1);
        tm.addToSubtasks(subTask2);
        tm.removeTaskById(subTask2.getId());
        assertNotEquals(subTask2.getId(), 2, "после удаления подзадача продолжаает хранить внутри себя старый ID");

    }

    @Test
    void epicShouldNotKeepIdsOfDDeletedSubTasks() {
        Epic epic1 = new Epic("epic1","epic1 description",0);
        SubTask subTask1 = new SubTask("subTask1", "subtask1 description", 1, TaskStatus.NEW, 0);
        SubTask subTask2 = new SubTask("subTask2", "subtask2 description", 2, TaskStatus.NEW, 0);
        tm.addToEpics(epic1);
        tm.addToSubtasks(subTask1);
        tm.addToSubtasks(subTask2);
        tm.removeTaskById(subTask2.getId());
        assertEquals(tm.getEpicById(0).getSubTaskIdList().contains(subTask2.getId()), false,"в эпике храанится id удаленной подзаадачи");
    }
}