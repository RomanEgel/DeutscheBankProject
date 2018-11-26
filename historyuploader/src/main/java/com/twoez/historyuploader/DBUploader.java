package com.twoez.historyuploader;

import java.sql.Timestamp;
import java.util.Map;

public interface DBUploader {
    void fillDB(final Map<Timestamp, Double> priceData);

    void setConfiguration(final SQLConfiguration configuration);
}
