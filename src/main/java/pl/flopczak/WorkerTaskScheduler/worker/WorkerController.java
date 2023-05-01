package pl.flopczak.WorkerTaskScheduler.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.flopczak.WorkerTaskScheduler.worker.data.Worker;
import pl.flopczak.WorkerTaskScheduler.worker.service.WorkerService;

import java.util.List;

@RestController
@RequestMapping(path = "api/workers")
public class WorkerController {

    private final WorkerService workerService;

    @Autowired
    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @GetMapping
    public List<Worker> findAll() {
        return workerService.findAll();
    }
}
