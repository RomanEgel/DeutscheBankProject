package com.twoez.listener;

import org.springframework.messaging.simp.SimpMessagingTemplate;

public class PredictedListener implements Listener<Double> {

    private SimpMessagingTemplate client;

    private final String clientId;

    public PredictedListener(SimpMessagingTemplate client, String clientId){
        this.client = client;
        this.clientId = clientId;
    }

    @Override
    public void onUpdate(Double value) {
        System.out.println("sending prediction to user:" + clientId);
        client.convertAndSend("/prices/getPrediction/"+clientId,value);
    }

    @Override
    public String hash() {
        return clientId;
    }
}
