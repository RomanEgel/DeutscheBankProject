package com.twoez.sparkprediction;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class PredictionProducer {

    private final Properties properties;

    private final Producer<String, String> producer;

    private final static String TOPIC = "predicted_prices";

    public PredictionProducer(){
        properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, 0);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "predictorSpark");
        producer = new KafkaProducer<String, String>(properties);
    }

    public void send(double predicted){
        String key = "predicted";
        String value = String.valueOf(predicted);
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(TOPIC, key, value);
        producer.send(record);
    }
}
