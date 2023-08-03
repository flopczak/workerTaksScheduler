package pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm;

import java.util.List;
import java.util.Map;

public class FlatIndividualUtil {
    public static Double calculateTimeFitness(List<FlatReservation> reservations) {
        Double toReturn = 0.0;
        //TODO do zmiany nie uwzględnia pustych miejsc gdzie pracownik nic nie robi
//        for (FlatReservation reservation : reservations) {
//            toReturn += reservation.getReservationDuration();
//        }
//        if (toReturn != 0.0) {
//            return 100.0/toReturn;
//        }
//to jest na podstawie taska a nie pracownika sprawdzać kiedy zaczyna się ostatni wolny przedział pracownika
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
            toReturn += timePeriods.get(timePeriods.size()-1).getStartTime();
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

    public static Double timeUtilizationFitness(List<FlatReservation> reservations, Map<Integer, List<TimePeriod>> workersAvailabilities) {
        Double toReturn = 0.0;
        //procent wolnego miejsca do zajętego do końca workerSchedule


        //weź każdego pracownika z rezerwacji i znajdź tą która kończy się najpóźniej

        //HA nie potrzeba rezerwacji... sprawdź kiedy zaczyna się ostatni TP w worker schedule wszystko przed tym to nieużytki

        //zsumuj wolne sloty pracownika kończące się przed endTime ostatniej z rezerwacji
//        for (FlatReservation reservation : reservations) {
//            toReturn += reservation.getReservationDuration();
//        }
//        if (toReturn!= 0.0) {
//            return 100.0/toReturn;
//        }
        return toReturn;
    }
}
