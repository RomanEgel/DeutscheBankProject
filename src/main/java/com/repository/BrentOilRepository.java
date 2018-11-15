package com.repository;

import com.domain.BrentOil;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;

public interface BrentOilRepository extends CrudRepository<BrentOil, Long> {
    BrentOil findByOpen(double open);
    BrentOil findByDate(Date date);
}
