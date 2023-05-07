package pl.flopczak.WorkerTaskScheduler.process.service;

import pl.flopczak.WorkerTaskScheduler.process.data.Process;

import java.util.List;

public interface IProcessService {

    List<Process> findAll();
}
