package pl.flopczak.WorkerTaskScheduler.worker.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workers")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Worker {
    /**
     * ID of the worker
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workerId;
    /**
     * Name of the worker
     */
    private Integer name;

}
