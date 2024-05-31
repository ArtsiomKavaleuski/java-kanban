public class SubTask extends Task {
    private final int epicId;

    public SubTask(String taskName, String taskDescription, int taskId, TaskStatus taskStatus, int epicId) {
        super(taskName, taskDescription, taskId, taskStatus);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{subtaskName='" + super.taskName +
                "', subtaskDescription='" + super.taskDescription +
                "', subtaskId='" + super.taskId +
                "', subtaskStatus='" + super.taskStatus +
                "', epicId='" + epicId +
                "'}";
    }
}
