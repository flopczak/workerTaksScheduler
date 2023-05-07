package pl.flopczak.WorkerTaskScheduler.process.contoller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.flopczak.WorkerTaskScheduler.process.data.Process;
import pl.flopczak.WorkerTaskScheduler.process.service.ProcessService;

import java.util.List;

@RestController
@RequestMapping(path = "api/processes")
public class ProcessController {
    private final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Process> findAll() {
        return processService.findAll();
    }
}
