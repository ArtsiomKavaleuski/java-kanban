import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        TaskManager tm = new TaskManager();

        Task task1 = new Task("Задача 1", "описание задачи 1", tm.getId(),
                TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "описание задачи 2", tm.getId(),
                TaskStatus.IN_PROGRESS);
        tm.addToTasks(task1);
        tm.addToTasks(task2);

        Epic epic1 = new Epic("Эпик 1", "описание эпика 1", tm.getId());
        Epic epic2 = new Epic("Эпик 2", "описание эпика 2", tm.getId());
        tm.addToEpics(epic1);
        tm.addToEpics(epic2);

        SubTask subTask1 = new SubTask("Подзадача 1", "описание подзадачи 1", tm.getId(),
                TaskStatus.NEW, epic1.taskId);
        SubTask subTask2 = new SubTask("Подзадача 2", "описание подзадачи 2", tm.getId(),
                TaskStatus.NEW, epic1.taskId);
        SubTask subTask3 = new SubTask("Подзадача 1", "описание подзадачи 1", tm.getId(),
                TaskStatus.NEW, epic2.taskId);
        tm.addToSubtasks(subTask1);
        tm.addToSubtasks(subTask2);
        tm.addToSubtasks(subTask3);

        System.out.println("*".repeat(150));
        System.out.println("Добавлены 2 задачи со статусами IN_PROGRESS и NEW, \n" +
                "Добавлен 1 эпик с 2 подзадачами со статусами NEW,\n" +
                "Добавлен 1 эпик с 1 подзадачей со статусом NEW");
        print(tm);

        tm.updateTask(task1.getTaskId(), new Task(task1.getTaskName(), task1.taskDescription, task1.getTaskId(),
                TaskStatus.IN_PROGRESS));
        tm.updateTask(task2.getTaskId(), new Task(task2.getTaskName(), task2.taskDescription, task2.getTaskId(),
                TaskStatus.DONE));
        tm.updateSubTask(subTask1.getTaskId(), new SubTask(subTask1.getTaskName(), subTask1.getTaskDescription(),
                subTask1.getTaskId(), TaskStatus.IN_PROGRESS, subTask1.getEpicId()));
        tm.updateSubTask(subTask2.getTaskId(), new SubTask(subTask2.getTaskName(), subTask2.getTaskDescription(),
                subTask2.getTaskId(), TaskStatus.DONE, subTask2.getEpicId()));
        tm.updateSubTask(subTask3.getTaskId(), new SubTask(subTask3.getTaskName(), subTask2.getTaskDescription(),
                subTask3.getTaskId(), TaskStatus.DONE, subTask3.getEpicId()));

        System.out.println("");
        System.out.println("*".repeat(150));
        System.out.println("Статусы задач изменены на IN_PROGRESS и DONE, \n" +
                "Статусы подзадач 1-го эпика изменены на IN_PROGRESS и DONE \n" +
                "(ожидается изменение статуса 1-го эпика на IN_PROGRESS), \n" +
                "Статус подзадачи 2-го эписка изменен на DONE \n" +
                "(ожидается изменение статуса 2-го эпика на DONE)");

        print(tm);

        tm.updateSubTask(subTask1.getTaskId(), new SubTask(subTask1.getTaskName(), subTask1.getTaskDescription(),
                subTask1.getTaskId(), TaskStatus.DONE, subTask1.getEpicId()));

        System.out.println("");
        System.out.println("*".repeat(150));
        System.out.println("Статус 1-й подзадачи 1-го эпика был изменен на DONE \n" +
                "(ожидается изменение статуса 1-го эпика на DONE)");
        print(tm);

        tm.updateEpic(epic2.getTaskId(), new Epic("Обновлённый эпик 2",
                "Обновленное описание эпика 2", epic2.getTaskId(), epic2.getSubTaskIds()));

        System.out.println("");
        System.out.println("*".repeat(150));
        System.out.println("Был обновлен эпик 2\n" +
                "(ожидается, что его статус не поменяется)");
        print(tm);

        tm.removeTaskById(task1.getTaskId());
        tm.removeTaskById(epic2.getTaskId());

        System.out.println("");
        System.out.println("*".repeat(150));
        System.out.println("Удалена 1-я задача\n" +
                "Удален 2-й эпик");
        print(tm);
    }

    public static void print(TaskManager tm) {
        System.out.println("-".repeat(120));
        for (Task task : tm.getTasks()) {System.out.println(task);}
        System.out.println("");
        for (Epic epic : tm.getEpics()) {System.out.println(epic);}
        System.out.println("");
        for (SubTask subTask : tm.getSubTasks()) {System.out.println(subTask);}
    }
}
