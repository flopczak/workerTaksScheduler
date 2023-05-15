package pl.flopczak.WorkerTaskScheduler.reservation.service;

import org.springframework.stereotype.Service;
import pl.flopczak.WorkerTaskScheduler.reservation.data.Reservation;
import pl.flopczak.WorkerTaskScheduler.reservation.data.ReservationDTO;
import pl.flopczak.WorkerTaskScheduler.reservation.repository.ReservationRepository;
import pl.flopczak.WorkerTaskScheduler.statistics.data.StatisticDTO;
import pl.flopczak.WorkerTaskScheduler.statistics.service.StatisticService;
import pl.flopczak.WorkerTaskScheduler.statistics.service.StatisticUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReservationService implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final StatisticService statisticService;

    public ReservationService(ReservationRepository reservationRepository, StatisticService statisticService) {
        this.reservationRepository = reservationRepository;
        this.statisticService = statisticService;
    }


    @Override
    public List<ReservationDTO> findAll() {
        return ReservationDTO.fromEntities(reservationRepository.findAll());
    }



    /**
     * Zwraca dostępne zasoby mogce wykonać zadanie
     *
     * @param startTime
     * @return availableWorkers
     */
    @Override
    public Set<Integer> getAvailable(Instant startTime) {
        List<StatisticDTO> statistics = statisticService.findAll();
        Set<Integer> unavailableResources = new HashSet<>();

        for (StatisticDTO stat : statistics) {
            Instant endTime = StatisticUtils.estimatedEndTime(stat.getEstimatedTimeInSeconds(),startTime);
            List<ReservationDTO> unavailableResource = ReservationDTO.fromEntities(reservationRepository.findByEndTimeBeforeOtherStart(endTime, stat.getWorkerName()));
            if(!unavailableResource.isEmpty()) {
                unavailableResources.add(stat.getWorkerName());
            }
        }

        return StatisticUtils.getAviableWorkers(statistics,unavailableResources);
    }

    //może zwracać bool żeby było wiadomo czy się udało
    //TODO dodać sprawdzenie rezerwacji
    //TODO może w algortmie działać na liscie i potem dodawać całą listę rezerwacji a nie działać na bazie cały czas
    public void makeReservation(Reservation reservation) {
        reservationRepository.save(reservation);
    }
}
