package pl.flopczak.WorkerTaskScheduler.process.repository;

import org.springframework.data.repository.CrudRepository;
import pl.flopczak.WorkerTaskScheduler.process.data.Process;

public interface ProcessRepository extends CrudRepository<Process, Long> {
}
