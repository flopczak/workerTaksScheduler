package pl.flopczak.WorkerTaskScheduler;

import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class SchedulingConstants {

    /**
     * 01.01.2023 00:00:00
     */
    public static Instant BEGGINING_OF_SCHEDULING = Instant.ofEpochSecond(1672531200);

    public static List<Integer> TASK_ORDER = Arrays.asList(1,2,3,4,5);

}
