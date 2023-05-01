package pl.flopczak.WorkerTaskScheduler.worker.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import pl.flopczak.WorkerTaskScheduler.worker.data.Worker;
import pl.flopczak.WorkerTaskScheduler.worker.repository.WorkerRepository;

import java.util.List;

@Service
public class WorkerService implements IWorkerService {

    private static final Log LOG = LogFactory.getLog(WorkerService.class);
    private final WorkerRepository workerRepository;

    public WorkerService(WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
    }

    @Override
    public List<Worker> findAll() {
        return (List<Worker>) workerRepository.findAll();
    }
}
