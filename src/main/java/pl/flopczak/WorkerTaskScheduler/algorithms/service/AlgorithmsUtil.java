package pl.flopczak.WorkerTaskScheduler.algorithms.service;

import pl.flopczak.WorkerTaskScheduler.process.data.Process;
import pl.flopczak.WorkerTaskScheduler.reservation.data.ReservationDTO;
import pl.flopczak.WorkerTaskScheduler.statistics.data.Statistic;
import pl.flopczak.WorkerTaskScheduler.statistics.data.StatisticDTO;
import pl.flopczak.WorkerTaskScheduler.statistics.service.StatisticUtils;
import pl.flopczak.WorkerTaskScheduler.task.data.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AlgorithmsUtil {
    public static List<ReservationDTO> startOptimisticAllocationConflicted(List<Process> processes, List<StatisticDTO> statistics) {
        //Przydzielić jak najlepiej zasoby do zadań nie zwracając uwagi na konflikty zasobów
        List<ReservationDTO> optimisticReservationConflicted = new ArrayList<>();
        processes.sort(Comparator.comparing(Process::getDueDate));
        for (Process process : processes) {
            process.getTaskList().forEach( task -> {
                //TODO tu chyba nie powinno być tworzenia ReservationDTO tylko zwracanie intow????
                optimisticReservationConflicted.add(ReservationDTO.builder()
                        .taskType(task.getType())
                        .workerName(StatisticUtils.getFastestWorkerForTask(task, statistics))
                        .build());

            });
        }
        return optimisticReservationConflicted;
    }
}
