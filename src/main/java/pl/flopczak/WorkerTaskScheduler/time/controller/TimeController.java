package pl.flopczak.WorkerTaskScheduler.time.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.flopczak.WorkerTaskScheduler.time.service.TimeService;

@RestController
@RequestMapping(path = "api/time")
public class TimeController {

    private final TimeService timeService;

    public TimeController(TimeService timeService) {
        this.timeService = timeService;
    }

    @GetMapping
    public void fillDueDates(){
        timeService.fillDataWithRandomDueDates();
    }
}
