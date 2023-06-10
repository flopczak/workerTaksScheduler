package pl.flopczak.WorkerTaskScheduler.worker.data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import pl.flopczak.WorkerTaskScheduler.process.data.Process;

import java.time.Instant;

public class WorkerDTO {
    private Long taskId;
    private Process process;

    private Integer type;

    private Instant dueDate; //nie może być mniejsze niż due date procesu
}
