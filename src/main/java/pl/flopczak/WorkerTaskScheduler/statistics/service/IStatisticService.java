package pl.flopczak.WorkerTaskScheduler.statistics.service;

import pl.flopczak.WorkerTaskScheduler.statistics.data.Statistic;

import java.util.List;

public interface IStatisticService {
    List<Statistic> findAll();
}
