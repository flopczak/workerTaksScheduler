package pl.flopczak.WorkerTaskScheduler.algorithms.service;

import lombok.Getter;
import lombok.Setter;
import pl.flopczak.WorkerTaskScheduler.SchedulingConstants;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.FlatIndividual;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.FlatIndividualUtil;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.FlatReservation;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.TimePeriod;
import pl.flopczak.WorkerTaskScheduler.process.data.Process;
import pl.flopczak.WorkerTaskScheduler.statistics.data.StatisticDTO;
import pl.flopczak.WorkerTaskScheduler.statistics.service.StatisticService;
import pl.flopczak.WorkerTaskScheduler.task.data.Task;
import pl.flopczak.WorkerTaskScheduler.task.data.TaskDTO;

import java.time.Instant;
import java.util.*;

@Getter
@Setter
public class BreadthFirstAlgorithm {
    private List<StatisticDTO> statistics;
    private List<Task> tasks;
    private Map<Long, Stack<TaskDTO>> taskStacks;
    private List<Process> processes;
    private FlatIndividual individual;
    private StatisticService statisticService;
    private static final AlgorithmType algorithmType = AlgorithmType.BLStrategy;

    public BreadthFirstAlgorithm(List<StatisticDTO> statistics, List<Task> tasks, List<Process> processes, StatisticService statisticService) {
        this.statistics = statistics;
        this.tasks = tasks;
        this.processes = processes;
        this.individual = new FlatIndividual(tasks, statistics);
        this.taskStacks = initializeTasksStacks();
        this.statisticService = statisticService;
    }


    public void scheduleTasks() {
        for (int i = 0; i < tasks.size(); i++) {
            Map<Integer, List<TimePeriod>> workersSchedule = individual.getWorkersAvailabilities();
            Integer workerWithEarliestSlot = getWorkerWithEarliestSlot(workersSchedule);
            TimePeriod earliestTimePeriod = getEarliestTimePeriodForWorker(workersSchedule, workerWithEarliestSlot);
            List<TaskDTO> candidates = prepareCandidates(earliestTimePeriod, workerWithEarliestSlot);
            TaskDTO mostUrgentTask = getMostUrgentTask(candidates);
            individual.scheduleTask(FlatReservation.builder()
                    .reservationId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE)
                    .taskId(mostUrgentTask.getTaskId())
                    .processId(mostUrgentTask.getProcessId())
                    .taskType(mostUrgentTask.getType())
                    .algorithmType(algorithmType)
                    .workerName(workerWithEarliestSlot)
                    .startTime(earliestTimePeriod.getStartTime())
                    .taskDueTimeInMinutes(changeDueToMinutes(mostUrgentTask.getDueDate()))
                    .endTime(earliestTimePeriod.getStartTime() + getEstimatedTimeInMinutesForTaskAndWorker(mostUrgentTask.getType(), workerWithEarliestSlot))
                    .build());
            updateStack(mostUrgentTask);
        }
        individual.setTimeFitnesse(FlatIndividualUtil.calculateTimeFitness(individual.getSchedule()));
        individual.setDueFitnesse(FlatIndividualUtil.calculateDueFitness(individual.getSchedule()));
//        try {
//            individual.initializeChartData();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void updateStack(TaskDTO task) {
        taskStacks.get(task.getProcessId()).pop();
    }

    private TaskDTO getMostUrgentTask(List<TaskDTO> candidates) {
        TaskDTO mostUrgentTask = null;
        for (TaskDTO candidate : candidates) {
            if (mostUrgentTask == null) {
                mostUrgentTask = candidate;
                continue;
            }
            if (candidate.getDueDate().isBefore(mostUrgentTask.getDueDate())) {
                mostUrgentTask = candidate;
            }
        }
        return mostUrgentTask;
    }


    private List<TaskDTO> prepareCandidates(TimePeriod timePeriod, Integer workerName) {
        //przejdz po mapie stosów i sporządź listę zadań mogących zmiescić się w tp
        List<TaskDTO> candidates = new ArrayList<>();

        for (Map.Entry<Long, Stack<TaskDTO>> tasksEntry : taskStacks.entrySet()) {
            if (tasksEntry.getValue().isEmpty()) continue;
            TaskDTO topTask = tasksEntry.getValue().peek();
            if (willTaskFillInTimePeriod(topTask.getType(), timePeriod.getTimePeriodDuration(), workerName)) {
                candidates.add(topTask);
            }
        }

        return candidates;
    }

    private Integer getWorkerWithEarliestSlot(Map<Integer, List<TimePeriod>> workersSchedule) {
        Integer workerWithEarliestSlot = null;
        Integer earliestStartTime = null;
        for (Map.Entry<Integer, List<TimePeriod>> entry : workersSchedule.entrySet()) {
            List<TimePeriod> timePeriods = entry.getValue();
            if (timePeriods.isEmpty()) {
                continue;
            }
            TimePeriod earliestTimePeriod = timePeriods.get(0);
            if (workerWithEarliestSlot == null) {
                workerWithEarliestSlot = entry.getKey();
                earliestStartTime = earliestTimePeriod.getStartTime();
            } else {
                if (earliestTimePeriod.getStartTime() < earliestStartTime) {
                    workerWithEarliestSlot = entry.getKey();
                    earliestStartTime = earliestTimePeriod.getStartTime();
                }
            }
        }
        return workerWithEarliestSlot;
    }

    private TimePeriod getEarliestTimePeriodForWorker(Map<Integer, List<TimePeriod>> workersSchedule, Integer workerName) {
        List<TimePeriod> timePeriods = workersSchedule.get(workerName);
        return timePeriods.get(0);
    }

    private Map<Long, Stack<TaskDTO>> initializeTasksStacks() {
        Map<Long, Stack<TaskDTO>> toReturn = new HashMap<>();
        for (Process process : processes) {
            List<Task> processTaskList = process.getTaskList();
            Stack<TaskDTO> stack = new Stack<>();
            processTaskList.sort(Comparator.comparing(Task::getType));
            processTaskList.forEach(task -> stack.push(task.toDTO()));
            toReturn.put(process.getProcessId(), stack);
        }
        return toReturn;
    }

    public boolean willTaskFillInTimePeriod(Integer taskType, Integer timeInMinutes, Integer workerName) {
        StatisticDTO result = statisticService.findByTaskTypeAndWorkerName(taskType, workerName);
        if (result == null) {
            return false;
        }
        Integer timePeriod = result.getEstimatedTimeInSeconds() / 60;
        if (timeInMinutes > timePeriod) {
            return true;
        }
        return false;
    }

    public Integer getEstimatedTimeInMinutesForTaskAndWorker(Integer taskType, Integer workerName) {
        StatisticDTO result = statisticService.findByTaskTypeAndWorkerName(taskType, workerName);
        if (result == null) {
            return null;
        }
        return result.getEstimatedTimeInSeconds() / 60;
    }

    private Long changeDueToMinutes(Instant due) {
        return (due.getEpochSecond() - SchedulingConstants.BEGGINING_OF_SCHEDULING.getEpochSecond()) / 60;
    }

}
