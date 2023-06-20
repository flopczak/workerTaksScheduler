package pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm;

import lombok.Data;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.BreadthFirstAlgorithm;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.DepthFirstAlgorithm;
import pl.flopczak.WorkerTaskScheduler.process.data.Process;
import pl.flopczak.WorkerTaskScheduler.statistics.data.StatisticDTO;
import pl.flopczak.WorkerTaskScheduler.statistics.service.StatisticService;
import pl.flopczak.WorkerTaskScheduler.task.data.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Data
public class GeneticAlgorithm {
    private List<FlatIndividual> population;
    private List<StatisticDTO> statistics;
    private List<Process> processes;
    private List<Task> tasks;
    private StatisticService statisticService;
    private Random random;


    //TODO dodać fitness będący utylizacją czasu

    public GeneticAlgorithm(List<Task> tasks, List<StatisticDTO> statistics, List<Process> processes, StatisticService statisticService) {
        this.statistics = statistics;
        this.tasks = tasks;
        population = initializePopulation();
        this.random = new Random();
        this.processes = processes;
        this.statisticService = statisticService;
    }

    private List<FlatIndividual> initializePopulation() {
        List<FlatIndividual> toReturn = new ArrayList<>();
        for(int i = 0; i < GeneticAlgorithmConstans.POPULATION_SIZE-2; i++) {
            List<Task> taskCopy = new ArrayList<>(tasks);
            FlatIndividual individual = new FlatIndividual(taskCopy,statistics);
            individual.generateRandomSchedule();
            toReturn.add(individual);
        }

        DepthFirstAlgorithm dp = new DepthFirstAlgorithm(statistics, tasks, processes);
        BreadthFirstAlgorithm bd = new BreadthFirstAlgorithm(statistics, tasks, processes, statisticService);
        dp.scheduleTasks();
        bd.scheduleTasks();
        toReturn.add(random.nextInt(GeneticAlgorithmConstans.POPULATION_SIZE-2),dp.getIndividual());
        toReturn.add(random.nextInt(GeneticAlgorithmConstans.POPULATION_SIZE-2),bd.getIndividual());
        return toReturn;
    }

    private FlatIndividual tournamentSelection(Integer tournamentSize){
        List<FlatIndividual> tournament = new ArrayList<>(tournamentSize);


        for (int i = 0; i < tournamentSize; i++) {
            int randomIndex = random.nextInt(population.size());
            tournament.add(population.get(randomIndex));
        }

        FlatIndividual best = tournament.get(0);

        for (int i = 1; i < tournament.size(); i++) {
            Double timeProtencage = best.getTimeFitnesse() / tournament.get(i).getTimeFitnesse();
            Double dueProtencage = best.getDueFitnesse() / tournament.get(i).getDueFitnesse();
            Double growth = timeProtencage + dueProtencage;
            if (growth < 2) { // TODO to check
                best = tournament.get(i);
            }
        }

        return best;
    }

    private FlatIndividual crossover(FlatIndividual parent1, FlatIndividual parent2) {
        FlatReservation closest;


        for (FlatReservation res : parent1.getSchedule()) {
            parent2.getSchedule().stream().filter(res2 -> res2.getTaskId().equals(res.getTaskId())).findAny().orElse(null);
        }
        //return rescheduled child
        return null;
    }




}
