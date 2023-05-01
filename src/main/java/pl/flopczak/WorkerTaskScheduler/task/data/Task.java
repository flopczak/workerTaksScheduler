package pl.flopczak.WorkerTaskScheduler.task.data;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tasks")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Task {

    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long taskId;

    private Integer type;

}
