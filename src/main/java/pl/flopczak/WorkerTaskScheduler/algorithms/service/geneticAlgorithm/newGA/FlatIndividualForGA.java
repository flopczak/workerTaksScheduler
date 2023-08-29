package pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.newGA;


import lombok.Data;
import pl.flopczak.WorkerTaskScheduler.SchedulingConstants;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.AlgorithmType;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.ChartData;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.DataRecord;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.FlatIndividualUtil;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.FlatReservation;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.LegendObject;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.TimePeriod;
import pl.flopczak.WorkerTaskScheduler.process.data.Process;
import pl.flopczak.WorkerTaskScheduler.statistics.data.StatisticDTO;
import pl.flopczak.WorkerTaskScheduler.statistics.service.StatisticService;
import pl.flopczak.WorkerTaskScheduler.task.data.Task;
import pl.flopczak.WorkerTaskScheduler.task.data.TaskDTO;

import java.time.Instant;
import java.util.*;

@Data
public class FlatIndividualForGA {
    private Double timeFitnesse;
    private Double dueFitnesse;
    private ChartData chartData;
    private List<Process> processes;
    private List<FlatReservation> schedule;
    private List<Task> availableTasks;
    private List<StatisticDTO> statistics;
    private StatisticService statisticService;

    private Map<Integer, List<TimePeriod>> workersAvailabilities;
    private Map<Long, Stack<TaskDTO>> taskStacks;
    Random rand = new Random();
    private static final AlgorithmType algorithmType = AlgorithmType.GAStrategy;


    public FlatIndividualForGA(List<Task> availableTasks, List<StatisticDTO> statistics, List<Process> processes, StatisticService statisticService) {
        this.availableTasks = availableTasks;
        this.statistics = statistics;
        this.processes = processes;
        workersAvailabilities = inicializeWorkerAvailabilityMap();
        this.schedule = new ArrayList<>();
        this.timeFitnesse = 0.0;
        this.dueFitnesse = 0.0;
        this.statisticService = statisticService;
        this.taskStacks = initializeTasksStacks();
    }


    public void fromEncodedSchedule(List<FlatReservationForCrossing> toDecode) {
        List<Long> processOrder = toDecode.stream().filter(process -> process.getTaskType().equals(1)).map(process -> process.getProcessId()).toList();

        for (Long processId : processOrder) {
            List<FlatReservationForCrossing> processToSchedule = toDecode.stream().filter(process -> process.getProcessId().equals(processId)).toList();
            scheduleEncodedProcess(processToSchedule);
        }
    }


    private void scheduleEncodedProcess(List<FlatReservationForCrossing> processToSchedule1) {
        List<FlatReservationForCrossing> processToSchedule = new ArrayList<>(processToSchedule1);
        processToSchedule.sort(Comparator.comparing(FlatReservationForCrossing::getTaskType));
        Integer lastTaskEndTime = 0;
        for (FlatReservationForCrossing task : processToSchedule) {
            Integer taskDurationInMinutes = statisticService.findByTaskTypeAndWorkerName(task.getTaskType(), task.getWorkerName()).getEstimatedTimeInSeconds() / 60;

            List<TimePeriod> workerPeriods = workersAvailabilities.get(task.getWorkerName());
            for (TimePeriod timePeriod : workerPeriods) {

                if (timePeriod.getStartTime() <= lastTaskEndTime && timePeriod.getEndTime() >= lastTaskEndTime + taskDurationInMinutes) {
                    scheduleTask(FlatReservation.builder()
                            .startTime(lastTaskEndTime)
                            .endTime(lastTaskEndTime + taskDurationInMinutes)
                            .workerName(task.getWorkerName())
                            .reservationId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
                            .processId(task.getProcessId())
                            .taskId(task.getTaskId())
                            .taskType(task.getTaskType())
                            .taskDueTimeInMinutes(task.getTaskDueTimeInMinutes())
                            .algorithmType(AlgorithmType.GAStrategy)
                            .build());
                    lastTaskEndTime = lastTaskEndTime + taskDurationInMinutes;
                    break;
                }
                else if(lastTaskEndTime < timePeriod.getStartTime() && timePeriod.getTimePeriodDuration() > taskDurationInMinutes) {
                    scheduleTask(FlatReservation.builder()
                            .startTime(timePeriod.getStartTime())
                            .endTime(timePeriod.getStartTime() + taskDurationInMinutes)
                            .workerName(task.getWorkerName())
                            .reservationId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
                            .processId(task.getProcessId())
                            .taskId(task.getTaskId())
                            .taskType(task.getTaskType())
                            .taskDueTimeInMinutes(task.getTaskDueTimeInMinutes())
                            .algorithmType(AlgorithmType.GAStrategy)
                            .build());
                    lastTaskEndTime = timePeriod.getStartTime() + taskDurationInMinutes;
                    break;
                }
            }
        }

    }

    public void initializeRandomSchedule() {
        int tasksToSchedule = availableTasks.size();
        int iteration = 0;
        while (tasksToSchedule > 0) {
            Integer worker = rand.nextInt(1, 6);
            List<TimePeriod> chosenWorkerAvailability = workersAvailabilities.get(worker);
            FlatReservation chosen = null;
            for (TimePeriod tp : chosenWorkerAvailability) {
                List<FlatReservation> candidates = prepareCandidates(tp, worker);
                iteration++;
                if (iteration > 10000) throw new IllegalStateException("nieskończona pętla");
                if (candidates.isEmpty()) continue;
                chosen = candidates.get(rand.nextInt(candidates.size()));
            }
            if (chosen == null) continue;
            scheduleTask(chosen);
            updateStack(chosen);
            tasksToSchedule--;
        }
        setTimeFitnesse(FlatIndividualUtil.calculateTimeFitness(getSchedule()));
        setDueFitnesse(FlatIndividualUtil.calculateDueFitness(getSchedule()));
    }

