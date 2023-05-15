package pl.flopczak.WorkerTaskScheduler.statistics.service;

import pl.flopczak.WorkerTaskScheduler.statistics.data.StatisticDTO;
import pl.flopczak.WorkerTaskScheduler.task.data.Task;

import java.time.Instant;
import java.util.*;

public class StatisticUtils {
    public static Instant estimatedEndTime(Integer estimatedTimeInSeconds, Instant startTime) {
        return Instant.ofEpochSecond(startTime.getEpochSecond() + estimatedTimeInSeconds);
    }

    public static Set<Integer> getAviableWorkers(List<StatisticDTO> statistics, Set<Integer> unavailableWorkers) {
        Set<Integer> availableWorkers = new HashSet<>();
        statistics.stream().forEach(statistic -> {
            if (!unavailableWorkers.contains(statistic.getWorkerName())) {
               availableWorkers.add(statistic.getWorkerName());
            }
        });
        return availableWorkers;
    }

    public static Integer getFastestWorkerForTask(Task task, List<StatisticDTO> statistics) {
        Integer taskType = task.getType();
        Optional<StatisticDTO> result = statistics.stream()
                .filter(statistic -> Objects.equals(statistic.getTaskType(), taskType))
                .min(Comparator.comparingInt(StatisticDTO::getEstimatedTimeInSeconds));
        return result.isPresent() ? result.get().getWorkerName() : -1;
    }

}
