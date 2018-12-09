package com.twoez.historyuploader;

import com.twoez.common.SQLConfiguration;

import java.sql.Timestamp;
import java.util.Map;

public interface DBUploader {
    void fillDB(final Map<Timestamp, Double> priceData);

    void setConfiguration(final SQLConfiguration configuration);

    void createDatasetProcedure();

    void createDailyDatasetAndTrigger();
}
