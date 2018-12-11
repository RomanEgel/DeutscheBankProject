package com.twoez.sparkprediction;

import com.twoez.common.SQLConfiguration;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;
import picocli.CommandLine;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "Predictor", mixinStandardHelpOptions = true, version = "0.1")
public class Predictor implements Callable<Integer> {

    @CommandLine.Option(names = {"-n", "--db_name"},required = true, description = "Name of database where to store prices")

    String dbName;

    @CommandLine.Option(names = {"-u", "--user_name"}, required = true, description = "Login to your database")

    String login;

    @CommandLine.Option(names = {"-p","--password"}, required = false, description = "Password paired with provided login if it required")

    String password;

    public static void main(String[] args) throws IOException {
        CommandLine.call(new Predictor(), args);
    }

    @Override
    public Integer call() throws Exception {
        SparkSession spark = SparkSession
                .builder()
                .appName("Java Spark SQL basic example")
                .config("spark.master", "local")
                .getOrCreate();
        JavaSparkContext jsc = JavaSparkContext.fromSparkContext(spark.sparkContext());

        SQLConfiguration configuration = new SQLConfiguration(dbName, login, password);
        PredictionEvaluator coverage = new HistoryCoverage(configuration, spark);
        coverage.initDataset();
        coverage.trainRegressor();
        coverage.evaluatePrediction(0);
        DataAccessor accessor = new DataAccessor(spark, configuration);
        accessor.run();
        jsc.stop();
        return 0;
    }
}