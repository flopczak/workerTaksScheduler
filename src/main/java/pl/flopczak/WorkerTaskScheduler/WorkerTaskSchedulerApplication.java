package pl.flopczak.WorkerTaskScheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.flopczak.WorkerTaskScheduler.worker.Worker;

import java.util.List;

@SpringBootApplication
@RestController
public class WorkerTaskSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkerTaskSchedulerApplication.class, args);
	}

	@GetMapping("/hello")
	public List<Worker> hello() {
        return List.of(new Worker(1L, "worker1"), new Worker(2L, "worker"));
    }

}
