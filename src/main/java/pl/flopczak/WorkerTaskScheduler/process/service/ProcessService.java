package pl.flopczak.WorkerTaskScheduler.process.service;

import org.springframework.stereotype.Service;
import pl.flopczak.WorkerTaskScheduler.process.data.Process;
import pl.flopczak.WorkerTaskScheduler.process.repository.ProcessRepository;

import java.util.List;

@Service
public class ProcessService implements IProcessService {
    private ProcessRepository processRepository;


    public ProcessService(ProcessRepository processRepository) {
        this.processRepository = processRepository;
    }

    @Override
    public List<Process> findAll() {
        return (List<Process>) processRepository.findAll();
    }

    public Process updateProcess(Process process) {
        return processRepository.save(process);
    }
}
