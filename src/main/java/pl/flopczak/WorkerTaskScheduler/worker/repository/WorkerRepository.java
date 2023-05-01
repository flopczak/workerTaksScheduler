package pl.flopczak.WorkerTaskScheduler.worker.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.flopczak.WorkerTaskScheduler.worker.data.Worker;

@Repository
public interface WorkerRepository extends CrudRepository<Worker, Long> {

}
