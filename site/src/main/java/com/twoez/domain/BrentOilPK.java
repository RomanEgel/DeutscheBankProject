package com.twoez.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Embeddable
public class BrentOilPK implements Serializable {
    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    private Date price_timestamp;

    @Enumerated(EnumType.STRING)
    private SourceName source;

    public BrentOilPK() {
    }

    public BrentOilPK(Date price_timestamp, SourceName source) {
        this.price_timestamp = price_timestamp;
        this.source = source;
    }

    public Date getPrice_timestamp() {
        return price_timestamp;
    }

    public void setPrice_timestamp(Date price_timestamp) {
        this.price_timestamp = price_timestamp;
    }

    public SourceName getSource() {
        return source;
    }

    public void setSource(SourceName source) {
        this.source = source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrentOilPK that = (BrentOilPK) o;
        return Objects.equals(price_timestamp, that.price_timestamp) &&
                source == that.source;
    }

    @Override
    public int hashCode() {
        return Objects.hash(price_timestamp, source);
    }
}
