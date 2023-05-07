package pl.flopczak.WorkerTaskScheduler.reservation.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pl.flopczak.WorkerTaskScheduler.reservation.data.Reservation;

import java.time.Instant;
import java.util.List;

public interface ReservationRepository extends CrudRepository<Reservation, Long> {
    List<Reservation> findAll();
    @Query(value = "SELECT * FROM reservations WHERE start_time > :endTime AND worker_name = :workerName", nativeQuery = true)
    List<Reservation> findByEndTimeBeforeOtherStart(Instant endTime, Integer workerName);
}
