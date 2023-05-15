package pl.flopczak.WorkerTaskScheduler.algorithms.service;

import pl.flopczak.WorkerTaskScheduler.process.data.Process;
import pl.flopczak.WorkerTaskScheduler.reservation.data.ReservationDTO;

import java.util.List;

public interface Algorithm {
    List<ReservationDTO> schedule(List<Process> processes);
}
