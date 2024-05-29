import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIds = new ArrayList<>();

    public Epic(String taskName, String taskDescription, int taskId) {
        super(taskName, taskDescription, taskId);
    }

    public void linkSubtask(int subTaskLinkId) {
        this.subTaskIds.add(subTaskLinkId);
    }
}
