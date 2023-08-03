package pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm;

import lombok.Data;
import org.springframework.data.util.Pair;
import pl.flopczak.WorkerTaskScheduler.SchedulingConstants;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.AlgorithmType;
import pl.flopczak.WorkerTaskScheduler.process.data.Process;
import pl.flopczak.WorkerTaskScheduler.statistics.data.StatisticDTO;
import pl.flopczak.WorkerTaskScheduler.statistics.service.StatisticService;
import pl.flopczak.WorkerTaskScheduler.task.data.Task;

import java.time.Instant;
import java.util.*;

import static pl.flopczak.WorkerTaskScheduler.statistics.service.StatisticUtils.getFastestWorkerForTask;


@Data
public class DFAlgorithm {
    private List<StatisticDTO> statistics;
    private List<Task> tasks;
    private List<Process> processes;
    private FlatIndividual individual;
    private Map<Long, List<FlatReservation>> optimisticAllocationMap;
    private StatisticService statisticService;
    private static final AlgorithmType algorithmType = AlgorithmType.DMStrategy;

    public DFAlgorithm(List<StatisticDTO> statistics, List<Task> tasks, List<Process> processes, StatisticService statisticService) {
        this.statistics = statistics;
        this.tasks = tasks;
        this.processes = processes;
        this.individual = new FlatIndividual(tasks, statistics);
        this.statisticService = statisticService;
        this.optimisticAllocationMap = optimisticAllocation();
    }


