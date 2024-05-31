import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIds = new ArrayList<>();

    public Epic(String taskName, String taskDescription, int taskId) {
        super(taskName, taskDescription, taskId, TaskStatus.NEW);
    }

    public Epic(String taskName, String taskDescription, int taskId, ArrayList<Integer> subTaskIds) {
        super(taskName, taskDescription, taskId, TaskStatus.NEW);
        this.subTaskIds = subTaskIds;
    }

    public ArrayList<Integer> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskId(int subTaskId) {
        this.subTaskIds.add(subTaskId);
    }

    @Override
    public String toString() {
        return "Epic{epicName='" + super.taskName +
                "', epicDescription='" + super.taskDescription +
                "', epicId='" + super.taskId +
                "', epicStatus='" + super.taskStatus +
                "', subTaskIds=" + subTaskIds +
                "}";
    }
}
