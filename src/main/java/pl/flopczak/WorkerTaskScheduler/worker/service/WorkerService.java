package pl.flopczak.WorkerTaskScheduler.worker.service;

import org.springframework.stereotype.Service;
import pl.flopczak.WorkerTaskScheduler.worker.Worker;

import java.util.List;

@Service
public class WorkerService {

    public List<Worker> getWorkers() {
        return List.of(new Worker(1L, "worker1"), new Worker(2L, "worker"));
    }
}
