package com.domain;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class BrentOil {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    Date date;

    double open;


    public BrentOil() {

    }

    public BrentOil(Date date, double open) {
        this.date = date;
        this.open = open;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
