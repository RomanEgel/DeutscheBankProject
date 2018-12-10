package com.twoez.listener;

import com.twoez.domain.BrentOil;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Collections;

public class CurrentPriceListener implements Listener<BrentOil>{

    private final SimpMessagingTemplate client;

    private final String clientId;

    public CurrentPriceListener(SimpMessagingTemplate client, String clientId){
        this.client = client;
        this.clientId = clientId;
    }

    @Override
    public void onUpdate(BrentOil value) {
        //System.out.println("sending to user: " + clientId);
        client.convertAndSend("/prices/getCurrent/"+clientId,value);
    }

    @Override
    public String hash() {
        return clientId;
    }
}
