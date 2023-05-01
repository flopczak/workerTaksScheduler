package pl.flopczak.WorkerTaskScheduler.worker.service;

import pl.flopczak.WorkerTaskScheduler.worker.data.Worker;

import java.util.List;

public interface IWorkerService {
    List<Worker> findAll();
}
