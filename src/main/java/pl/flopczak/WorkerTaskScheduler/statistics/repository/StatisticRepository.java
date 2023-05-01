package pl.flopczak.WorkerTaskScheduler.statistics.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.flopczak.WorkerTaskScheduler.statistics.data.Statistic;

@Repository
public interface StatisticRepository extends CrudRepository<Statistic, Long> {
}
