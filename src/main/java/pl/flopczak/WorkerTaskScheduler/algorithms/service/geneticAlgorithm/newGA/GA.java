package pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.newGA;

import lombok.Data;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.FlatIndividualUtil;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.GeneticAlgorithmConstans;
import pl.flopczak.WorkerTaskScheduler.process.data.Process;
import pl.flopczak.WorkerTaskScheduler.process.service.ProcessService;
import pl.flopczak.WorkerTaskScheduler.statistics.data.StatisticDTO;
import pl.flopczak.WorkerTaskScheduler.statistics.service.StatisticService;
import pl.flopczak.WorkerTaskScheduler.task.data.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@Data
public class GA {
    private List<FlatIndividualForGA> population;
    private List<StatisticDTO> statistics;
    private List<Process> processes;
    private List<Task> tasks;
    private StatisticService statisticService;
    private ProcessService processService;
    private Random random;
    private List<List<NewFitnesses>> fitnesses;
    private FlatIndividualForGA bestEver;


    public GA(List<Task> tasks, List<StatisticDTO> statistics, ProcessService processService, StatisticService statisticService, List<Process> processes) {
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


    public void mainLoop() {
        for (int i = 0; i < GeneticAlgorithmConstans.MAX_GENERATIONS; i++) {
            List<FlatIndividualForGA> newPopulation = new ArrayList<>();

            if (population.size() > 0) {
                for (int j = 0; j < population.size(); j++) {
                    FlatIndividualForGA individual = population.get(j);
                    individual.setTimeFitnesse(FlatIndividualUtil.calculateTimeFitness(individual.getWorkersAvailabilities()));
                    individual.setDueFitnesse(FlatIndividualUtil.calculateDueFitness(individual.getSchedule()));
                }
            }
            //selekcja
            FlatIndividualForGA elite = evaluateFitnesses(population);

            for (int j = 0; j < GeneticAlgorithmConstans.POPULATION_SIZE - (GeneticAlgorithmConstans.POPULATION_SIZE * (1 - GeneticAlgorithmConstans.CROSSOVER_RATE)); j++) {
                FlatIndividualForGA winner = tournamentSelection(GeneticAlgorithmConstans.TOURNAMET_SIZE);
                winner.initializeChartData();
                newPopulation.add(winner);
            }

            for (int j = 0; j < GeneticAlgorithmConstans.POPULATION_SIZE - (GeneticAlgorithmConstans.POPULATION_SIZE * GeneticAlgorithmConstans.CROSSOVER_RATE); j++) {

                FlatIndividualForGA winner = tournamentSelection(GeneticAlgorithmConstans.TOURNAMET_SIZE);
                FlatIndividualForGA winner2 = tournamentSelection(GeneticAlgorithmConstans.TOURNAMET_SIZE);
                //kodowanie
                List<FlatReservationForCrossing> parent1 = winner.getSchedule().stream().map(record -> record.toCrossing()).toList();
                List<FlatReservationForCrossing> parent2 = winner2.getSchedule().stream().map(record -> record.toCrossing()).toList();

                List<FlatReservationForCrossing> childScheduleToDecode = crossoverAndMutate(parent1, parent2);

                //dekodowanie osobników
                FlatIndividualForGA child = new FlatIndividualForGA(tasks, statistics, processes, statisticService);
                child.fromEncodedSchedule(childScheduleToDecode);
                child.setTimeFitnesse(FlatIndividualUtil.calculateTimeFitness(child.getWorkersAvailabilities()));
                child.setDueFitnesse(FlatIndividualUtil.calculateDueFitness(child.getSchedule()));
//                child.initializeChartData();
                newPopulation.add(child);
            }
            newPopulation.add(elite);


            saveFitnesses(newPopulation);
        }
        bestEver.initializeChartData();
    }


    private List<FlatReservationForCrossing> crossoverAndMutate(List<FlatReservationForCrossing> parent1, List<FlatReservationForCrossing> parent2) {
        List<Integer> processIds = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6));
        List<FlatReservationForCrossing> child1 = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Integer randomProcessId = processIds.remove(random.nextInt(processIds.size()));
            //dupa czy chodzi że long
            List<FlatReservationForCrossing> processFromParent1 = parent1.stream().filter(reservation -> reservation.getProcessId().equals(randomProcessId.longValue())).toList();
            if (willMutate()) {
                processFromParent1 = mutate(processFromParent1);
            }
            child1 = Stream.concat(child1.stream(), processFromParent1.stream()).toList();

