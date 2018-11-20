package com.repository;

import com.domain.BrentOil;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface BrentOilRepository extends JpaRepository<BrentOil, Long> {
    BrentOil findByOpen(double open);
    BrentOil findByDate(Date date);

    // чтобы не откатывала таблица при  @Transactional
}
