package pl.flopczak.WorkerTaskScheduler.algorithms.service.geneticAlgorithm;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TimePeriod {
    private Integer startTime;
    private Integer endTime;

    public TimePeriod(Integer startTime, Integer endTime) {
        if (startTime >= endTime) { throw new IllegalArgumentException("startTime must be smaller than endTime"); }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Integer getTimePeriodDuration() {
      return endTime - startTime;
    };

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }
}
