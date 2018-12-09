package com.twoez.kafka;

import com.twoez.domain.BrentOil;
import com.twoez.domain.SourceName;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class KafkaProducerPrices {

    private final Properties properties;

    private final Producer<String, String> producer;

    private final static String TOPIC = "current_price";

    public KafkaProducerPrices(){
        properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, 0);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "predictorSpark");
        producer = new KafkaProducer<String, String>(properties);
    }

    public void send(BrentOil price){
        String key = SourceName.RealTimeXMarkets.name();
        String value = String.valueOf(price.getPrice());
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(TOPIC, key, value);
        producer.send(record);
    }
}
