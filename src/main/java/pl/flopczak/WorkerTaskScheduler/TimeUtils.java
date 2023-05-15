package pl.flopczak.WorkerTaskScheduler;

import java.time.*;
import java.util.concurrent.ThreadLocalRandom;

public class TimeUtils {


    //TODO tu chyba jednak nie ma potrzeby losowania godzin z przedziału bo to due date
    //zasoby po prostu muszą wykonać zadanie przed due date
    public static Instant randomDateBetween(Instant startInclusive, Instant endExclusive) {
        long startSeconds = startInclusive.getEpochSecond();
        long endSeconds = endExclusive.getEpochSecond();
        long random = ThreadLocalRandom
                .current()
                .nextLong(startSeconds, endSeconds);

        return Instant.ofEpochSecond(random);
    }
}
