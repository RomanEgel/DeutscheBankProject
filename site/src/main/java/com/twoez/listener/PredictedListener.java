package com.twoez.listener;

import org.springframework.messaging.simp.SimpMessagingTemplate;

public class PredictedListener implements Listener<Double> {

    private SimpMessagingTemplate client;


    public PredictedListener(SimpMessagingTemplate client){
        this.client = client;
    }
    @Override
    public void onUpdate(Double value) {
        client.convertAndSend("Paste here your url", value);
    }

    @Override
    public int hash() {
        return client.hashCode();
    }
}
