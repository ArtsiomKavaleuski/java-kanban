package com.koval.kanban;

import com.koval.kanban.model.Epic;
import com.koval.kanban.model.SubTask;
import com.koval.kanban.model.Task;
import com.koval.kanban.service.Managers;
import com.koval.kanban.service.TaskManager;
import com.koval.kanban.service.TaskStatus;


public class Main {

    public static void main(String[] args) {
        TaskManager tm = Managers.getDefault();

        Task task1 = new Task("Задача 1", "описание задачи 1", tm.getId(), TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "описание задачи 2", tm.getId(), TaskStatus.IN_PROGRESS);
        tm.addToTasks(task1);
        tm.addToTasks(task2);

        Epic epic1 = new Epic("Эпик 1", "описание эпика 1", tm.getId());
        Epic epic2 = new Epic("Эпик 2", "описание эпика 2", tm.getId());
        tm.addToEpics(epic1);
        tm.addToEpics(epic2);

        SubTask subTask1 = new SubTask("Подзадача 1", "описание подзадачи 1", tm.getId(),
                TaskStatus.NEW, epic1.getId());
        SubTask subTask2 = new SubTask("Подзадача 2", "описание подзадачи 2", tm.getId(),
                TaskStatus.NEW, epic1.getId());
        SubTask subTask3 = new SubTask("Подзадача 3", "описание подзадачи 3", tm.getId(),
                TaskStatus.NEW, epic1.getId());
        tm.addToSubtasks(subTask1);
        tm.addToSubtasks(subTask2);
        tm.addToSubtasks(subTask3);

        System.out.println("Добавлены 2 задачи, 1 эпик с 3 подзадачами и 1 эпик без подзадач");
        printAllTasks(tm);

        tm.getTaskById(task1.getId());
        tm.getEpicById(epic2.getId());
        tm.getSubTaskById(subTask3.getId());
        tm.getEpicById(epic1.getId());
        tm.getTaskById(task1.getId());
        tm.getEpicById(epic2.getId());

        System.out.println("Просмотрены: задача 1, эпик 2, подзадача 3, эпик 1, задача 1, эпик 2 \n" +
                "Ожидается, что в истории просмотров не будет повторов и будут отражены только последние " +
                "просмотры всех задач \n" +
                "А именно: подзадача 3, эпик 1, задача 1, эпик 2.");
        printAllTasks(tm);

        tm.getTaskById(task1.getId());
        tm.getTaskById(task2.getId());
        tm.getEpicById(epic1.getId());
        tm.getSubTaskById(subTask2.getId());
        tm.getSubTaskById(subTask1.getId());
        tm.getSubTaskById(subTask3.getId());
        tm.getEpicById(epic1.getId());
        tm.getTaskById(task1.getId());
        tm.getEpicById(epic1.getId());

        System.out.println("Снова были просмотрены: задача 1, задача 2, эпик 1, подзадача 2, подзадача 1, подзадача 3, " +
                "эпик 1, задача 1, эпик 1 (все, кроме эпика 2) \n" +
                "Ожидается, что в истории просмотров все также не будет повторов и будут отражены только последние " +
                "просмотры всех задач \n" +
                "А именно: эпик 2, задача 2, подзадача 2, подзадача 1, подзадача 3, задача 1, эпик 1.");
        printAllTasks(tm);

        tm.removeTaskById(task1.getId());

        System.out.println("Была удалена задача 1 \n" +
                "Ожидается, что ее просмотр в истории просмотров будет удален");
        printAllTasks(tm);

        tm.removeTaskById(epic1.getId());

        System.out.println("Был удален эпик 1 с тремя подзадачами \n" +
                "Ожидается, что в истории просмотров будет удален просмотр самого эпика, " +
                "а также просмотры его подзадач");
        printAllTasks(tm);


    }

    private static void printAllTasks(TaskManager tm) {
        String separator1 = "═";
        String separator2 = "-";
        int numbersOfRepeat = 120;
        System.out.println(separator2.repeat(numbersOfRepeat));

        System.out.println("Задачи:");
        for (Task task : tm.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : tm.getEpics()) {
            System.out.println(epic);

            for (Task subTask : tm.getSubTasksByEpic(epic.getId())) {
                System.out.println("--> " + subTask);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : tm.getSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : tm.getHm().getHistory()) {
            System.out.println(task);
        }
        System.out.println(separator1.repeat(numbersOfRepeat));
    }
}
