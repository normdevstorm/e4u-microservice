package jmaster.io.statisticservice.repository;

import jmaster.io.statisticservice.entity.Statistic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticRepository extends JpaRepository<Statistic, Long> {

}
