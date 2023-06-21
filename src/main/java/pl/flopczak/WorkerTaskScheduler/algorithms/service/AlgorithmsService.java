package pl.flopczak.WorkerTaskScheduler.algorithms.service;

import org.springframework.stereotype.Service;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.FlatIndividual;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.GAToReturn;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.GeneticAlgorithm;
import pl.flopczak.WorkerTaskScheduler.process.data.Process;
import pl.flopczak.WorkerTaskScheduler.process.service.ProcessService;
import pl.flopczak.WorkerTaskScheduler.reservation.data.Reservation;
import pl.flopczak.WorkerTaskScheduler.reservation.data.ReservationDTO;
import pl.flopczak.WorkerTaskScheduler.reservation.service.ReservationService;
import pl.flopczak.WorkerTaskScheduler.statistics.data.Statistic;
import pl.flopczak.WorkerTaskScheduler.statistics.data.StatisticDTO;
import pl.flopczak.WorkerTaskScheduler.statistics.service.StatisticService;
import pl.flopczak.WorkerTaskScheduler.task.service.TaskService;
import pl.flopczak.WorkerTaskScheduler.worker.service.WorkerService;

import java.util.ArrayList;
import java.util.List;

@Service
public class AlgorithmsService {
    private final TaskService taskService;
    private final WorkerService workerService;
    private final StatisticService statisticService;
    private final ReservationService reservationService;
    private final ProcessService processService;

    private List<Reservation> allocations = new ArrayList<>();

    public AlgorithmsService(TaskService taskService, WorkerService workerService, StatisticService statisticService, ReservationService reservationService, ProcessService processService) {
        this.taskService = taskService;
        this.workerService = workerService;
        this.statisticService = statisticService;
        this.reservationService = reservationService;
        this.processService = processService;
    }




    public GAToReturn geneticAlgorithm() {
        List<Process> processes = processService.findAll();
        GeneticAlgorithm ga = new GeneticAlgorithm(taskService.findAll(), statisticService.findAll(), processService, statisticService, processes);
        ga.mainLoop();
        return new GAToReturn(ga.getFitnesses(), ga.getBestEver());
    }

    public FlatIndividual geneticAlgorithmBest() {
        List<Process> processes = processService.findAll();
        GeneticAlgorithm ga = new GeneticAlgorithm(taskService.findAll(), statisticService.findAll(), processService, statisticService, processes);
        ga.mainLoop();
        return ga.getBestEver();
    }

    public DepthFirstAlgorithm depthFirstAlgorithm() {
        List<Process> processes = processService.findAll();
        DepthFirstAlgorithm dp = new DepthFirstAlgorithm(statisticService.findAll(), taskService.findAll(), processes);
        dp.scheduleTasks();
        return dp;
    }

    public BreadthFirstAlgorithm breadthFirstAlgorithm() {
        List<Process> processes = processService.findAll();
        BreadthFirstAlgorithm bf = new BreadthFirstAlgorithm(statisticService.findAll(), taskService.findAll(), processes, statisticService);
        bf.scheduleTasks();
        return bf;
    }

    public FlatIndividual randomFlatIndividual() {
        FlatIndividual toReturn = new FlatIndividual(taskService.findAll(), statisticService.findAll());
        toReturn.generateRandomSchedule();
        return toReturn;
    }


    public List<ReservationDTO> startOptimisticAllocationConflicted() {
        List<Process> processes = processService.findAll();
        List<StatisticDTO> statistics = statisticService.findAll();
        return AlgorithmsUtil.startOptimisticAllocationConflicted(processes, statistics);
    }

}
