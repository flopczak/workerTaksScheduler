package pl.flopczak.WorkerTaskScheduler.algorithms.service;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pl.flopczak.WorkerTaskScheduler.process.data.Process;
import pl.flopczak.WorkerTaskScheduler.reservation.data.ReservationDTO;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DepthFirstAlgorithm implements Algorithm{
    private static final Log LOG = LogFactory.getLog(DepthFirstAlgorithm.class);
    private final List<ReservationDTO> reservations = new ArrayList<>();

    private static final AlgorithmType algorithmType = AlgorithmType.DMStrategy;

    @Override
    public List<ReservationDTO> schedule(List<Process> processes) {
        //TODO nappisać util optimisticAlloc(ins)

        return reservations;
    }
}
