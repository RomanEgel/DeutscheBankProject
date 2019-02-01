package com.twoez.repository;


import com.twoez.domain.BrentOilWithPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface PredictedPricesRepository extends JpaRepository<BrentOilWithPrediction,Integer> {

    @Query(value = "Select * From predicted_prices as bo Where bo.price_time > ?1 and bo.price_time < ?2",nativeQuery = true)
    List<BrentOilWithPrediction> findPricesWithPredictionBetween(Date d1, Date d2);
}
