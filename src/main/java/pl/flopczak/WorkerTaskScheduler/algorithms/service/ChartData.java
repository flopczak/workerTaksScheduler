package pl.flopczak.WorkerTaskScheduler.algorithms.service;


import lombok.Data;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.LegendObject;

import java.util.List;

@Data
public class ChartData {
    private String title;
    private String label;
    private List<LegendObject> legend;
    private List<String> rows;
    private List<Integer> columns;
    private List<DataRecord> bars;
    private Double bar_height;
}
