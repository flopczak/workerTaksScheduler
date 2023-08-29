package pl.flopczak.WorkerTaskScheduler.algorithms.service;

import org.springframework.stereotype.Service;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.*;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.newGA.GA;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.newGA.NewGaToReturn;
import pl.flopczak.WorkerTaskScheduler.process.data.Process;
import pl.flopczak.WorkerTaskScheduler.process.service.ProcessService;
import pl.flopczak.WorkerTaskScheduler.reservation.data.Reservation;
import pl.flopczak.WorkerTaskScheduler.reservation.data.ReservationDTO;
import pl.flopczak.WorkerTaskScheduler.reservation.service.ReservationService;
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

    public GA gA() {
        List<Process> processes = processService.findAll();
        GA ga = new GA(taskService.findAll(), statisticService.findAll(), processService, statisticService, processes);
        ga.mainLoop();
        return ga;
    }
    public NewGaToReturn gaToReturn() {
        List<Process> processes = processService.findAll();
        GA ga = new GA(taskService.findAll(), statisticService.findAll(), processService, statisticService, processes);
        ga.mainLoop();
        return new NewGaToReturn(ga.getFitnesses(), ga.getBestEver());
    }

    public NewBreadthFirstAlgorithm newBreadthFirstAlgorithm() {
        List<Process> processes = processService.findAll();
        NewBreadthFirstAlgorithm bf = new NewBreadthFirstAlgorithm(statisticService.findAll(), taskService.findAll(), processes, statisticService);
        bf.scheduleTasks();
        return bf;
    }

    public DFAlgorithm newDFAlgorithm() {
        List<Process> processes = processService.findAll();
        DFAlgorithm df = new DFAlgorithm(statisticService.findAll(), taskService.findAll(), processes, statisticService);
        df.schedule();
        return df;
    }


    public List<ReservationDTO> startOptimisticAllocationConflicted() {
        List<Process> processes = processService.findAll();
        List<StatisticDTO> statistics = statisticService.findAll();
        return AlgorithmsUtil.startOptimisticAllocationConflicted(processes, statistics);
    }

}
