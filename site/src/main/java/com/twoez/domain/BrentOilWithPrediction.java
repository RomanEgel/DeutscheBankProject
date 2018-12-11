package com.twoez.domain;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "predicted_prices")
public class BrentOilWithPrediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private double price;

    private double next_price;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    private Date price_time;

    private double prediction;

    public BrentOilWithPrediction() {
    }

    public BrentOilWithPrediction(double price, double next_price, Date price_time, double prediction) {
        this.price = price;
        this.next_price = next_price;
        this.price_time = price_time;
        this.prediction = prediction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getNext_price() {
        return next_price;
    }

    public void setNext_price(double next_price) {
        this.next_price = next_price;
    }

    public Date getPrice_time() {
        return price_time;
    }

    public void setPrice_time(Date price_time) {
        this.price_time = price_time;
    }

    public double getPrediction() {
        return prediction;
    }

    public void setPrediction(double prediction) {
        this.prediction = prediction;
    }
}
