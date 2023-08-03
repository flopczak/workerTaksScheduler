package pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import org.springframework.util.ObjectUtils;
import pl.flopczak.WorkerTaskScheduler.SchedulingConstants;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.AlgorithmType;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.ChartData;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.DataRecord;
import pl.flopczak.WorkerTaskScheduler.statistics.data.StatisticDTO;
import pl.flopczak.WorkerTaskScheduler.task.data.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Data
public class FlatIndividual {
    private Double timeFitnesse;
    private Double dueFitnesse;
    private ChartData chartData;
    private List<FlatReservation> schedule;
    private List<Task> availableTasks;
    private List<StatisticDTO> statistics;

    private Map<Integer, List<TimePeriod>> workersAvailabilities;



    public FlatIndividual(List<Task> availableTasks, List<StatisticDTO> statistics) {
        this.availableTasks = availableTasks;
        this.statistics = statistics;
        workersAvailabilities = inicializeWorkerAvailabilityMap();
        this.schedule = new ArrayList<>();
        this.timeFitnesse = 0.0;
        this.dueFitnesse = 0.0;
    }

    public void initializeChartData() {
        ChartData chartData1 = new ChartData();
        chartData1.setLabel("Time");
        chartData1.setTitle("Workers Schedule");
        List<String> collors = Arrays.asList("yellow", "green", "blue", "purple", "red");
        List<LegendObject> legend = new ArrayList<>();
        Integer index = 1;
        List<String> rows = new ArrayList<>();
        for(String str : collors) {
            legend.add(new LegendObject(str, index.toString()));
            rows.add(index.toString());
            index++;
        }
        List<Integer> columns = new ArrayList<>();
        for(int i = 0; i < 36000; i++){
            columns.add(i);
        }
        List<DataRecord> bars = new ArrayList<>();
        for(FlatReservation reservation : schedule) {
            bars.add(new DataRecord(reservation.getWorkerName().toString(),
                    reservation.getStartTime(),reservation.getEndTime(), collors.get(reservation.getTaskType()-1)));
        }
        chartData1.setLegend(legend);
        chartData1.setRows(rows);
        chartData1.setColumns(columns);
        chartData1.setBars(bars);
        chartData1.setBar_height(0.5);
        setChartData(chartData1);
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        gson.toJson(chartData1, new FileWriter("C:\\Users\\Jakub\\Desktop\\Projekty\\json-to-gantt-master\\examples\\"+ System.currentTimeMillis()+schedule.get(0).getAlgorithmType()+".json"));
    }

    public void generateRandomSchedule() {
        if (availableTasks.isEmpty()) {
            throw new IllegalArgumentException("Lista z zadaniami jest pusta wszystkie zostały przydzielone");
        }
        Random random = new Random();
        int size = availableTasks.size();
        while (size > 0) {
            int index = random.nextInt(size);
            Task randomTask = availableTasks.remove(index);
            List<FlatReservation> possibleReservations = getPossibleReservations(randomTask);
            int index2 = random.nextInt(possibleReservations.size());
            scheduleTask(possibleReservations.get(index2));
            size--;
        }
        setTimeFitnesse(FlatIndividualUtil.calculateTimeFitness(schedule));
        setDueFitnesse(FlatIndividualUtil.calculateDueFitness(schedule));
    }

    public void scheduleTask(FlatReservation reservation) {
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
                    if (reservation.getStartTime() - timePeriod.getStartTime() > 1) {
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
            if (after.getStartTime() != null && after.getEndTime() != null) {
                workerSchedule.add(after);
            }
            workersAvailabilities.put(reservation.getWorkerName(), workerSchedule);
        } else {
            TimePeriod finalToDelete = toDelete;
            workerSchedule.removeIf(t -> t.getStartTime().equals(finalToDelete.getStartTime()) && t.getEndTime().equals(finalToDelete.getEndTime()));
            if (before.getStartTime() != null && before.getEndTime() != null) {
                workerSchedule.add(before);
            }
            if (after.getStartTime() != null && after.getEndTime() != null) {
                workerSchedule.add(after);
            }
            workersAvailabilities.put(reservation.getWorkerName(), workerSchedule);
        }
    }


