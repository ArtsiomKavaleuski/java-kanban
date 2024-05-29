import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        TaskManager tm = new TaskManager();

        while (true) {
            printMenu();
            int userCommand = Integer.parseInt(scan.nextLine());
            switch (userCommand) {
                case 1:
                    System.out.println("Enter the task name:");
                    String userTaskName = scan.nextLine();
                    System.out.println("Enter the task description:");
                    String userTaskDescription = scan.nextLine();
                    Task task = new Task(userTaskName, userTaskDescription, tm.getId());
                    tm.addToTasks(task);
                    break;
                case 0:
                    System.out.println("Good Buy");
                    return;
            }
        }





    }

    public static void printMenu() {
        System.out.println("choose command:");
        System.out.println("1 - add new task");
        System.out.println("0 - exit");
    }
}
