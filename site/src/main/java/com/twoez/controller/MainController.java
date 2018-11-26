package com.twoez.controller;

import com.twoez.CurrentPriceListener;
import com.twoez.PriceDispatcher;
import com.twoez.crawler.PriceGetterState;
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
            PriceDispatcher.dispatcher.addListener(new CurrentPriceListener(simpMessagingTemplate));
        } else {
            PriceDispatcher.dispatcher.removeListenerByClient(simpMessagingTemplate);
        }
    }

    @GetMapping("/uploadValues")
    public String uploadValues(){
        return "uploadValues";
    }

}
