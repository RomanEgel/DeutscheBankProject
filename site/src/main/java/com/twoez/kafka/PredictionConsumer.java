package com.twoez.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Properties;
import java.util.function.Consumer;

@Component
public class PredictionConsumer implements Runnable {
    private KafkaConsumer<String, String> kafkaConsumer;

    private final String TOPIC = "predicted_prices";

    private final String BOOTSTRAP_SERVER = "localhost:9092";

    private Consumer<Double> policy;

    public PredictionConsumer(){
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "predictionListener");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,"PredictionConsumer");
        kafkaConsumer = new KafkaConsumer<String, String>(properties);
        kafkaConsumer.subscribe(Arrays.asList(TOPIC));
    }

    public void setPolicy(Consumer<Double> consumer){
        policy = consumer;
    }

    @Override
    public void run() {
        if(policy == null) throw new RuntimeException("Consumer for predicted prices not set");
        ConsumerRecords<String, String> consumerRecords;
        while (true){
            consumerRecords = kafkaConsumer.poll(1000);
            if(consumerRecords.isEmpty()){
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex){
                    System.out.println("Interrupted\n" + ex.getMessage());
                }
            } else{
                String value = "";
                for(ConsumerRecord<String, String> item : consumerRecords){
                    value = item.value();
                }
                policy.accept(Double.parseDouble(value));
            }
        }
    }
}
