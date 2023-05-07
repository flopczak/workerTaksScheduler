package pl.flopczak.WorkerTaskScheduler.statistics.service;

import pl.flopczak.WorkerTaskScheduler.statistics.data.StatisticDTO;

import java.util.List;

public interface IStatisticService {
    List<StatisticDTO> findAll();
}
