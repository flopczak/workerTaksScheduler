package pl.flopczak.WorkerTaskScheduler.reservation.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@Builder
public class ReservationDTO {

    private Long reservationId;

    private Integer taskType;
    private Integer workerName;

    private Instant startTime;
    private Instant endTime;

    public ReservationDTO(Reservation reservation) {
        this.reservationId = reservation.getReservationId();
        this.taskType = reservation.getTaskType();
        this.workerName = reservation.getWorkerName();
        this.startTime = reservation.getStartTime();
        this.endTime = reservation.getEndTime();
    }

    public static List<ReservationDTO> fromEntities(List<Reservation> reservations) {
        List<ReservationDTO> reservationDTOS = new ArrayList<>();
        for (Reservation reservation : reservations) {
            ReservationDTO dto = ReservationDTO.builder()
                    .reservationId(reservation.getReservationId())
                    .workerName(reservation.getWorkerName())
                    .taskType(reservation.getTaskType())
                    .startTime(reservation.getStartTime())
                    .endTime(reservation.getEndTime())
                    .build();
            reservationDTOS.add(dto);
        }
        return reservationDTOS;
    }
}
