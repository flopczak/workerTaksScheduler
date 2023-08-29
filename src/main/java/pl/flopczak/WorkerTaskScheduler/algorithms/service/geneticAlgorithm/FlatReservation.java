package pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.AlgorithmType;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.newGA.FlatReservationForCrossing;


@Data
@AllArgsConstructor
@Builder
public class FlatReservation {

    private Long reservationId;

    private Integer taskType;
    private Integer workerName;

    private Integer startTime;
    private Integer endTime;
    private AlgorithmType algorithmType;
    private Long taskId;
    private Long processId;
    private Integer testNumber;
    private Long taskDueTimeInMinutes;

    public static class FlatReservationBuilder {
        public FlatReservationBuilder() {
        }
    }

    public FlatReservationForCrossing toCrossing() {
        return FlatReservationForCrossing.builder()
                .taskType(taskType)
                .workerName(workerName)
                .taskId(taskId)
                .processId(processId)
                .taskDueTimeInMinutes(taskDueTimeInMinutes)
                .build();
    }

}
