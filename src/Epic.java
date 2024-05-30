import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIds = new ArrayList<>();

    public Epic(String taskName, String taskDescription, int taskId) {
        super(taskName, taskDescription, taskId, TaskStatus.NEW);
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskId(int subTaskId) {
        this.subTaskIds.add(subTaskId);
    }
}
