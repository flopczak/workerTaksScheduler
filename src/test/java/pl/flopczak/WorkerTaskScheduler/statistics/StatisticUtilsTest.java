package pl.flopczak.WorkerTaskScheduler.statistics;

import org.junit.jupiter.api.Test;
import pl.flopczak.WorkerTaskScheduler.statistics.service.StatisticUtils;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatisticUtilsTest {


    //mamy statystki i rezerwacje
    // w rezerwacjach chcemy znaleźć dostępny zasób na podstawie startTime oraz estimatedTime
    // statysyyki zawieraą estimatedTime zasobu

    //weźmy wszystkie zasoby wolne w danym startTime następnie oddrzućmy zasoby w któych startTime+estimatedTime >= startTime innego zadania

    //metoda do zamiany start time na listę startTime+estimatedTime

    @Test
    public void  testEstimatedEndTime() throws Exception {
        Instant startTime = Instant.ofEpochSecond(1672531200L);
        Integer estimatedTimeInSeconds = 60;

        Instant estimatedTimePeriod = StatisticUtils.estimatedEndTime(estimatedTimeInSeconds, startTime);
        assertEquals(estimatedTimePeriod,Instant.ofEpochSecond(1672531260L));
    }

}
