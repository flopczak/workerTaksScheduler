package pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GAToReturn {
//    private List<FlatIndividual> population;
    private List<List<Fitnesses>> fitnesses;
    private FlatIndividual bestEver;
}
