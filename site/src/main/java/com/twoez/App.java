package com.twoez;

import com.twoez.crawler.XMarketsPriceGetter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@SpringBootApplication
public class App{
    public static void main(String[] args) {
        PriceDispatcher.dispatcher.setCurrentPriceUpdater(new XMarketsPriceGetter());
        SpringApplication.run(App.class, args);
    }
}
