package pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.newGA;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.Fitnesses;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.FlatIndividual;

import java.util.List;

@Data
@AllArgsConstructor
public class NewGaToReturn {
    private List<List<NewFitnesses>> fitnesses;
    private FlatIndividualForGA bestEver;
}
