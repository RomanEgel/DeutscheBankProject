package com.twoez.domain;


import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class BrentOil {

    @EmbeddedId
    private BrentOilPK id;

    private double price;

    public BrentOil() {

    }

    public BrentOil(BrentOilPK id, double price) {
        this.id = id;
        this.price = price;
    }

    public BrentOilPK getId() {
        return id;
    }

    public void setId(BrentOilPK id) {
        this.id = id;
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
        return this.id.getPrice_timestamp().equals(bo.getId().getPrice_timestamp()) && this.price == bo.getPrice();
    }
}
