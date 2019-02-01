package com.twoez.sparkprediction;

import com.twoez.common.SQLConfiguration;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.sql.SparkSession;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class DataAccessor implements Runnable {

    private KafkaConsumer<String, String> kafkaConsumer;

    private final String TOPIC = "current_price";

    private final String BOOTSTRAP_SERVER = "localhost:9092";

    private final SparkSession session;

    private final SQLConfiguration configuration;

    public DataAccessor(SparkSession session, SQLConfiguration configuration){
        this.session = session;
        this.configuration = configuration;
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "predictorSpark");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,"DataAccessorPredictor");
        kafkaConsumer = new KafkaConsumer<String, String>(properties);
        kafkaConsumer.subscribe(Arrays.asList(TOPIC));
    }

    @Override
    public void run() {
        PredictionProducer producer = new PredictionProducer();
        PredictionEvaluator predictor = new RealTimePredictor(session,configuration, producer::send);
        ConsumerRecords<String, String> consumerRecords;
        while (true){
            consumerRecords = kafkaConsumer.poll(Duration.ofMillis(1000));
            if(consumerRecords.isEmpty()){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex){
                    System.out.println("Interrupted\n" + ex.getMessage());
                }
            } else{
                String value = "";
                for(ConsumerRecord<String, String> item : consumerRecords){
                    value = item.value();
                }
                predictor.evaluatePrediction(Double.parseDouble(value));
            }
        }
    }
}
