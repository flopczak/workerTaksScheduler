package pl.flopczak.WorkerTaskScheduler.statistics.service;

import org.springframework.stereotype.Service;
import pl.flopczak.WorkerTaskScheduler.statistics.data.Statistic;
import pl.flopczak.WorkerTaskScheduler.statistics.data.StatisticDTO;
import pl.flopczak.WorkerTaskScheduler.statistics.data.StatisticsMapper;
import pl.flopczak.WorkerTaskScheduler.statistics.repository.StatisticRepository;

import java.util.List;

@Service
public class StatisticService implements IStatisticService {

    private final StatisticRepository statisticRepository;

    public StatisticService(StatisticRepository statisticRepository) {
        this.statisticRepository = statisticRepository;
    }

    @Override
    public List<StatisticDTO> findAll() {
        return StatisticsMapper.toDto((List<Statistic>) statisticRepository.findAll());
    }
}
