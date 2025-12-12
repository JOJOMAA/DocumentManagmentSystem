package org.example.batch.repository;

import org.example.batch.model.DailyAccessStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyAccessStatRepository extends JpaRepository<DailyAccessStat, Long> {
}