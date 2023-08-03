package pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.newGA;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlatReservationForCrossing {
    private Integer taskType;
    private Integer workerName;
    private Long taskId;
    private Long processId;
    private Long taskDueTimeInMinutes;
}