    public void scheduleTasks() {
        int index = 0;
        //tu pętla z ilością procesów a nie po mapie zawsze bierzemy pierwszy element z mapy do przydzielenia (nawet nie pierwszy element a schedule dla procesu)
        //do mapy dodajemy tylko nie przydzielone procesy
//        for (Map.Entry<Long, List<FlatReservation>> entry : optimisticAllocationMap.entrySet()) {
//            if(index == 0) {
//                individual.addToSchedule(entry.getValue());
//                index++;
//            } else {
//                //adjust optimistic allocation
//                individual.addToSchedule(scheduleProcess(entry.getValue()));
//            }
//        }
        individual.setTimeFitnesse(FlatIndividualUtil.calculateTimeFitness(individual.getSchedule()));
        individual.setDueFitnesse(FlatIndividualUtil.calculateDueFitness(individual.getSchedule()));
        try {
            individual.initializeChartData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void schedule() {
        List<Process> localProcesses = new ArrayList<>(processes);
        localProcesses.sort(Comparator.comparingLong(p -> p.getDueDate().getEpochSecond()));


        for (Process process : localProcesses) {
            List<Task> processTasks = process.getTaskList();
            List<FlatReservation> reservationsForProcess = new ArrayList<>();
            processTasks.sort(Comparator.comparing(Task::getType));
            Integer lastEnd = 0;
            for (Task task : processTasks) {
                FlatReservation winner = getWinnerForTask(task, lastEnd);
                lastEnd = winner.getEndTime();
                reservationsForProcess.add(winner);
            }
            individual.addToSchedule(reservationsForProcess);
        }

        individual.setTimeFitnesse(FlatIndividualUtil.calculateTimeFitness(individual.getSchedule()));
        individual.setDueFitnesse(FlatIndividualUtil.calculateDueFitness(individual.getSchedule()));
        try {
            individual.initializeChartData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private FlatReservation getWinnerForTask(Task task, Integer lastTaskEnd) {
        //return pair workerName and index of timePeriod
        Pair<Integer, Integer> winner = Pair.of(-1, -1);
        Integer winnerEndTime = -1;
        for (Map.Entry<Integer, List<TimePeriod>> entry : individual.getWorkersAvailabilities().entrySet()) {
            //pick the winner based on endTime
            //can task be done in timePeriod?
            for (int i = 0; i < entry.getValue().size(); i++) {
                TimePeriod timePeriod = entry.getValue().get(i);
                if (lastTaskEnd > timePeriod.getEndTime()) continue;
                StatisticDTO result = statisticService.findByTaskTypeAndWorkerName(task.getType(), entry.getKey());
                Integer taskDuration = result.getEstimatedTimeInSeconds() / 60;

                if (timePeriod.getStartTime() <= lastTaskEnd && timePeriod.getEndTime() >= taskDuration + lastTaskEnd) {
                    //Is timePeriod better than current winner?
                    if (winnerEndTime == -1) {
                        winnerEndTime = taskDuration + lastTaskEnd;
                        winner = Pair.of(entry.getKey(), i);
                    }
                    if (winnerEndTime > taskDuration + lastTaskEnd) {
                        winnerEndTime = taskDuration + lastTaskEnd;
                        winner = Pair.of(entry.getKey(), i);
                    }
                }
                if (timePeriod.getStartTime() > lastTaskEnd && timePeriod.getStartTime() + taskDuration <= timePeriod.getEndTime()) {
                    if (winnerEndTime == -1) {
                        winnerEndTime = timePeriod.getStartTime() + taskDuration;
                        winner = Pair.of(entry.getKey(), i);
                    }
                    if (winnerEndTime > timePeriod.getStartTime() + taskDuration) {
                        winnerEndTime = timePeriod.getStartTime() + taskDuration;
                        winner = Pair.of(entry.getKey(), i);
                    }

                }
            }
        }
        StatisticDTO result = statisticService.findByTaskTypeAndWorkerName(task.getType(), winner.getFirst());
        Integer taskDuration = result.getEstimatedTimeInSeconds() / 60;
        if (lastTaskEnd < individual.getWorkersAvailabilities().get(winner.getFirst()).get(winner.getSecond()).getStartTime()) {
            return FlatReservation.builder()
                    .startTime(individual.getWorkersAvailabilities().get(winner.getFirst()).get(winner.getSecond()).getStartTime())
                    .endTime(individual.getWorkersAvailabilities().get(winner.getFirst()).get(winner.getSecond()).getStartTime() + taskDuration)
                    .taskId(task.getTaskId())
                    .taskType(task.getType())
                    .processId(task.getProcess().getProcessId())
                    .taskDueTimeInMinutes(changeDueToMinutes(task.getDueDate()))
                    .workerName(winner.getFirst())
                    .algorithmType(algorithmType)
                    .testNumber(-1)
                    .build();
        }

        return FlatReservation.builder()
                .startTime(lastTaskEnd)
                .endTime(lastTaskEnd + taskDuration)
                .taskId(task.getTaskId())
                .taskType(task.getType())
                .processId(task.getProcess().getProcessId())
                .taskDueTimeInMinutes(changeDueToMinutes(task.getDueDate()))
                .workerName(winner.getFirst())
                .algorithmType(algorithmType)
                .testNumber(-1)
                .build();
    }

    private Map<Long, List<FlatReservation>> optimisticAllocation() {
        List<Process> localProcesses = new ArrayList<>(processes);
        Map<Long, List<FlatReservation>> reservationsForEachProcess = new TreeMap<>();
        for (Process process : localProcesses) {
            List<Task> processTasks = process.getTaskList();
            List<FlatReservation> reservationsForProcess = new ArrayList<>();
            processTasks.sort(Comparator.comparing(Task::getType));
            Long processDueGap = changeDueToMinutes(process.getDueDate());
            Integer lastStart = 0;
            for (Task task : processTasks) {
                Pair<Integer, Integer> fastestForTask = getFastestWorkerForTask(task, statistics);
                FlatReservation reservation = FlatReservation.builder()
                        .startTime(lastStart)
                        .endTime(lastStart + (fastestForTask.getSecond() / 60))
                        .taskId(task.getTaskId())
                        .taskType(task.getType())
                        .processId(task.getProcess().getProcessId())
                        .taskDueTimeInMinutes(changeDueToMinutes(task.getDueDate()))
                        .workerName(fastestForTask.getFirst())
                        .algorithmType(algorithmType)
                        .testNumber(-1)
                        .build();
                processDueGap = processDueGap - (fastestForTask.getSecond() / 60);
                lastStart += (fastestForTask.getSecond() / 60); //TODO to gówno nie działa dobrze
                reservationsForProcess.add(reservation);
            }
            reservationsForEachProcess.put(processDueGap, reservationsForProcess);
        }
        return reservationsForEachProcess;
    }

    private Long changeDueToMinutes(Instant due) {
        return (due.getEpochSecond() - SchedulingConstants.BEGGINING_OF_SCHEDULING.getEpochSecond()) / 60;
    }
}
