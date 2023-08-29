package pl.flopczak.WorkerTaskScheduler.algorithms.controller;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.AlgorithmsService;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.BreadthFirstAlgorithm;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.DepthFirstAlgorithm;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.DFAlgorithm;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.FlatIndividual;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.NewBreadthFirstAlgorithm;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.newGA.FlatIndividualForGA;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.newGA.GA;

import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.newGA.NewFitnesses;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.newGA.NewGaToReturn;
import pl.flopczak.WorkerTaskScheduler.reservation.data.ReservationDTO;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "api/algorithms")
public class AlgorithmsController {

    private AlgorithmsService algorithmsService;

    public AlgorithmsController(AlgorithmsService algorithmsService) {
        this.algorithmsService = algorithmsService;
    }


    @GetMapping("/optimisticConflicted")
    public List<ReservationDTO> startOptimisticAllocationConflicted() {
     return algorithmsService.startOptimisticAllocationConflicted();
    }

    @GetMapping("/randomFlatIndividual")
    @ResponseBody
    public FlatIndividual randomFlatIndividual() {
        return algorithmsService.randomFlatIndividual();
    }

    @GetMapping("/depthFirstAlgorithm")
    public DepthFirstAlgorithm depthFirstAlgorithm() { return algorithmsService.depthFirstAlgorithm();}
    @GetMapping("/breadthFirstAlgorithm")
    public BreadthFirstAlgorithm breadthFirstAlgorithm() { return algorithmsService.breadthFirstAlgorithm();}
    @GetMapping("/newBF")
    public NewBreadthFirstAlgorithm newbreadthFirstAlgorithm() { return algorithmsService.newBreadthFirstAlgorithm();}

    @GetMapping("/DF")
    public DFAlgorithm getDF() { return algorithmsService.newDFAlgorithm();}

    @GetMapping("/GA")
    public GA getGA() { return algorithmsService.gA();}

    @GetMapping("/GA2")
    public NewGaToReturn getGA2() { return algorithmsService.gaToReturn();}

    @GetMapping("/GA3")
    public List<List<NewFitnesses>> getGA3() {
        NewGaToReturn a = algorithmsService.gaToReturn();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter("bestever.json")) {
            gson.toJson(a.getBestEver(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return a.getFitnesses();}

    @GetMapping("/GA4")
    public FlatIndividualForGA getGA4() {
        NewGaToReturn a = algorithmsService.gaToReturn();

        return a.getBestEver();}
}
