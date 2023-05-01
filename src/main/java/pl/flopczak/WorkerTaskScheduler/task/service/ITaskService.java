package pl.flopczak.WorkerTaskScheduler.task.service;

import pl.flopczak.WorkerTaskScheduler.task.data.Task;

import java.util.List;

public interface ITaskService {
    List<Task> findAll();
}
