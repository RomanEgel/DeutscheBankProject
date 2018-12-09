package com.twoez.repository;

import com.twoez.domain.BrentOil;
import com.twoez.domain.BrentOilPK;
import com.twoez.domain.SourceName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface BrentOilRepository extends JpaRepository<BrentOil, BrentOilPK> {
    @Query(value = "Select * From brent_oil as bo Where bo.price_timestamp > ?1 and bo.price_timestamp < ?2",nativeQuery = true)
    List<BrentOil> findPricesBetween(Date d1, Date d2);
}
