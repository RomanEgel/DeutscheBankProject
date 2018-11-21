package com.twoez.repository;

import com.twoez.domain.BrentOil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface BrentOilRepository extends JpaRepository<BrentOil, Long> {
    BrentOil findByOpen(double open);
    BrentOil findByDate(Date date);

    // чтобы не откатывала таблица при  @Transactional
}
