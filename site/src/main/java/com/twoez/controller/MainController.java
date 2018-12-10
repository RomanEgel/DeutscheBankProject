package com.twoez.controller;

import com.twoez.dispatcher.PriceDispatcher;
import com.twoez.domain.BrentOil;
import com.twoez.listener.CurrentPriceListener;
import com.twoez.listener.ListenerState;
import com.twoez.listener.PredictedListener;
import com.twoez.repository.BrentOilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class MainController {

    @Autowired
    private BrentOilRepository brentOilRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/")
    public String greeting(Model model){
        List<String> names = new ArrayList<>();
        names.add("Brent Oil");
        model.addAttribute("names",names);
        return "greeting";
    }

    @GetMapping("/getCurrent")
    public String getCurrent(){
        return "getCurrent";
    }

    @MessageMapping("/state")
    public String changeState(ListenerState state, @Header("simpSessionId") String sessionId) {
        if (state.getIsActive()) {
            PriceDispatcher.dispatcher.addListener(new CurrentPriceListener(simpMessagingTemplate,sessionId));
            if (state.isPrediction()) {
                PriceDispatcher.dispatcher.addPredictedPriceListener(new PredictedListener(simpMessagingTemplate,sessionId));
            }
        } else {
            if (state.isPrediction()) {
                PriceDispatcher.dispatcher.removePredictedListenerByHash(sessionId);
            }
            PriceDispatcher.dispatcher.removeListenerByHash(sessionId);
        }
        return sessionId;
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
