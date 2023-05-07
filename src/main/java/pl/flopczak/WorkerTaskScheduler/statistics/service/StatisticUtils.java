package pl.flopczak.WorkerTaskScheduler.statistics.service;

import pl.flopczak.WorkerTaskScheduler.statistics.data.StatisticDTO;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

}
