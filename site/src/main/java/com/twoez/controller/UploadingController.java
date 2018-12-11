package com.twoez.controller;


import com.twoez.domain.BrentDates;
import com.twoez.domain.BrentOil;
import com.twoez.domain.BrentOilWithPrediction;
import com.twoez.repository.BrentOilRepository;
import com.twoez.repository.PredictedPricesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class UploadingController {

    @Autowired
    private BrentOilRepository brentOilRepository;

    @Autowired
    private PredictedPricesRepository predictedPricesRepository;

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @PostMapping(value = "/uploadValues", consumes = {"application/json"}, produces = {"application/json"})
    public @ResponseBody Object[] handleJsonUpload(@RequestBody BrentDates dates) {
        try {
            Date dFrom = format.parse(dates.getFromDate());
            Date dTo = format.parse(dates.getToDate());
            if(dates.isWithPrediction()){
                List<BrentOilWithPrediction> list = predictedPricesRepository.findPricesWithPredictionBetween(dFrom,dTo);
                return list.toArray();
            } else{
                return brentOilRepository.findPricesBetween(dFrom,dTo).toArray();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }




}
