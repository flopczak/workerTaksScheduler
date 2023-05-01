package pl.flopczak.WorkerTaskScheduler.task.service;


import org.springframework.stereotype.Service;
import pl.flopczak.WorkerTaskScheduler.task.data.Task;
import pl.flopczak.WorkerTaskScheduler.task.repository.TaskRepository;

import java.util.List;

@Service
public class TaskService implements ITaskService{

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> findAll() {
        return (List<Task>) taskRepository.findAll();
    }
}
