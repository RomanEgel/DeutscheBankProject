package com.twoez.listener;

import com.twoez.domain.BrentOil;
import com.twoez.kafka.KafkaProducerPrices;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
@Qualifier("kafkaListener")
public class KafkaListener implements Listener<BrentOil> {

    private final KafkaProducerPrices producerPrices;

    public KafkaListener(){
        producerPrices = new KafkaProducerPrices();
    }

    @Override
    public void onUpdate(BrentOil value) {
        producerPrices.send(value);
    }

    @Override
    public String hash() {
        return "kafkaListenerSingleton";
    }
}