            Integer randomProcessId2 = processIds.remove(random.nextInt(processIds.size()));
            List<FlatReservationForCrossing> processFromParent2 = parent2.stream().filter(reservation -> reservation.getProcessId().equals(randomProcessId2.longValue())).toList();
            if (willMutate()) {
                processFromParent2 = mutate(processFromParent2);
            }
            child1 = Stream.concat(child1.stream(), processFromParent2.stream()).toList();
        }


        return child1;
    }


    private List<FlatReservationForCrossing> mutate(List<FlatReservationForCrossing> process) {
        List<FlatReservationForCrossing> toReturn = new ArrayList<>();
        for (FlatReservationForCrossing reservation : process) {
            Integer randomWorkerId = random.nextInt(1, 6);
            toReturn.add(FlatReservationForCrossing.builder()
                    .workerName(randomWorkerId)
                    .taskType(reservation.getTaskType())
                    .taskId(reservation.getTaskId())
                    .processId(reservation.getProcessId())
                    .build());
        }
        return toReturn;
    }


    private boolean willMutate() {
        return random.nextDouble() < GeneticAlgorithmConstans.MUTATION_RATE;
    }


    private FlatIndividualForGA tournamentSelection(Integer tournamentSize) {
        List<FlatIndividualForGA> tournament = new ArrayList<>(tournamentSize);


        for (int i = 0; i < tournamentSize; i++) {
            int randomIndex = random.nextInt(population.size());
            tournament.add(population.get(randomIndex));
        }

        return evaluateFitnesses(tournament);
    }

    private FlatIndividualForGA evaluateFitnesses(List<FlatIndividualForGA> tournament) {
        FlatIndividualForGA best = tournament.get(0);
//        for (int i = 1; i < tournament.size(); i++) {
//            Double combinedFitnessBest = (best.getDueFitnesse() + best.getTimeFitnesse()) / 2.0;
//            Double combinedFitnessCurrent = (tournament.get(i).getDueFitnesse() + tournament.get(i).getTimeFitnesse()) / 2.0;
//            if (combinedFitnessBest < combinedFitnessCurrent) {
//                best = tournament.get(i);
//            }
//        }
        for (int i = 1; i < tournament.size(); i++) {
            if (best.getTimeFitnesse() < tournament.get(i).getTimeFitnesse()) {
                best = tournament.get(i);
            }
        }
        return best;
    }


    private List<FlatIndividualForGA> initializePopulation() {
        List<FlatIndividualForGA> toReturn = new ArrayList<>();
        for (int i = 0; i < GeneticAlgorithmConstans.POPULATION_SIZE; i++) {
            List<Task> taskCopy = new ArrayList<>(tasks);
            FlatIndividualForGA individual = new FlatIndividualForGA(taskCopy, statistics, processes, statisticService);
            individual.initializeRandomSchedule();
            individual.initializeChartData();
            toReturn.add(individual);
        }
        return toReturn;
    }

    private void saveFitnesses(List<FlatIndividualForGA> newPopulation) {
        List<NewFitnesses> toReturn = new ArrayList<>();
        for (int i = 0; i < newPopulation.size() - 1; i++) {
            FlatIndividualForGA individual = newPopulation.get(i);
            individual.setTimeFitnesse(FlatIndividualUtil.calculateTimeFitness(individual.getWorkersAvailabilities()));
            individual.setDueFitnesse(FlatIndividualUtil.calculateDueFitness(individual.getSchedule()));
            List<FlatIndividualForGA> populationForBestEver = new ArrayList<>(newPopulation);
            if (bestEver != null) {
                populationForBestEver.add(bestEver);
            }
            bestEver = evaluateFitnesses(populationForBestEver);
            toReturn.add(new NewFitnesses(individual.getTimeFitnesse(), individual.getDueFitnesse()));
        }
        fitnesses.add(toReturn);
    }

}