    public List<FlatReservation> getPossibleReservations(Task task) {
        List<StatisticDTO> statisticForGivenTask = statistics.stream().filter(statistic -> Objects.equals(statistic.getTaskType(), task.getType())).toList();
        List<FlatReservation> toReturn = new ArrayList<>();
        Long dueTimeInMinutes = (task.getDueDate().getEpochSecond() - SchedulingConstants.BEGGINING_OF_SCHEDULING.getEpochSecond()) / 60;
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
                            .taskType(task.getType())
                            .processId(task.getProcess().getProcessId())
                            .taskDueTimeInMinutes(dueTimeInMinutes)
                            .workerName(workerEntry.getKey())
                            .algorithmType(AlgorithmType.GAStrategy)
                            .testNumber(-1)
                            .build());
                }
            }
        }
        return toReturn;
    }

    public FlatReservation getFastestPossibleReservation(Task task, Integer endTimeTaskBefore) {
        List<StatisticDTO> statisticForGivenTask = statistics.stream()
                .filter(statistic -> Objects.equals(statistic.getTaskType(), task.getType()))
                .toList();
        Long dueTimeInMinutes = (task.getDueDate().getEpochSecond() - SchedulingConstants.BEGGINING_OF_SCHEDULING.getEpochSecond()) / 60;

        StatisticDTO bestWorkerStatistics = new StatisticDTO();
        Integer bestWorkerEndTime = 0;
        Integer startOfWorkerFreeTimeToTest = 0;
        Integer startOfWorkerFreeTime = 0;
        for (Map.Entry<Integer, List<TimePeriod>> workerEntry : workersAvailabilities.entrySet()) {
            StatisticDTO statisticsForCurrentWorker = statisticForGivenTask.stream()
                    .filter(statistic -> statistic.getWorkerName().equals(workerEntry.getKey()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("statistics are null how this happen")); //TODO to denegerous but im sure that there is statistic for every worker
            for (TimePeriod workerFreeToWorkTimePeriod : workerEntry.getValue()) {
                boolean test = isTaskBeforeEndTimeInRangeOfTimePeriod(workerFreeToWorkTimePeriod, endTimeTaskBefore);
                if (!test) continue; //sprawdzenie czy zadanie może się zacząć w przedziale czasu w którym pracownik jest wolny

                //powinienem sprawdzić czy również może się w nim skończyć tu
                //następnie powinienem zwrócić przedział czasu w który zostanie zaalokowany
                startOfWorkerFreeTimeToTest = workerFreeToWorkTimePeriod.getStartTime();

                // can do the work
                if (workerFreeToWorkTimePeriod.getTimePeriodDuration() >= (statisticsForCurrentWorker.getEstimatedTimeInSeconds() / 60)) {
                    if (startOfWorkerFreeTimeToTest < endTimeTaskBefore) startOfWorkerFreeTimeToTest = endTimeTaskBefore;
                    if (bestWorkerEndTime == 0) {
                        bestWorkerEndTime = startOfWorkerFreeTimeToTest + (statisticsForCurrentWorker.getEstimatedTimeInSeconds() / 60);
                        bestWorkerStatistics = statisticsForCurrentWorker;
                        startOfWorkerFreeTime = startOfWorkerFreeTimeToTest;
                    }
                    if (bestWorkerEndTime > startOfWorkerFreeTimeToTest + (statisticsForCurrentWorker.getEstimatedTimeInSeconds() / 60)) {
                        bestWorkerEndTime = startOfWorkerFreeTimeToTest + (statisticsForCurrentWorker.getEstimatedTimeInSeconds() / 60);
                        bestWorkerStatistics = statisticsForCurrentWorker;
                        startOfWorkerFreeTime = startOfWorkerFreeTimeToTest;
                    }
                }
            }
        }

        if (ObjectUtils.isEmpty(bestWorkerStatistics))
            throw new IllegalStateException("There is no one to do task: " + task.getTaskId().toString());

        if(startOfWorkerFreeTime> bestWorkerEndTime)
            throw new IllegalStateException("sth happen and start time is bigger than end time " + task.getTaskId().toString());
        return FlatReservation.builder()
                .reservationId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
                .startTime(startOfWorkerFreeTime)
                .endTime(bestWorkerEndTime)
                .taskId(task.getTaskId())
                .taskType(task.getType())
                .processId(task.getProcess().getProcessId())
                .taskDueTimeInMinutes(dueTimeInMinutes)
                .workerName(bestWorkerStatistics.getWorkerName())
                .testNumber(-1)
                .build();
    }

    private boolean isTaskBeforeEndTimeInRangeOfTimePeriod(TimePeriod workerFreeToWorkTimePeriod, Integer endTimeTaskBefore) {
        if (ObjectUtils.isEmpty(workerFreeToWorkTimePeriod.getStartTime())) {
            throw new IllegalArgumentException("why");
        }
        return workerFreeToWorkTimePeriod.getEndTime() > endTimeTaskBefore;
    }




    private Map<Integer, List<TimePeriod>> inicializeWorkerAvailabilityMap() {
        Map<Integer, List<TimePeriod>> workerAvailabilityMap = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            List<TimePeriod> tempList = new ArrayList<>();
            tempList.add(new TimePeriod(0, 36000));
            workerAvailabilityMap.put(i, tempList);
        }
        return workerAvailabilityMap;
    }

    public void addToSchedule(List<FlatReservation> reservations) {
        reservations.forEach(this::scheduleTask);
    }
}
