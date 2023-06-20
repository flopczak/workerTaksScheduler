package pl.flopczak.WorkerTaskScheduler.task.data;

import lombok.Data;

import java.time.Instant;

@Data
public class TaskDTO {
    private Long taskId;
    private Long processId;
    private Integer type;
    private Instant dueDate;
    private boolean isScheduled = false;
}
