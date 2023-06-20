package pl.flopczak.WorkerTaskScheduler.algorithms.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataRecord {
    private String row;
    private Integer from;
    private Integer to;
    private String color;
}
