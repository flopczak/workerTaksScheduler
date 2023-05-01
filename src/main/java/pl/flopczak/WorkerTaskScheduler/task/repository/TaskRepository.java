package pl.flopczak.WorkerTaskScheduler.task.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.flopczak.WorkerTaskScheduler.task.data.Task;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {
}
