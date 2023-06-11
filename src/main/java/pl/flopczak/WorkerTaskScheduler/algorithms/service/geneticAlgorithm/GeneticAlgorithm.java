package pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm;

import pl.flopczak.WorkerTaskScheduler.algorithms.service.AlgorithmType;
import pl.flopczak.WorkerTaskScheduler.process.data.Process;
import pl.flopczak.WorkerTaskScheduler.reservation.data.ReservationDTO;
import pl.flopczak.WorkerTaskScheduler.task.data.Task;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class GeneticAlgorithm {
    private List<Individual> population;// czy to wgl nie powinna być lista list reservation
    //potrzebne lista zadań do wyonania
    private List<Process> processes;
    private List<Task> tasks;

    public GeneticAlgorithm(List<Process> processes, List<Task> tasks) {
        population = new ArrayList<>();
        this.processes = processes;
        this.tasks = tasks;
    }

//    private void initializePopulation() {
//        //Inicjalizacja populacji na początek może byc losowa jednak lepiej może zadziałać zainicjalizowanie
//        //populacji jakąś heurystyką np najlepszą z pozostałych algorytmów testowanych w ramach pracy jako pojedyńczego osobnika>????
//
//        for(int i = 0; i < GeneticAlgorithmConstans.POPULATION_SIZE; i++) {
//            Individual individual = generateRandomGene(); //TODO zmień generateRandomGene na generateFromHeuristic
//
//        }

//    }

    // jako że zaczynam od czystej kartki metody do sprawdzania czy pracownik jest dostepny powinny być tworzone na
    // podstawie listy "population"?
//    private Individual generateRandomGene() {
//        Individual individual = Individual.builder()
//                .availableTasks(tasks)
//                .schedule(new ArrayList<>())
//                .dueFitnesse(0)
//                .timeFitnesse(0)
//                .build();
//
//        return null;
//    }

    //Przenieść do klasy Individual?

}
