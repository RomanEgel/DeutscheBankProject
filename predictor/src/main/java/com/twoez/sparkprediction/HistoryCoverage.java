package com.twoez.sparkprediction;

import com.twoez.common.SQLConfiguration;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.regression.*;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.Properties;

public class HistoryCoverage implements PredictionEvaluator {

    private final SQLConfiguration configuration;

    private Dataset<Row> dataset;

    private final SparkSession session;

    private LinearRegressionModel model;

    private final String sourceName = "PredictionLinear";

    private String url;

    private Properties properties;

    private final String tableName = "brent_oil_predicted";

    public HistoryCoverage(SQLConfiguration config, SparkSession session){
        configuration = config;
        this.session = session;
    }
    @Override
    public void initDataset(){
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
    public void trainRegressor(){
        LinearRegression lr = new LinearRegression()
                .setLabelCol("next_price")
                .setElasticNetParam(0.5)
                .setRegParam(0.1)
                .setMaxIter(10);

        model = lr.fit(dataset);
    }
    @Override
    public void evaluatePrediction(double feature){
        model.transform(dataset)
                .drop("features")
                .write()
                .mode("overwrite")
                .jdbc(url, "predicted_prices", properties);
    }
}
