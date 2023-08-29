package pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm;

import java.util.List;
import java.util.Map;

public class FlatIndividualUtil {
    public static Double calculateTimeFitness(List<FlatReservation> reservations) {
        Double toReturn = 0.0;
        List<FlatReservation> lastTasks = reservations.stream().filter(reservation -> reservation.getTaskType().equals(5)).toList();
        for (FlatReservation reservation : lastTasks) {
            toReturn += reservation.getEndTime();
        }
        if (toReturn != 0.0) {
            return 100.0 / toReturn;
        }
        return toReturn;
    }

    public static Double calculateTimeFitness(Map<Integer, List<TimePeriod>> workersAvailabilities){
        Double toReturn = 0.0;

        for (Map.Entry<Integer, List<TimePeriod>> entry : workersAvailabilities.entrySet()) {
            List<TimePeriod> timePeriods = entry.getValue();
            for (TimePeriod timePeriod : timePeriods) {
                if (timePeriod.getEndTime().equals(36000)) {
                    toReturn += timePeriod.getStartTime();
                }
            }
        }
        if (toReturn != 0.0) {
            return 100.0 / toReturn;
        }

        return toReturn;
    }

    public static Double calculateDueFitness(List<FlatReservation> reservations) {
        Double toReturn = 0.0;
        for (FlatReservation reservation : reservations) {
            if (reservation.getTaskDueTimeInMinutes() == null) continue;
            toReturn += reservation.getTaskDueTimeInMinutes() - reservation.getEndTime();
        }
        if (toReturn != 0.0) {
            return toReturn / 1000000.0;
        }
        return toReturn;
    }
}
