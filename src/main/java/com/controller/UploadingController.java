package com.controller;


import com.crawler.IntrinioPriceGetter;
import com.domain.BrentDates;
import com.domain.BrentOil;
import com.prediction.KalmanFilter;
import com.repository.BrentOilRepository;
import com.actionHandlers.HistoryPriceSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class UploadingController {

    @Autowired
    BrentOilRepository brentOilRepository;

    private HistoryPriceSaver historyPriceSaver = null;
    private IntrinioPriceGetter priceGetter = null;

    @Value("${intrinioUsername}")
    private String username;

    @Value("${intrinioPassword}")
    private String password;

    @PostMapping(value = "/uploadValues", consumes = {"application/json"}, produces = {"application/json"})
    public @ResponseBody Object[] handleJsonUpload(@RequestBody BrentDates dates) {
        if(priceGetter == null)
            priceGetter = new IntrinioPriceGetter(username,password);
        if(historyPriceSaver == null) {
            historyPriceSaver = new HistoryPriceSaver(brentOilRepository);
        }

        if(!historyPriceSaver.isActive()) {
            historyPriceSaver.setHistory(priceGetter.getHistory(dates));
            new Thread(historyPriceSaver).start();
        }
        //predictPrice(brentOilPrices);

        return historyPriceSaver.getHistory().toArray();
    }


    /*public List<BrentOil> predictPrice(BrentOil[] prices){
        List<BrentOil> predictedPrices = new ArrayList<BrentOil>(Arrays.asList(prices));
        double volatility = Collections.max(predictedPrices,
                (o1, o2) -> o1.getOpen() > o2.getOpen() ? 1 : 0).getOpen() -
                Collections.min(predictedPrices, (o1, o2) -> o1.getOpen() > o2.getOpen() ? 0 : 1).getOpen();
        KalmanFilter kalmanFilter = new KalmanFilter(1, volatility, 1, volatility / 2);
        kalmanFilter.setState(predictedPrices.get(0).getOpen() + 1,0.1);
        for (int i = 0; i < predictedPrices.size() - 2;i++) {
            kalmanFilter.correct(predictedPrices.get(i).getOpen());
            predictedPrices.get(i).setOpen(kalmanFilter.getState());
            System.out.println("price " + i + " : predicted " +  predictedPrices.get(i).getOpen() +
                    ", actual " +  predictedPrices.get(i+1).getOpen());
        }
        return predictedPrices;
    }*/

}
