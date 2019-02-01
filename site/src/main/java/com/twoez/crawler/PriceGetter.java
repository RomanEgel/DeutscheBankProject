package com.twoez.crawler;

import com.twoez.domain.BrentOil;

import java.util.function.Consumer;

public interface PriceGetter extends Runnable {
    BrentOil getCurrentPrice();

    void setConsumer(Consumer<BrentOil> consumer);
}
