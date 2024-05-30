import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        TaskManager tm = new TaskManager();

        while (true) {
            ConsoleTestingMenu.printMenu();
            int userCommand = Integer.parseInt(scan.nextLine());
            switch (userCommand) {
                case 1:
                    System.out.println("Enter the task name:");
                    String userTaskName = scan.nextLine();
                    System.out.println("Enter the task description:");
                    String userTaskDescription = scan.nextLine();
                    System.out.println("Enter the status of task: " + "[" + TaskStatus.NEW.toString() + ", " +
                            TaskStatus.IN_PROGRESS.toString() + ", " + TaskStatus.DONE.toString() + "]");
                    TaskStatus userStatus = TaskStatus.valueOf(scan.nextLine());
                    tm.addToTasks(new Task(userTaskName, userTaskDescription, tm.getId(), userStatus));
                    break;
                case 2:
                    System.out.println("Enter the epic name:");
                    userTaskName = scan.nextLine();
                    System.out.println("Enter the task description:");
                    userTaskDescription = scan.nextLine();
                    tm.addToEpics(new Epic(userTaskName, userTaskDescription, tm.getId()));
                    break;
                case 3:
                    System.out.println("Enter the subtask name:");
                    userTaskName = scan.nextLine();
                    System.out.println("Enter the subtask description:");
                    userTaskDescription = scan.nextLine();
                    System.out.println("Enter the status of subtask: " + "[" + TaskStatus.NEW.toString() + ", " +
                            TaskStatus.IN_PROGRESS.toString() + ", " + TaskStatus.DONE.toString() + "]");
                    userStatus = TaskStatus.valueOf(scan.nextLine().toUpperCase());
                    System.out.println("Enter the epic id:");
                    int userEpicId = Integer.parseInt(scan.nextLine());
                    tm.addToSubtasks(new SubTask(userTaskName, userTaskDescription, tm.getId(), userStatus, userEpicId));
                    break;
                case 4:
                    System.out.println(tm.getTasks());
                    break;
                case 5:
                    System.out.println(tm.getEpics());
                    break;
                case 6:
                    System.out.println(tm.getSubTasks());
                    break;
                case 7:
                    System.out.println(tm.getTaskById());
                    break;
                case 8:
                    System.out.println(tm.getEpicById());
                    break;
                case 9:
                    System.out.println(tm.getSubTaskById());
                    break;
                case 10:
                    System.out.println("Enter the epic id");
                    userEpicId = Integer.parseInt(scan.nextLine());
                    System.out.println(tm.getSubTasksByEpic(userEpicId));
                    break;
                case 11:
                    tm.updateTask();
                    break;
                case 12:
                    tm.updateEpic();
                    break;
                case 13:
                    System.out.println("Enter the subtask ID:");
                    int userSubTaskId = Integer.parseInt(scan.nextLine());
                    System.out.println("Enter the subtask name:");
                    System.out.println("Enter new status of subtask:" + "[" + TaskStatus.NEW.toString() + ", " +
                            TaskStatus.IN_PROGRESS.toString() + ", " + TaskStatus.DONE.toString() + "]");
                    userStatus = TaskStatus.valueOf(scan.nextLine());
                    tm.updateSubTask(userSubTaskId, new SubTask(tm.subtasks.get(userSubTaskId).getTaskName(),
                            tm.subtasks.get(userSubTaskId).getTaskDescription(),
                            userSubTaskId, userStatus, tm.subtasks.get(userSubTaskId).getEpicId()));
                    break;
                case 14:
                    tm.removeTasks();
                    break;
                case 15:
                    tm.removeEpics();
                    break;
                case 16:
                    tm.removeSubtasks();
                    break;
                case 17:
                    tm.removeTaskById();
                case 0:
                    System.out.println("Good Buy");
                    return;
                default:
                    System.out.println("Unknown command. Try again.");
            }
        }
    }
}
