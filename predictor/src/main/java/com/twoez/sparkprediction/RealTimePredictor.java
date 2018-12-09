package com.twoez.sparkprediction;

import com.twoez.common.SQLConfiguration;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.linalg.DenseVector;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.regression.LinearRegressionModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.Properties;
import java.util.function.Consumer;

public class RealTimePredictor implements PredictionEvaluator {

    private final SparkSession session;

    private final SQLConfiguration configuration;

    private Properties properties;

    private String url;

    private Dataset<Row> dataset;

    private LinearRegressionModel model;

    private final Consumer<Double> resultPolicy;

    private double previousFeature;

    public RealTimePredictor(SparkSession session, SQLConfiguration config, Consumer<Double> policy){
        this.session = session;
        configuration = config;
        resultPolicy = policy;
    }

    public void printIncomingValue(String value){
        System.out.println(value);
    }

    @Override
    public void initDataset() {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (Exception ex){
            throw new RuntimeException("Driver was not loaded");
        }
        url = "jdbc:mysql://localhost/" + configuration.name();
        properties = new Properties();
        properties.put("useLegacyDatetimeCode", "false");
        properties.put("serverTimezone", "America/New_York");
        properties.put("user", configuration.login());
        if(configuration.password() != null){
            properties.put("password", configuration.password());
        }
        dataset = session.read().jdbc(url, "new_table", properties);
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(new String[]{"price"})
                .setOutputCol("features");
        dataset = assembler.transform(dataset);
    }

    @Override
    public void trainRegressor() {
        initDataset();
        LinearRegression lr = new LinearRegression()
                .setMaxIter(10)
                .setRegParam(0.1)
                .setElasticNetParam(0.5)
                .setLabelCol("next_price");
        model = lr.fit(dataset);
    }

    @Override
    public void evaluatePrediction(double feature) {
        if(feature != previousFeature){
            trainRegressor();
            previousFeature = feature;
        }
        resultPolicy.accept(model.predict(new DenseVector(new double[]{feature})));
    }
}
