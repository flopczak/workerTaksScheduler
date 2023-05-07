package pl.flopczak.WorkerTaskScheduler.reservation.service;

import pl.flopczak.WorkerTaskScheduler.reservation.data.ReservationDTO;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface IReservationService {

    List<ReservationDTO> findAll();
    Set<Integer> getAvailable(Instant startTime);
}
