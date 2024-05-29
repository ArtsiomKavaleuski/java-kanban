import java.util.Objects;

public class Task {
    protected final String taskName;
    protected final String taskDescription;
    protected final int taskId;
    protected TaskStatus taskStatus;

    public Task(String taskName, String taskDescription, int taskId) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskId = taskId;
        this.taskStatus = TaskStatus.NEW;
    }

    public int getTaskId() {
        return taskId;
    }


    @Override
    public String toString() {
        return "Task{taskName=" + taskName +
                ", taskDescription=" + taskDescription +
                ", taskId=" + taskId +
                ", taskStatus=" + taskStatus + "}";
    }


}
