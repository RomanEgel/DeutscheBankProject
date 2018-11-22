package com.twoez;

import com.twoez.domain.BrentOil;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class CurrentPriceListener implements Listener<BrentOil>{

    private final SimpMessagingTemplate client;

    public CurrentPriceListener(SimpMessagingTemplate client){
        this.client = client;
    }

    @Override
    public void onUpdate(BrentOil value) {
        client.convertAndSend("/prices/getCurrent", value);
    }

    @Override
    public int hash() {
        return client.hashCode();
    }
}
