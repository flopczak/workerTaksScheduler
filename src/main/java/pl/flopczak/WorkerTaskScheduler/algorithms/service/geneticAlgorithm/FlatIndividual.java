package pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm;

import lombok.Data;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.AlgorithmType;
import pl.flopczak.WorkerTaskScheduler.statistics.data.StatisticDTO;
import pl.flopczak.WorkerTaskScheduler.task.data.Task;
import pl.flopczak.WorkerTaskScheduler.worker.data.Worker;

import java.util.*;

@Data
public class FlatIndividual {

    List<FlatReservation> schedule;
    List<Task> availableTasks;
    List<StatisticDTO> statistics;
    Integer timeFitnesse;
    Integer dueFitnesse;
    Map<Integer, List<TimePeriod>> workersAvailabilities = new HashMap<>();


    //konstruktor generujący osobnika z losowym schedule o nazwie generateRandomIndividual

    public FlatIndividual(List<Task> availableTasks, List<StatisticDTO> statistics) {
        this.availableTasks = availableTasks;
        this.statistics = statistics;
        workersAvailabilities = inicializeWorkerAvailabilityMap();
        this.schedule = new ArrayList<>();
        this.timeFitnesse = 0;
        this.dueFitnesse = 0;
    }

    public void generateRandomSchedule() {
        if (availableTasks.isEmpty()) {
            throw new IllegalArgumentException("Lista z zadaniami jest pusta wszystkie zostały przydzielone");
        }
        Random random = new Random();
        int size = availableTasks.size();
        while(size > 0) {
            int index = random.nextInt(size);
            Task randomTask = availableTasks.remove(index);
            List<FlatReservation> possibleReservations = getPossibleReservations(randomTask);
            int index2 = random.nextInt(possibleReservations.size());
            scheduleTask(possibleReservations.get(index2));
            size--;
        }
    }

    private void scheduleTask(FlatReservation reservation) {
        reservation.setReservationId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE); //generate uuid
        schedule.add(reservation);
        List<TimePeriod> workerSchedule = workersAvailabilities.get(reservation.getWorkerName());
        TimePeriod before = new TimePeriod();
        TimePeriod after = new TimePeriod();
        TimePeriod toDelete = new TimePeriod();
        for (TimePeriod timePeriod : workerSchedule) {
            if (timePeriod.getStartTime() <= reservation.getStartTime() && timePeriod.getEndTime() >= reservation.getEndTime()) {
                if (timePeriod.getStartTime() == 0) {
                    after.setStartTime(reservation.getEndTime());
                    after.setEndTime(timePeriod.getEndTime());
                    toDelete = timePeriod;
                } else {
                    if(reservation.getStartTime() - timePeriod.getStartTime() > 1) {
                        before.setStartTime(timePeriod.getStartTime());
                        before.setEndTime(reservation.getStartTime());
                    }
                    after.setStartTime(reservation.getEndTime());
                    after.setEndTime(timePeriod.getEndTime());
                    toDelete = timePeriod;
                }
                break;
            }
//            else throw new IllegalArgumentException("How it is even possible ive checked if i can make reservation before proposing it!");
        }
        if (before.getStartTime() == null) {
            TimePeriod finalToDelete = toDelete;
            workerSchedule.removeIf(t -> t.getStartTime().equals(finalToDelete.getStartTime()) && t.getEndTime().equals(finalToDelete.getEndTime()));
            workerSchedule.add(after);
            workersAvailabilities.put(reservation.getWorkerName(), workerSchedule);
        } else {
            TimePeriod finalToDelete = toDelete;
            workerSchedule.removeIf(t -> t.getStartTime().equals(finalToDelete.getStartTime()) && t.getEndTime().equals(finalToDelete.getEndTime()));
            workerSchedule.add(before);
            workerSchedule.add(after);
            workersAvailabilities.put(reservation.getWorkerName(), workerSchedule);
        }
    }

    private List<Worker> getAvailableWorkers() {
        // if getAvailable is empty find worker that can do task in closest start time

        // wyklucz zasoby które nie wykonają zadania w dostępnym oknie czasowym
        // jeżeli lista zrobiła się pusta znajdz możliwe przydziały


        //stwórz listę możliwych przydziałów


        return null;
    }

    private List<FlatReservation> getPossibleReservations(Task task) {
        List<StatisticDTO> statisticForGivenTask = statistics.stream().filter(statistic -> Objects.equals(statistic.getTaskType(), task.getType())).toList();
        List<FlatReservation> toReturn = new ArrayList<>();

        for (Map.Entry<Integer, List<TimePeriod>> workerEntry : workersAvailabilities.entrySet()) {
            StatisticDTO statisticsForCurrentWorker = statisticForGivenTask.stream()
                    .filter(statistic -> statistic.getWorkerName().equals(workerEntry.getKey()))
                    .findFirst()
                    .orElse(null); //TODO to denegerous but im sure that there is statistic for every worker
            for (TimePeriod workerFreeToWorkTimePeriod : workerEntry.getValue()) {
                if (workerFreeToWorkTimePeriod.getTimePeriodDuration() >= (statisticsForCurrentWorker.getEstimatedTimeInSeconds() / 60)) {
                    toReturn.add(FlatReservation.builder()
                            .startTime(workerFreeToWorkTimePeriod.getStartTime())
                            .endTime(workerFreeToWorkTimePeriod.getStartTime() + (statisticsForCurrentWorker.getEstimatedTimeInSeconds() / 60))
                            .taskId(task.getTaskId())
                            .workerName(workerEntry.getKey())
                            .algorithmType(AlgorithmType.GAStrategy)
                            .testNumber(-1)
                            .build());
                }
            }
        }
        return toReturn;
    }

    private Map<Integer, List<TimePeriod>> inicializeWorkerAvailabilityMap() {
        Map<Integer, List<TimePeriod>> workerAvailabilityMap = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            List<TimePeriod> tempList = new ArrayList<>();
            tempList.add(new TimePeriod(0, 3600));
            workerAvailabilityMap.put(i, tempList);
        }
        return workerAvailabilityMap;
    }
}
