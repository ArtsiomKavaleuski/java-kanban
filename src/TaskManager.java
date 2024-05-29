import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TaskManager {
    private int taskIdCounter = 0;
    HashMap<Integer, Task> tasks = new HashMap();
    HashMap<Integer, Epic> epics = new HashMap();
    HashMap<Integer, SubTask> subtasks = new HashMap();

    public void addToTasks(Task task) {
        tasks.put(task.getTaskId(), task);
    }

    public void addToEpics(Epic epic) {
        epics.put(epic.getTaskId(), epic);
    }

    public void addToSubtasks(SubTask subTask) {
        subtasks.put(subTask.getTaskId(), subTask);
    }

    public int getId() {
        return taskIdCounter++;
    }
}
