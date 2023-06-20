package pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm;

import java.util.List;

public class FlatIndividualUtil {
    public static Double calculateTimeFitness(List<FlatReservation> reservations) {
        Double toReturn = 0.0;
        //TODO do zmiany nie uwzględnia pustych miejsc gdzie pracownik nic nie robi
        for (FlatReservation reservation : reservations) {
            toReturn += reservation.getReservationDuration();
        }
        if (toReturn != 0.0) {
            return 100.0/toReturn;
        }
        return toReturn;
    }
    public static Double calculateDueFitness(List<FlatReservation> reservations) {
        Double toReturn = 0.0;
        for (FlatReservation reservation : reservations) {
            toReturn += reservation.getTaskDueTimeInMinutes() - reservation.getEndTime();
        }
        if (toReturn != 0.0) {
            return 1000.0/toReturn;
        }
        return toReturn;
    }
}
