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
        SubTask subTask3 = new SubTask("Подзадача 1", "описание подзадачи 1", tm.getId(),
                TaskStatus.NEW, epic2.getId());
        tm.addToSubtasks(subTask1);
        tm.addToSubtasks(subTask2);
        tm.addToSubtasks(subTask3);

        System.out.println("Добавлены 2 задачи со статусами NEW и IN_PROGRESS, \n" +
                "Добавлен 1 эпик с 2 подзадачами со статусами NEW,\n" +
                "Добавлен 1 эпик с 1 подзадачей со статусом NEW");
        printAllTasks(tm);

        tm.updateTask(new Task(task1.getName(), task1.getDescription(), task1.getId(),
                TaskStatus.IN_PROGRESS));
        tm.updateTask(new Task(task2.getName(), task2.getDescription(), task2.getId(),
                TaskStatus.DONE));
        tm.updateSubTask(new SubTask(subTask1.getName(), subTask1.getDescription(),
                subTask1.getId(), TaskStatus.IN_PROGRESS, subTask1.getEpicId()));
        tm.updateSubTask(new SubTask(subTask2.getName(), subTask2.getDescription(),
                subTask2.getId(), TaskStatus.DONE, subTask2.getEpicId()));
        tm.updateSubTask(new SubTask(subTask3.getName(), subTask2.getDescription(),
                subTask3.getId(), TaskStatus.DONE, subTask3.getEpicId()));

        System.out.println("Статусы задач изменены на IN_PROGRESS и DONE, \n" +
                "Статусы подзадач 1-го эпика изменены на IN_PROGRESS и DONE \n" +
                "(ожидается изменение статуса 1-го эпика на IN_PROGRESS), \n" +
                "Статус подзадачи 2-го эпика изменен на DONE \n" +
                "(ожидается изменение статуса 2-го эпика на DONE)");

        printAllTasks(tm);

        tm.getTaskById(0);
        tm.getTaskById(1);
        tm.getEpicById(2);
        tm.getEpicById(3);
        tm.getSubTaskById(4);
        tm.getSubTaskById(5);
        tm.getSubTaskById(6);
        tm.getTaskById(0);
        tm.getTaskById(1);
        tm.getEpicById(2);

        System.out.println("Было просмотрено 10 задач по ID");
        printAllTasks(tm);

        tm.updateSubTask(new SubTask(subTask1.getName(), subTask1.getDescription(),
                subTask1.getId(), TaskStatus.DONE, subTask1.getEpicId()));
        tm.getEpicById(3);
        tm.getEpicById(2);

        System.out.println("Статус 1-й подзадачи 1-го эпика был изменен на DONE \n" +
                "(ожидается изменение статуса 1-го эпика на DONE) \n" +
                "Также было просмотрено еще две задачи. \n" +
                "(Ожидается что из истории будут удалены два первых просмотра, \n" +
                "а в конец списка добавятся два последних.");

        printAllTasks(tm);

        tm.updateEpic(new Epic("Обновлённый эпик 2","Обновленное описание эпика 2",
                epic2.getId()));
        tm.getSubTaskById(5);

        System.out.println("Был обновлен эпик 2\n" + "(ожидается, что подзадачи старого эпика будут удалены," +
                "а у обновленного эпика будет статус NEW). \n" +
                "Также по ID была просмотрена еще одна задача. \n" +
                "(Ожидается смещение списка вверх на одну позицию).");
        printAllTasks(tm);

        tm.removeTaskById(task1.getId());
        tm.removeTaskById(epic2.getId());

        System.out.println("Удалена 1-я задача\n" + "Удален 2-й эпик");
        printAllTasks(tm);

        for (int i = 0; i < 10; i++) {tm.getTaskById(1);}
        System.out.println("2-я задача была просмотрена 10 раз. \n" +
                "Ожидается, что список истрии просмотров будет состоять только из просмотров задачи №2.");
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
