package pl.flopczak.WorkerTaskScheduler.algorithms.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.AlgorithmsService;
import pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm.FlatIndividual;
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
}
