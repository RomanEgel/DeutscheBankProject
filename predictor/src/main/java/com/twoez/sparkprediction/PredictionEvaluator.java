package com.twoez.sparkprediction;

import org.apache.spark.ml.linalg.Vector;

public interface PredictionEvaluator {

    void initDataset();

    void trainRegressor();

    void evaluatePrediction(double feature);
}
