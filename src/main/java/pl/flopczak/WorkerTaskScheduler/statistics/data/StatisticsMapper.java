package pl.flopczak.WorkerTaskScheduler.statistics.data;

import java.util.ArrayList;
import java.util.List;

public class StatisticsMapper {

    public static List<StatisticDTO> toDto(List<Statistic> entities) {
        List<StatisticDTO> result = new ArrayList<>();

        for (Statistic entity : entities) {
            StatisticDTO statisticDTO = new StatisticDTO(entity);
            result.add(statisticDTO);
        }

        return result;
    }
}
