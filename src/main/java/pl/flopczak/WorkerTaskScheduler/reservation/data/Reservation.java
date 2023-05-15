package pl.flopczak.WorkerTaskScheduler.reservation.data;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.AlgorithmType;

import java.time.Instant;

@Entity
@Table(name = "reservations")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reservationId;

    private Long taskId;
    private Integer taskType;
    private Integer workerName;

    private Instant startTime;
    private Instant endTime;

    private AlgorithmType algorithmType;
    private Integer testNumber;

}
