package com.repository;

import com.domain.BrentOil;
import org.springframework.data.repository.CrudRepository;

public interface BrentOilRepository extends CrudRepository<BrentOil, Long> {
    BrentOil findByOpen(double open);
}
