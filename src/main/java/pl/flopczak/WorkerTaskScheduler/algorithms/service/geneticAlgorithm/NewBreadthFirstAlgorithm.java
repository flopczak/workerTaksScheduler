package pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.util.Pair;
import pl.flopczak.WorkerTaskScheduler.SchedulingConstants;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.AlgorithmType;
import pl.flopczak.WorkerTaskScheduler.process.data.Process;
import pl.flopczak.WorkerTaskScheduler.statistics.data.StatisticDTO;
import pl.flopczak.WorkerTaskScheduler.statistics.service.StatisticService;
import pl.flopczak.WorkerTaskScheduler.task.data.Task;
import pl.flopczak.WorkerTaskScheduler.task.data.TaskDTO;

import java.time.Instant;
import java.util.*;

import static pl.flopczak.WorkerTaskScheduler.statistics.service.StatisticUtils.getFastestWorkerForTask;

@Getter
@Setter
public class NewBreadthFirstAlgorithm {
    private List<StatisticDTO> statistics;
    private List<Task> tasks;
    private Map<Long, Stack<TaskDTO>> taskStacks;
    private List<Process> processes;
    private FlatIndividual individual;
    private StatisticService statisticService;
    private Map<Long, List<FlatReservation>> optimisticAllocationMap;
    private static final AlgorithmType algorithmType = AlgorithmType.BLStrategy;

    public NewBreadthFirstAlgorithm(List<StatisticDTO> statistics, List<Task> tasks, List<Process> processes, StatisticService statisticService) {
        this.statistics = statistics;
        this.tasks = tasks;
        this.processes = processes;
        this.individual = new FlatIndividual(tasks, statistics);
        this.taskStacks = initializeTasksStacks();
        this.statisticService = statisticService;
    }


