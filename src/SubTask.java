public class SubTask extends Task {
    private final int linkId;

    public SubTask(String taskName, String taskDescription, int taskId, int linkId) {
        super(taskName, taskDescription, taskId);
        this.linkId = linkId;
    }
}
