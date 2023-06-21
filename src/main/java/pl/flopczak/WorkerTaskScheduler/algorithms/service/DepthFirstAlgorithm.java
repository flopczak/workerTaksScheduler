package pl.flopczak.WorkerTaskScheduler.algorithms.service;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.util.Pair;
import pl.flopczak.WorkerTaskScheduler.SchedulingConstants;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.FlatIndividual;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.FlatIndividualUtil;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.FlatReservation;
import pl.flopczak.WorkerTaskScheduler.process.data.Process;
import pl.flopczak.WorkerTaskScheduler.statistics.data.StatisticDTO;
import pl.flopczak.WorkerTaskScheduler.task.data.Task;

import java.time.Instant;
import java.util.*;

import static pl.flopczak.WorkerTaskScheduler.statistics.service.StatisticUtils.getFastestWorkerForTask;

@Getter
@Setter
public class DepthFirstAlgorithm {
    private static final Log LOG = LogFactory.getLog(DepthFirstAlgorithm.class);

    private List<StatisticDTO> statistics;
    private List<Task> tasks;
    private List<Process> processes;
    private FlatIndividual individual;
    private Map<Long, List<FlatReservation>> optimisticAllocationMap;
    private static final AlgorithmType algorithmType = AlgorithmType.DMStrategy;

    public DepthFirstAlgorithm(List<StatisticDTO> statistics, List<Task> tasks, List<Process> processes) {
        this.statistics = statistics;
        this.tasks = tasks;
        this.processes = processes;
        this.individual = new FlatIndividual(tasks,statistics);
        this.optimisticAllocationMap = optimisticAllocation();
    }




    public void scheduleTasks() {
        int index =  0;
        for (Map.Entry<Long, List<FlatReservation>> entry : optimisticAllocationMap.entrySet()) {
            if(index == 0) {
                individual.addToSchedule(entry.getValue());
                index++;
            } else {
                individual.addToSchedule(scheduleProcess(entry.getValue()));
            }
        }
        individual.setTimeFitnesse(FlatIndividualUtil.calculateTimeFitness(individual.getSchedule()));
        individual.setDueFitnesse(FlatIndividualUtil.calculateDueFitness(individual.getSchedule()));
        try {
            individual.initializeChartData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<FlatReservation> scheduleProcess(List<FlatReservation> optimisticReservations) {
        List<Task> tasksToSchedule = processes.stream()
                .filter(process -> process.getProcessId().equals(optimisticReservations.get(0).getProcessId()))
                .findFirst().get().getTaskList();
        tasksToSchedule.sort(Comparator.comparing(Task::getType));
        List<FlatReservation> toReturn = new ArrayList<>();

        Integer endTimeTaskBefore = 0;
            for(Task task : tasksToSchedule) {
                FlatReservation bestReservation = individual.getFastestPossibleReservation(task, endTimeTaskBefore);
                endTimeTaskBefore = bestReservation.getEndTime();
                toReturn.add(bestReservation);
            }


        return toReturn;
    }


    private Map<Long, List<FlatReservation>> optimisticAllocation() {
        //do każdej instancji procesu przydzielić najlepsze opcje i sprawidzić która ma najmniejszy czas due
        //uszeregować procesy od najbardziej zagrorzonyc i przydzielić im najlepsze zasoby uznajmy że ważna jest kolejność zadań
        List<Process> localProcesses = new ArrayList<>(processes);
        Map<Long, List<FlatReservation>> reservationsForEachProcess = new TreeMap<>();
        for (Process process : localProcesses) {
            List<Task> processTasks = process.getTaskList();
            List<FlatReservation> reservationsForProcess = new ArrayList<>();
            processTasks.sort(Comparator.comparing(Task::getType));
            Long processDueGap = changeDueToMinutes(process.getDueDate());
            Integer lastStart = 0;
            for (Task task : processTasks) {
                Pair<Integer,Integer> fastestForTask = getFastestWorkerForTask(task, statistics);
                FlatReservation reservation = FlatReservation.builder()
                        .startTime(lastStart)
                        .endTime(lastStart + (fastestForTask.getSecond()/60))
                        .taskId(task.getTaskId())
                        .taskType(task.getType())
                        .processId(task.getProcess().getProcessId())
                        .taskDueTimeInMinutes(changeDueToMinutes(task.getDueDate()))
                        .workerName(fastestForTask.getFirst())
                        .algorithmType(algorithmType)
                        .testNumber(-1)
                        .build();
                processDueGap = processDueGap - (fastestForTask.getSecond()/60);
                lastStart += (fastestForTask.getSecond()/60); //TODO to gówno nie działa dobrze
                reservationsForProcess.add(reservation);
            }
            reservationsForEachProcess.put(processDueGap,reservationsForProcess);
        }


        return reservationsForEachProcess;
    }
    private Long changeDueToMinutes(Instant due){
        return (due.getEpochSecond() - SchedulingConstants.BEGGINING_OF_SCHEDULING.getEpochSecond()) / 60;
    }
}
