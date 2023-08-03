package pl.flopczak.WorkerTaskScheduler.algorithms.controller;


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
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.newGA.GA;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.GAToReturn;

import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.newGA.NewGaToReturn;
import pl.flopczak.WorkerTaskScheduler.reservation.data.ReservationDTO;

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

    @GetMapping("/geneticAlgorithm")
    public GAToReturn geneticAlgorithm() { return algorithmsService.geneticAlgorithm(); }

    @GetMapping("/geneticAlgorithmBest")
    public FlatIndividual geneticAlgorithmBest() { return algorithmsService.geneticAlgorithmBest(); }

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
}
