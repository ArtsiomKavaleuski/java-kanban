import java.util.Objects;

public class Task {
    protected final String taskName;
    protected final String taskDescription;
    protected final int taskId;
    protected TaskStatus taskStatus;

    public Task(String taskName, String taskDescription, int taskId, TaskStatus taskStatus) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskId = taskId;
        this.taskStatus = taskStatus;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public int getTaskId() {
        return taskId;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void checkStatus(TaskStatus taskStatus) {
        if (taskStatus != TaskStatus.NEW || taskStatus != TaskStatus.IN_PROGRESS || taskStatus != TaskStatus.DONE) {
            System.out.println("wrong status");
        }
    }
    @Override
    public String toString() {
        return "Task{taskName='" + taskName +
                "', taskDescription='" + taskDescription +
                "', taskId='" + taskId +
                "', taskStatus='" + taskStatus + "'}";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId && Objects.equals(taskName, task.taskName) && Objects.equals(taskDescription, task.taskDescription) && taskStatus == task.taskStatus;
    }
    @Override
    public int hashCode() {
        return Objects.hash(taskName, taskDescription, taskId, taskStatus);
    }
}
