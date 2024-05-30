public class SubTask extends Task {
    private final int epicId;

    public SubTask(String taskName, String taskDescription, int taskId, TaskStatus taskStatus, int epicId) {
        super(taskName, taskDescription, taskId, taskStatus);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
