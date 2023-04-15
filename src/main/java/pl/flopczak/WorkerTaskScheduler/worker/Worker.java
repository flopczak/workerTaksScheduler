package pl.flopczak.WorkerTaskScheduler.worker;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class Worker {
    /**
     * ID of the worker
     */
    private Long id;
    /**
     * Name of the worker
     */
    private String name;

}
