package com.twoez.domain;

public class BrentDates {

    private String fromDate;
    private String toDate;

    private boolean withPrediction;

    public BrentDates() {

    }

    public BrentDates(String fromDate, String toDate) {
        this.toDate = toDate;
        this.fromDate = fromDate;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public boolean isWithPrediction() {
        return withPrediction;
    }

    public void setWithPrediction(boolean withPrediction) {
        this.withPrediction = withPrediction;
    }
}
