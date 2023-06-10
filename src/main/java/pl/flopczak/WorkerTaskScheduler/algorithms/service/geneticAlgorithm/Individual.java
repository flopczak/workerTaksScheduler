package pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.AlgorithmType;
import pl.flopczak.WorkerTaskScheduler.reservation.data.ReservationDTO;
import pl.flopczak.WorkerTaskScheduler.task.data.Task;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class Individual {
    List<ReservationDTO> schedule;
    List<Task> availableTasks;
    Integer timeFitnesse;
    Integer dueFitnesse;




    //konstruktor generujący osobnika z losowym schedule o nazwie generateRandomIndividual

    private ReservationDTO makeRandomReservation() {
        //bierzemy/losujemy pierwsze zadanie z listy
        //sprawdzamy jacy pracownicy są dostępni do wykonaia go
        //losujemy pracownika do wykonania zadania
        //dodajemy rezerwacje z pracownikiem do osobnika pierwotnej populacji

        return new ReservationDTO.ReservationDTOBuilder()
                .reservationId(1L) //TODO generate unique
                .algorithmType(AlgorithmType.GAStrategy)
                .workerName(1) //TODO pick free
                .taskType(1)
                .taskId(1L) //TODO czy potrzebne? jeżeli będę usówał z listy zadań już przydzielone
//                .startTime() //TODO start from free time
//                .endTime() //TODO calculate end time for worker and start time
                .build();
    }
}
