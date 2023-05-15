package pl.flopczak.WorkerTaskScheduler.time.service;


import org.springframework.stereotype.Service;
import pl.flopczak.WorkerTaskScheduler.SchedulingConstants;
import pl.flopczak.WorkerTaskScheduler.TimeUtils;
import pl.flopczak.WorkerTaskScheduler.process.data.Process;
import pl.flopczak.WorkerTaskScheduler.process.service.ProcessService;
import pl.flopczak.WorkerTaskScheduler.task.data.Task;
import pl.flopczak.WorkerTaskScheduler.task.service.TaskService;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TimeService {

    private ProcessService processService;

    public TimeService(ProcessService processService) {
        this.processService = processService;
    }


    public void fillDataWithRandomDueDates() {
        List<Process> processes = processService.findAll();

        //TODO poprawić random date between żeby była między 8:00 a 16:00

        for (Process process : processes) {
            process.setDueDate(TimeUtils
                    .randomDateBetween(SchedulingConstants.BEGGINING_OF_SCHEDULING,
                            SchedulingConstants.BEGGINING_OF_SCHEDULING.plus(5, ChronoUnit.DAYS)));
            //TODO wywołać update na repo procesu
            for (Task task : process.getTaskList()) {
                task.setDueDate(TimeUtils.randomDateBetween(SchedulingConstants.BEGGINING_OF_SCHEDULING, process.getDueDate()));
            }
            processService.updateProcess(process);
        }
    }
}
