package pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm;

import lombok.Data;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.BreadthFirstAlgorithm;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.DepthFirstAlgorithm;
import pl.flopczak.WorkerTaskScheduler.process.data.Process;
import pl.flopczak.WorkerTaskScheduler.process.service.ProcessService;
import pl.flopczak.WorkerTaskScheduler.statistics.data.StatisticDTO;
import pl.flopczak.WorkerTaskScheduler.statistics.service.StatisticService;
import pl.flopczak.WorkerTaskScheduler.task.data.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Data
public class GeneticAlgorithm {
    private List<FlatIndividual> population;
    private List<StatisticDTO> statistics;
    private List<Process> processes;
    private List<Task> tasks;
    private StatisticService statisticService;
    private ProcessService processService;
    private Random random;
    private List<List<Fitnesses>> fitnesses;
    private FlatIndividual bestEver;


    //TODO dodać fitness będący utylizacją czasu

    public GeneticAlgorithm(List<Task> tasks, List<StatisticDTO> statistics, ProcessService processService, StatisticService statisticService, List<Process> processes) {
        this.statistics = statistics;
        this.tasks = tasks;
        this.random = new Random();
        this.processService = processService;
        this.processes = processes;
        this.statisticService = statisticService;
        this.fitnesses = new ArrayList<>();
        population = initializePopulation();
        bestEver = null;
    }

    private List<FlatIndividual> initializePopulation() {
        List<FlatIndividual> toReturn = new ArrayList<>();
        List<Task> taskCopy2 = new ArrayList<>(tasks);
        List<Task> taskCopy3 = new ArrayList<>(tasks);
        List<Process> processCopy2 = processService.findAll();
        List<Process> processCopy3 = processService.findAll();
        for (int i = 0; i < GeneticAlgorithmConstans.POPULATION_SIZE - 2; i++) {
            List<Task> taskCopy = new ArrayList<>(tasks);
            FlatIndividual individual = new FlatIndividual(taskCopy, statistics);
            individual.generateRandomSchedule();
            toReturn.add(individual);
        }

        DepthFirstAlgorithm dp = new DepthFirstAlgorithm(statistics, taskCopy2, processCopy2);
        BreadthFirstAlgorithm bd = new BreadthFirstAlgorithm(statistics, taskCopy3, processCopy3, statisticService);
        dp.scheduleTasks();
        bd.scheduleTasks();
        toReturn.add(random.nextInt(GeneticAlgorithmConstans.POPULATION_SIZE - 2), dp.getIndividual());
        toReturn.add(random.nextInt(GeneticAlgorithmConstans.POPULATION_SIZE - 2), bd.getIndividual());
        return toReturn;
    }

    public void mainLoop() {
        for (int i = 0; i < GeneticAlgorithmConstans.MAX_GENERATIONS; i++) {
            //selekcja n - 1 osobników
            List<FlatIndividual> newPopulation = new ArrayList<>();
            for (int j = 0; j < GeneticAlgorithmConstans.POPULATION_SIZE - 58; j++) {
                FlatIndividual winner = tournamentSelection(3);
                winner.initializeChartData();
                newPopulation.add(winner);
            }
            //crossover i mutacja 1 z osobników

            newPopulation.sort(Comparator.comparing(flatIndividual -> flatIndividual.getDueFitnesse() + flatIndividual.getTimeFitnesse()));
            Integer lastElement = newPopulation.size() - 1;

            for (int j = 0; j < 58; j++) {
                FlatIndividual childIndividual = crossover(newPopulation.get(random.nextInt(newPopulation.size())), newPopulation.get(random.nextInt(newPopulation.size())));
//                childIndividual.initializeChartData();
                newPopulation.add(childIndividual);
            }

            for (FlatIndividual individual : population) {
                if (bestEver == null) {
                    bestEver = individual;
                    continue;
                }
                Double combinedFitnessBestEver = (bestEver.getDueFitnesse() + bestEver.getTimeFitnesse()) / 2.0;
                Double combinedFitnessCurrentBest = (individual.getDueFitnesse() + individual.getTimeFitnesse()) / 2.0;
                if (combinedFitnessBestEver < combinedFitnessCurrentBest) {
                    bestEver = individual;
                    bestEver.setTimeFitnesse(FlatIndividualUtil.calculateTimeFitness(bestEver.getSchedule()));
                    bestEver.setDueFitnesse(FlatIndividualUtil.calculateDueFitness(bestEver.getSchedule()));
                }

            }


            //zapisz historyczne fitnessy
            saveFitnesses(newPopulation);

            setPopulation(newPopulation);
        }
        bestEver.initializeChartData();
    }

    private void saveFitnesses(List<FlatIndividual> newPopulation) {
        List<Fitnesses> toReturn = new ArrayList<>();
        for (int i = 0; i < newPopulation.size() - 1; i++) {
            FlatIndividual individual = newPopulation.get(i);
            individual.setTimeFitnesse(FlatIndividualUtil.calculateTimeFitness(individual.getSchedule()));
            individual.setDueFitnesse(FlatIndividualUtil.calculateDueFitness(individual.getSchedule()));
            toReturn.add(new Fitnesses(individual.getDueFitnesse(), individual.getTimeFitnesse()));
        }
        fitnesses.add(toReturn);

    }

