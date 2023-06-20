package pl.flopczak.WorkerTaskScheduler.statistics.service;

import org.springframework.data.util.Pair;
import pl.flopczak.WorkerTaskScheduler.statistics.data.StatisticDTO;
import pl.flopczak.WorkerTaskScheduler.task.data.Task;

import java.time.Instant;
import java.util.*;

public class StatisticUtils {

    private StatisticService statisticService;

    public StatisticUtils(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    public static Instant estimatedEndTime(Integer estimatedTimeInSeconds, Instant startTime) {
        return Instant.ofEpochSecond(startTime.getEpochSecond() + estimatedTimeInSeconds);
    }

    public static Set<Integer> getAvailableWorkers(List<StatisticDTO> statistics, Set<Integer> unavailableWorkers) {
        Set<Integer> availableWorkers = new HashSet<>();
        statistics.stream().forEach(statistic -> {
            if (!unavailableWorkers.contains(statistic.getWorkerName())) {
                availableWorkers.add(statistic.getWorkerName());
            }
        });
        return availableWorkers;
    }

    public static Pair<Integer, Integer> getFastestWorkerForTask(Task task, List<StatisticDTO> statistics) {
        Integer taskType = task.getType();
        Optional<StatisticDTO> result = statistics.stream()
                .filter(statistic -> Objects.equals(statistic.getTaskType(), taskType))
                .min(Comparator.comparingInt(StatisticDTO::getEstimatedTimeInSeconds));
        return result.isPresent() ? Pair.of(result.get().getWorkerName(), result.get().getEstimatedTimeInSeconds()) : Pair.of(-1, -1);
    }

}