    private Integer getTaskBeforeEndTime(TaskDTO task) {
        if (task.getType().equals(1)) return 0;

        FlatReservation res = schedule.stream().
                filter(reservation -> reservation.getProcessId().equals(task.getProcessId()) && reservation.getTaskType().equals(task.getType() - 1))
                .findFirst().orElse(null);
        if (res == null) return 0;
        return res.getEndTime();
    }


    public void scheduleTask(FlatReservation reservation) {
        reservation.setReservationId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
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

    private List<FlatReservation> prepareCandidates(TimePeriod timePeriod, Integer workerName) {
        List<FlatReservation> candidates = new ArrayList<>();

        for (Map.Entry<Long, Stack<TaskDTO>> tasksEntry : taskStacks.entrySet()) {
            if (tasksEntry.getValue().isEmpty()) continue;
            TaskDTO topTask = tasksEntry.getValue().peek();
            Integer endTimeTaskBefore = getTaskBeforeEndTime(topTask);
            if (endTimeTaskBefore < timePeriod.getEndTime()) {
                StatisticDTO result = statisticService.findByTaskTypeAndWorkerName(topTask.getType(), workerName);
                if (endTimeTaskBefore > timePeriod.getStartTime()) {
                    Integer timeToCheck = timePeriod.getTimePeriodDuration() - (endTimeTaskBefore - timePeriod.getStartTime());
                    if (willTaskFillInTimePeriod(topTask, timeToCheck, workerName)) {
                        candidates.add(FlatReservation.builder()
                                .reservationId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
                                .taskId(topTask.getTaskId())
                                .processId(topTask.getProcessId())
                                .taskType(topTask.getType())
                                .algorithmType(algorithmType)
                                .workerName(workerName)
                                .startTime(endTimeTaskBefore)
                                .taskDueTimeInMinutes(changeDueToMinutes(topTask.getDueDate()))
                                .endTime(endTimeTaskBefore + result.getEstimatedTimeInSeconds() / 60)
                                .build());
                    }

                }
                if (endTimeTaskBefore <= timePeriod.getStartTime() && (willTaskFillInTimePeriod(topTask, timePeriod.getTimePeriodDuration(), workerName))) {
                    candidates.add(FlatReservation.builder()
                            .reservationId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
                            .taskId(topTask.getTaskId())
                            .processId(topTask.getProcessId())
                            .taskType(topTask.getType())
                            .algorithmType(algorithmType)
                            .workerName(workerName)
                            .startTime(timePeriod.getStartTime())
                            .taskDueTimeInMinutes(changeDueToMinutes(topTask.getDueDate()))
                            .endTime(timePeriod.getStartTime() + result.getEstimatedTimeInSeconds() / 60)
                            .build());

                }
            }
        }

        return candidates;
    }

    public boolean willTaskFillInTimePeriod(TaskDTO task, Integer timeInMinutes, Integer workerName) {
        StatisticDTO result = statisticService.findByTaskTypeAndWorkerName(task.getType(), workerName);
        if (result == null) {
            return false;
        }
        Integer timePeriod = result.getEstimatedTimeInSeconds() / 60;
        if (timeInMinutes > timePeriod) {
            return true;
        }
        return false;
    }

    private Map<Long, Stack<TaskDTO>> initializeTasksStacks() {
        Map<Long, Stack<TaskDTO>> toReturn = new HashMap<>();
        for (Process process : processes) {
            List<Task> processTaskList = process.getTaskList();
            Stack<TaskDTO> stack = new Stack<>();
            processTaskList.sort(Comparator.comparing(Task::getType).reversed());
            processTaskList.forEach(task -> stack.push(task.toDTO()));
            toReturn.put(process.getProcessId(), stack);
        }
        return toReturn;
    }

    public void initializeChartData() {
        ChartData chartData1 = new ChartData();
        chartData1.setLabel("Time");
        chartData1.setTitle("Workers Schedule");
        List<String> collors = Arrays.asList("yellow", "green", "blue", "purple", "red");
        List<LegendObject> legend = new ArrayList<>();
        Integer index = 1;
        List<String> rows = new ArrayList<>();
        for (String str : collors) {
            legend.add(new LegendObject(str, index.toString()));
            rows.add(index.toString());
            index++;
        }
        List<Integer> columns = new ArrayList<>();
        for (int i = 0; i < 36000; i++) {
            columns.add(i);
        }
        List<DataRecord> bars = new ArrayList<>();
        for (FlatReservation reservation : schedule) {
            bars.add(new DataRecord(reservation.getWorkerName().toString(),
                    reservation.getStartTime(), reservation.getEndTime(), collors.get(reservation.getTaskType() - 1)));
        }
        chartData1.setLegend(legend);
        chartData1.setRows(rows);
        chartData1.setColumns(columns);
        chartData1.setBars(bars);
        chartData1.setBar_height(0.5);
        setChartData(chartData1);
    }

    private Long changeDueToMinutes(Instant due) {
        return (due.getEpochSecond() - SchedulingConstants.BEGGINING_OF_SCHEDULING.getEpochSecond()) / 60;
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

    private void updateStack(FlatReservation task) {
        taskStacks.get(task.getProcessId()).pop();
    }
}