    private FlatIndividual tournamentSelection(Integer tournamentSize) {
        List<FlatIndividual> tournament = new ArrayList<>(tournamentSize);


        for (int i = 0; i < tournamentSize; i++) {
            int randomIndex = random.nextInt(population.size());
            tournament.add(population.get(randomIndex));
        }

        FlatIndividual best = tournament.get(0);

//        for (int i = 1; i < tournament.size(); i++) {
//            Double timePercentage = best.getTimeFitnesse() / tournament.get(i).getTimeFitnesse();
//            Double duePercentage = best.getDueFitnesse() / tournament.get(i).getDueFitnesse();
//            Double growth = timePercentage + duePercentage;
//            if (growth < 2) { // TODO to check
//                best = tournament.get(i);
//            }
//
//        }
//        Double bestTimePercentage = bestEver.getTimeFitnesse() / best.getTimeFitnesse();
//        Double bestDuePercentage = bestEver.getDueFitnesse() / best.getDueFitnesse();
//        Double growth = bestTimePercentage + bestDuePercentage;
//        if (growth < 2) { // TODO to check
//            bestEver = best;
//            bestEver.setTimeFitnesse(FlatIndividualUtil.calculateTimeFitness(bestEver.getSchedule()));
//            bestEver.setDueFitnesse(FlatIndividualUtil.calculateDueFitness(bestEver.getSchedule()));
//        }

        for (int i = 1; i < tournament.size(); i++) {
            Double combinedFitnessBest = (best.getDueFitnesse() + best.getTimeFitnesse()) / 2.0;
            Double combinedFitnessCurrent = (tournament.get(i).getDueFitnesse() + tournament.get(i).getTimeFitnesse()) / 2.0;
            if (combinedFitnessBest < combinedFitnessCurrent) {
                best = tournament.get(i);
            }
        }


        return best;
    }


    private FlatIndividual crossover(FlatIndividual parent1, FlatIndividual parent2) {
        FlatIndividual child = new FlatIndividual(tasks, statistics);
        Long closestProcessId = -1L;
        Integer bestDistance = null;

//        for (Process process : processes) {
//            Integer distance = calculateDistance(parent1, parent2, process);
//            if (bestDistance == null || distance < bestDistance) {
//                bestDistance = distance;
//                closestProcessId = process.getProcessId();
//            }
//        }
        //trying random process instead closest bo wtedy chyba zawsze wybiera ten sam bo mam za mało procesów nic to nie dało
        closestProcessId = processes.get(random.nextInt(processes.size())).getProcessId();


        FlatIndividual chosenParent = random.nextBoolean() ? parent1 : parent2;

        Long finalClosestProcessId = closestProcessId;
        List<FlatReservation> scheduledProcessFromParent =
                new ArrayList<>(chosenParent.getSchedule().stream()
                        .filter(res -> res.getProcessId().equals(finalClosestProcessId)).toList());
        child.addToSchedule(scheduledProcessFromParent);

        for (Process process : processes) {
            if (process.getProcessId().equals(finalClosestProcessId)) continue;

            List<Task> tasksToSchedule = process.getTaskList();
            tasksToSchedule.sort(Comparator.comparing(Task::getType));
            List<FlatReservation> toReturn = new ArrayList<>();
            Integer endTimeTaskBefore = 0;
            for (Task task : tasksToSchedule) {
//                FlatReservation bestReservation = child.getFastestPossibleReservation(task, endTimeTaskBefore);
//                endTimeTaskBefore = bestReservation.getEndTime();
//                toReturn.add(bestReservation);
                List<FlatReservation> possibleReservations = child.getPossibleReservations(task);
                int index2 = random.nextInt(possibleReservations.size());
                toReturn.add(possibleReservations.get(index2));
            }
            child.addToSchedule(toReturn);
        }

        return child;
    }

    Integer calculateDistance(FlatIndividual parent1, FlatIndividual parent2, Process process) {
        Integer distance = 0;
        List<FlatReservation> scheduledProcess1 = new ArrayList<>(parent1.getSchedule().stream().filter(res -> res.getProcessId().equals(process.getProcessId())).toList());
        scheduledProcess1.sort(Comparator.comparing(FlatReservation::getTaskType));
        List<FlatReservation> scheduledProcess2 = new ArrayList<>(parent2.getSchedule().stream().filter(res -> res.getProcessId().equals(process.getProcessId())).toList());
        scheduledProcess2.sort(Comparator.comparing(FlatReservation::getTaskType));
        for (int i = 0; i < 5; i++) {
            if (!scheduledProcess1.get(i).getWorkerName().equals(scheduledProcess2.get(i).getWorkerName())) {
                distance += 525;
                continue;
            }
            distance += Math.abs(scheduledProcess1.get(i).getStartTime() - scheduledProcess2.get(i).getStartTime());
        }


        return distance;
    }


}
