import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIdList = new ArrayList<>();

    public Epic(String taskName, String taskDescription, int taskId) {
        super(taskName, taskDescription, taskId, TaskStatus.NEW);
    }

    public Epic(String taskName, String taskDescription, int taskId, ArrayList<Integer> subTaskIds) {
        super(taskName, taskDescription, taskId, TaskStatus.NEW);
        this.subTaskIdList = subTaskIds;
    }

    public ArrayList<Integer> getSubTaskIdList() {
        return subTaskIdList;
    }

    public void addSubTaskId(int subTaskId) {
        this.subTaskIdList.add(subTaskId);
    }

    @Override
    public String toString() {
        return "Epic{epicName='" + super.taskName +
                "', epicDescription='" + super.taskDescription +
                "', epicId='" + super.taskId +
                "', epicStatus='" + super.taskStatus +
                "', subTaskIds=" + subTaskIdList +
                "}";
    }
}
