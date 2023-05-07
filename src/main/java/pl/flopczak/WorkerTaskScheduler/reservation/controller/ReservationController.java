package pl.flopczak.WorkerTaskScheduler.reservation.controller;


import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.flopczak.WorkerTaskScheduler.reservation.data.ReservationDTO;
import pl.flopczak.WorkerTaskScheduler.reservation.service.ReservationService;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping(path = "/findAll")
    public List<ReservationDTO> findAll() {
        return reservationService.findAll();
    }


    @GetMapping(path = "/getAvailable")
    public Set<Integer> getAvailable(@RequestParam(value = "startTime", defaultValue = "2022-01-01T08:00:00Z") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime) {
        return reservationService.getAvailable(startTime);
    }
}
