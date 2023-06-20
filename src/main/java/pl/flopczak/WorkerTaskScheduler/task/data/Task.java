package pl.flopczak.WorkerTaskScheduler.task.data;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.flopczak.WorkerTaskScheduler.process.data.Process;

import java.time.Instant;

@Entity
@Table(name = "tasks")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Task {

    @Id
    @GeneratedValue
    private Long taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCESS_ID", referencedColumnName = "PROCESS_ID")
    @JsonBackReference
    private Process process;

    private Integer type;

    private Instant dueDate; //nie może być mniejsze niż due date procesu

    public TaskDTO toDTO() {
        TaskDTO dto = new TaskDTO();
        dto.setTaskId(this.taskId);
        dto.setProcessId(this.process != null ? this.process.getProcessId() : null);
        dto.setType(this.type);
        dto.setDueDate(this.dueDate);
        return dto;
    }

}
