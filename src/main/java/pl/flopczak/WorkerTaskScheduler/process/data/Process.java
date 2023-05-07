package pl.flopczak.WorkerTaskScheduler.process.data;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.flopczak.WorkerTaskScheduler.task.data.Task;

import java.util.List;

@Entity
@Table(name = "processes")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Process {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROCESS_ID")
    private Long processId;

    @JsonManagedReference
    @OneToMany(mappedBy = "process", fetch = FetchType.EAGER)
    private List<Task> taskList;
}
