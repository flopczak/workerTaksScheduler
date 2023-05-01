package pl.flopczak.WorkerTaskScheduler.statistics.data;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "statistics")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Statistic {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statisticId;


    private Integer taskType;

    private Integer workerName;

    private Integer estimatedTimeInSeconds;
}
