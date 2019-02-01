package com.twoez.domain;


import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class BrentOil {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @EmbeddedId
    private BrentOilPK boPK;

    private double price;

    public BrentOil() {

    }

    public BrentOil(BrentOilPK id, double price) {
        this.boPK = id;
        this.price = price;
    }

    public BrentOilPK getId() {
        return boPK;
    }

    public void setId(BrentOilPK id) {
        this.boPK = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean equals(BrentOil bo){
        if(bo == null) {
            return false;
        }
        return this.boPK.getPrice_timestamp().equals(bo.getId().getPrice_timestamp()) && this.price == bo.getPrice();
    }
}
