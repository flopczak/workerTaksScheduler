package pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.AlgorithmType;
import pl.flopczak.WorkerTaskScheduler.reservation.data.ReservationDTO;
import pl.flopczak.WorkerTaskScheduler.statistics.data.StatisticDTO;
import pl.flopczak.WorkerTaskScheduler.statistics.service.StatisticUtils;
import pl.flopczak.WorkerTaskScheduler.task.data.Task;
import pl.flopczak.WorkerTaskScheduler.worker.data.Worker;

import java.time.Instant;
import java.util.*;

@Data
@Builder
public class Individual {
//    List<ReservationDTO> schedule;
//    List<Task> availableTasks;
//    List<StatisticDTO> statistics;
//    Integer timeFitnesse;
//    Integer dueFitnesse;
//
//
//
//
//    //konstruktor generujący osobnika z losowym schedule o nazwie generateRandomIndividual
//
//    public Individual(List<Task> availableTasks,  List<StatisticDTO> statistics){
//        this.availableTasks = availableTasks;
//        this.statistics = statistics;
//        this.schedule = new ArrayList<>();
//        this.timeFitnesse = 0;
//        this.dueFitnesse = 0;
//    }
//
//    private void generateRandomSchedule(){
//        if (availableTasks.isEmpty()) {
//            throw new IllegalArgumentException("Lista z zadaniami jest pusta wszystkie zostały przydzielone");
//        }
//        Random random = new Random();
//        for(int i=0; i<availableTasks.size(); i++) {
//            int index = random.nextInt(availableTasks.size());
//            Task randomTask = availableTasks.remove(index);
//            schedule.add(makeRandomReservation(randomTask));
//        }
//    }
//
//
//    private ReservationDTO makeRandomReservation(Task task) {
//        //sprawdzamy jacy pracownicy są dostępni do wykonaia go
//
//        //losujemy pracownika do wykonania zadania
//        //dodajemy rezerwacje z pracownikiem do osobnika pierwotnej populacji
//
//        return new ReservationDTO.ReservationDTOBuilder()
//                .reservationId(1L) //TODO generate unique
//                .algorithmType(AlgorithmType.GAStrategy)
//                .workerName(1) //TODO pick free
//                .taskType(1)
//                .taskId(1L) //TODO czy potrzebne? jeżeli będę usówał z listy zadań już przydzielone
////                .startTime() //TODO start from free time
////                .endTime() //TODO calculate end time for worker and start time
//                .build();
//    }
//    private List<Worker> getAvailableWorkers() {
//        // if getAvailable is empty find worker that can do task in closest start time
//
//        // wyklucz zasoby które nie wykonają zadania w dostępnym oknie czasowym
//        // jeżeli lista zrobiła się pusta znajdz możliwe przydziały
//
//
//
//        //stwórz listę możliwych przydziałów
//
//
//        return null;
//    }
//
//    private List<ReservationDTO> getPossibleReservations(Task task) {
//
//        return null;
//    }
//
//    public Set<Integer> getAvailable(Instant startTime) {
//        Set<Integer> unavailableResources = new HashSet<>();
//
//        for (StatisticDTO stat : statistics) {
//            Instant endTime = StatisticUtils.estimatedEndTime(stat.getEstimatedTimeInSeconds(),startTime);
//            //to zmienić żey używało listy zamiast repo
//            List<ReservationDTO> unavailableResource = schedule.stream()
//                    .filter(reservation -> reservation.getStartTime().isAfter(startTime)&& reservation.getEndTime().isBefore(startTime)).toList();
//            if(!unavailableResource.isEmpty()) {
//                unavailableResources.add(stat.getWorkerName());
//            }
//        }
//
//        return StatisticUtils.getAvailableWorkers(statistics,unavailableResources);
//    }
}
