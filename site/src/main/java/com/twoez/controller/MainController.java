package com.twoez.controller;

import com.twoez.crawler.PriceGetterState;
import com.twoez.dispatcher.PriceDispatcher;
import com.twoez.domain.BrentOil;
import com.twoez.listener.CurrentPriceListener;
import com.twoez.repository.BrentOilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private BrentOilRepository brentOilRepository;

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
            PriceDispatcher.dispatcher.removeListenerByHash(simpMessagingTemplate.hashCode());
        }
    }

    @GetMapping("/uploadValues")
    public String uploadValues(Model model){

        return "uploadValues";
    }

    @GetMapping(value = "/getRange", produces = {"application/json"})
    public @ResponseBody Object[] getRange(){
        List<BrentOil> all = brentOilRepository.findAll();
        Date[] d = new Date[2];
        d[0] = all.get(0).getId().getPrice_timestamp();
        d[1] = all.get(all.size()-1).getId().getPrice_timestamp();
        return d;
    }


}
