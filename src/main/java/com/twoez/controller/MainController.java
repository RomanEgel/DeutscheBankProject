package com.twoez.controller;

import com.twoez.crawler.PriceGetterState;
import com.twoez.crawler.XMarketsPriceGetter;
import com.twoez.actionHandlers.CurrentPriceViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private CurrentPriceViewer priceGetter = null;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/")
    public String greeting(){
        return "greeting";
    }

    @GetMapping("/getCurrent")
    public String getCurrent(){
        return "getCurrent";
    }

    @MessageMapping("/state")
    public void changeState(PriceGetterState state){
        if(state.getIsActive()){
            if(priceGetter==null) {
                priceGetter = new CurrentPriceViewer(simpMessagingTemplate, new XMarketsPriceGetter());
                new Thread(priceGetter).start();
            }
        } else {
            if(priceGetter != null) {
                priceGetter.setEndFlag(true);
                priceGetter.endConnection();
                priceGetter = null;
            }
        }
    }

    @GetMapping("/uploadValues")
    public String uploadValues(){
        return "uploadValues";
    }

}