    public void scheduleTasks() {
        int tasksToSchedule = tasks.size();
        int iteration = 0;
        while (tasksToSchedule > 0) {
            Map<Integer, List<TimePeriod>> workersSchedule = individual.getWorkersAvailabilities();
            FlatReservation chosen = null;
            Pair<Integer,TimePeriod> pair = getWorkerWithEarliestSlot2(workersSchedule);
                List<FlatReservation> candidates = prepareCandidates2(pair.getSecond(), pair.getFirst());
                iteration++;
                if (iteration > 10000) throw new IllegalStateException("");
                if (candidates.isEmpty()) continue;
                chosen = getMostUrgentTask(candidates);
            if (chosen == null) continue;
            individual.scheduleTask(chosen);
            updateStack(chosen);
            tasksToSchedule--;
        }
        individual.setTimeFitnesse(FlatIndividualUtil.calculateTimeFitness(individual.getSchedule()));
        individual.setDueFitnesse(FlatIndividualUtil.calculateDueFitness(individual.getSchedule()));
        try {
            individual.initializeChartData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateStack(FlatReservation task) {
        taskStacks.get(task.getProcessId()).pop();
    }

    private FlatReservation getMostUrgentTask(List<FlatReservation> candidates) {
        FlatReservation mostUrgentTask = null;
        for (FlatReservation candidate : candidates) {
            if (mostUrgentTask == null) {
                mostUrgentTask = candidate;
                continue;
            }
            if (candidate.getTaskDueTimeInMinutes() < mostUrgentTask.getTaskDueTimeInMinutes()) {
                mostUrgentTask = candidate;
            }
        }
        return mostUrgentTask;
    }

    private FlatReservation getMostUrgentTask2(List<FlatReservation> candidates) {
        FlatReservation mostUrgentTask = null;
        Double mostUrgentPenalty = 0.0;
        for (FlatReservation candidate : candidates) {
            Double candidatePenalty = 1.0 - (1.0 / (2.0 * taskStacks.get(candidate.getProcessId()).size()));
            if (mostUrgentTask == null) {
                mostUrgentTask = candidate;
                mostUrgentPenalty = candidatePenalty;
                continue;
            }

            if (Objects.equals(candidatePenalty, mostUrgentPenalty) && candidate.getTaskDueTimeInMinutes() < mostUrgentTask.getTaskDueTimeInMinutes()) {
                mostUrgentTask = candidate;
            }

            if (candidatePenalty < mostUrgentPenalty) {
                mostUrgentTask = candidate;
                mostUrgentPenalty = candidatePenalty;
            }
        }
        return mostUrgentTask;
    }


    private List<TaskDTO> prepareCandidates(TimePeriod timePeriod, Integer workerName) {
        List<TaskDTO> candidates = new ArrayList<>();

        for (Map.Entry<Long, Stack<TaskDTO>> tasksEntry : taskStacks.entrySet()) {
            if (tasksEntry.getValue().isEmpty()) continue;
            TaskDTO topTask = tasksEntry.getValue().peek();
            Integer endTimeTaskBefore = 0;
            if (topTask.getType() != 1) {
                List<FlatReservation> processReservations = individual.getSchedule().stream().filter(task -> task.getProcessId().equals(topTask.getProcessId())).toList();
                FlatReservation reservationBefore = processReservations.stream()
                        .filter(flatReservation -> flatReservation.getTaskType().equals(topTask.getType() - 1)).findFirst().orElse(null);
                if (reservationBefore != null) {
                    endTimeTaskBefore = reservationBefore.getEndTime();
                }
            }

            if (willTaskFillInTimePeriod(topTask.getType(), timePeriod.getTimePeriodDuration(), workerName)) {
                candidates.add(topTask);
            }
        }

        return candidates;
    }


    private Pair<Integer,TimePeriod> getWorkerWithEarliestSlot2(Map<Integer, List<TimePeriod>> workersSchedule) {
        TimePeriod workerWithEarliestSlot = null;
        Integer workerWithEarliestSlotName = null;
        Integer earliestStartTime = null;
        for (Map.Entry<Integer, List<TimePeriod>> entry : workersSchedule.entrySet()) {
            List<TimePeriod> timePeriods = entry.getValue();
            if (timePeriods.isEmpty()) {
                continue;
            }
            List<TaskDTO> currentPossibleTasks = currentPossibleTasks();
            for (TimePeriod tp : timePeriods) {
                boolean canDoAnyTask = canDoAnyTask(tp, currentPossibleTasks, entry.getKey());
                if (canDoAnyTask) {
                    if (workerWithEarliestSlot == null) {
                        workerWithEarliestSlot = tp;
                        workerWithEarliestSlotName = entry.getKey();
                        earliestStartTime = tp.getStartTime();
                    } else {
                        if (tp.getStartTime() < earliestStartTime) {
                            workerWithEarliestSlot = tp;
                            workerWithEarliestSlotName = entry.getKey();
                            earliestStartTime = tp.getStartTime();
                        }
                    }
                }
            }

        }
        return Pair.of(workerWithEarliestSlotName, workerWithEarliestSlot);
    }


    private boolean canDoAnyTask(TimePeriod timePeriod, List<TaskDTO> tasks, Integer workerName) {
        boolean toReturn = false;
        for (TaskDTO task : tasks) {
            Integer endTimeTaskBefore = getTaskBeforeEndTime(task);
            StatisticDTO result = statisticService.findByTaskTypeAndWorkerName(task.getType(), workerName);

            if (endTimeTaskBefore < timePeriod.getEndTime()
                    && endTimeTaskBefore <= timePeriod.getStartTime() && (willTaskFillInTimePeriod2(task, timePeriod.getTimePeriodDuration(), workerName))) {
                toReturn = true;
                break;
            }

        }

        return toReturn;
    }

    private List<TaskDTO> currentPossibleTasks() {
        List<TaskDTO> currentPossibleTasks = new ArrayList<>();
        for (Map.Entry<Long, Stack<TaskDTO>> tasksEntry : taskStacks.entrySet()) {
            if (tasksEntry.getValue().isEmpty()) continue;
            currentPossibleTasks.add(tasksEntry.getValue().peek());
        }
        return currentPossibleTasks;
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


    private Long changeDueToMinutes(Instant due) {
        return (due.getEpochSecond() - SchedulingConstants.BEGGINING_OF_SCHEDULING.getEpochSecond()) / 60;
    }


    private List<FlatReservation> prepareCandidates2(TimePeriod timePeriod, Integer workerName) {
        List<FlatReservation> candidates = new ArrayList<>();

        for (Map.Entry<Long, Stack<TaskDTO>> tasksEntry : taskStacks.entrySet()) {
            if (tasksEntry.getValue().isEmpty()) continue;
            TaskDTO topTask = tasksEntry.getValue().peek();
            Integer endTimeTaskBefore = getTaskBeforeEndTime(topTask);
            if (endTimeTaskBefore < timePeriod.getEndTime()) {
                StatisticDTO result = statisticService.findByTaskTypeAndWorkerName(topTask.getType(), workerName);
                if (endTimeTaskBefore > timePeriod.getStartTime()) {
                    Integer timeToCheck = timePeriod.getTimePeriodDuration() - (endTimeTaskBefore - timePeriod.getStartTime());
                    if (willTaskFillInTimePeriod2(topTask, timeToCheck, workerName)) {
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
                if (endTimeTaskBefore <= timePeriod.getStartTime() && (willTaskFillInTimePeriod2(topTask, timePeriod.getTimePeriodDuration(), workerName))) {
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

    public boolean willTaskFillInTimePeriod2(TaskDTO task, Integer timeInMinutes, Integer workerName) {
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

    private Integer getTaskBeforeEndTime(TaskDTO task) {
        if (task.getType().equals(1)) return 0;

        FlatReservation res = individual.getSchedule().stream().
                filter(reservation -> reservation.getProcessId().equals(task.getProcessId()) && reservation.getTaskType().equals(task.getType() - 1))
                .findFirst().orElse(null);
        if (res == null) return 0;
        return res.getEndTime();
    }
}
