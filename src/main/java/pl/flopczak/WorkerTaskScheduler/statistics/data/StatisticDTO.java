package pl.flopczak.WorkerTaskScheduler.statistics.data;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StatisticDTO {
    private Long statisticId;
    private Integer taskType;
    private Integer workerName;
    private Integer estimatedTimeInSeconds;

    public StatisticDTO(Statistic entity) {
        this.statisticId = entity.getStatisticId();
        this.taskType = entity.getTaskType();
        this.workerName = entity.getWorkerName();
        this.estimatedTimeInSeconds = entity.getEstimatedTimeInSeconds();
    }
}
