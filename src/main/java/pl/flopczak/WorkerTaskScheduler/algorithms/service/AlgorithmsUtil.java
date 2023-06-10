package pl.flopczak.WorkerTaskScheduler.algorithms.service;

import org.springframework.data.util.Pair;
import pl.flopczak.WorkerTaskScheduler.SchedulingConstants;
import pl.flopczak.WorkerTaskScheduler.process.data.Process;
import pl.flopczak.WorkerTaskScheduler.reservation.data.ReservationDTO;
import pl.flopczak.WorkerTaskScheduler.statistics.data.StatisticDTO;
import pl.flopczak.WorkerTaskScheduler.task.data.Task;
import pl.flopczak.WorkerTaskScheduler.worker.data.Worker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static pl.flopczak.WorkerTaskScheduler.statistics.service.StatisticUtils.getFastestWorkerForTask;

public class AlgorithmsUtil {
    public static List<ReservationDTO> startOptimisticAllocationConflicted(List<Process> processes, List<StatisticDTO> statistics) {
        //Przydzielić jak najlepiej zasoby do zadań nie zwracając uwagi na konflikty zasobów
        List<ReservationDTO> optimisticReservationConflicted = new ArrayList<>();
        processes.sort(Comparator.comparing(Process::getDueDate));
        for (Process process : processes) {
            process.getTaskList().forEach(task -> {
                Pair<Integer, Integer> workerWithTime = getFastestWorkerForTask(task, statistics);
                optimisticReservationConflicted.add(ReservationDTO.builder()
                        .taskType(task.getType())
                        .workerName(workerWithTime.getFirst())
                        .startTime(SchedulingConstants.BEGGINING_OF_SCHEDULING)
                        .endTime(SchedulingConstants.BEGGINING_OF_SCHEDULING.plusSeconds(workerWithTime.getSecond()))
                        .build());

            });
        }
        return optimisticReservationConflicted;
    }

    public static void getFreeWorkersForTask(List<ReservationDTO> schedule, List<Worker> workers, Task task){
        // sprawidzić kiedy kończy się zadanie??
    }
}
