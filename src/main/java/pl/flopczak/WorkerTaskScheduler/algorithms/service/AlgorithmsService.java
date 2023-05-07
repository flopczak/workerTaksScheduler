package pl.flopczak.WorkerTaskScheduler.algorithms.service;

import pl.flopczak.WorkerTaskScheduler.process.service.ProcessService;
import pl.flopczak.WorkerTaskScheduler.reservation.service.ReservationService;
import pl.flopczak.WorkerTaskScheduler.statistics.service.StatisticService;
import pl.flopczak.WorkerTaskScheduler.task.service.TaskService;
import pl.flopczak.WorkerTaskScheduler.worker.service.WorkerService;

public class AlgorithmsService {
    private final TaskService taskService;
    private final WorkerService workerService;
    private final StatisticService statisticService;
    private final ReservationService reservationService;
    private final ProcessService processService;

    public AlgorithmsService(TaskService taskService, WorkerService workerService, StatisticService statisticService, ReservationService reservationService, ProcessService processService) {
        this.taskService = taskService;
        this.workerService = workerService;
        this.statisticService = statisticService;
        this.reservationService = reservationService;
        this.processService = processService;
    }

    //potrzebne makeReservation
    // czas zacząć napierdalać ALGORYTMY KUREWSKIE!!
}
