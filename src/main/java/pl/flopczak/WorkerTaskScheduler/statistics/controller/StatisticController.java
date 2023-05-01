package pl.flopczak.WorkerTaskScheduler.statistics.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.flopczak.WorkerTaskScheduler.statistics.data.Statistic;
import pl.flopczak.WorkerTaskScheduler.statistics.service.StatisticService;

import java.util.List;

@RestController
@RequestMapping(path = "api/statistics")
public class StatisticController {

    private final StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping
    public List<Statistic> findAll() {return statisticService.findAll();}
}
