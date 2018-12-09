package com.twoez.dispatcher;

import com.twoez.crawler.PriceGetter;
import com.twoez.crawler.XMarketsPriceGetter;
import com.twoez.domain.BrentOil;
import com.twoez.kafka.PredictionConsumer;
import com.twoez.listener.KafkaListener;
import com.twoez.listener.Listener;
import com.twoez.listener.SavingCurrentPriceListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class PriceDispatcher {
    private final List<Listener<BrentOil>> currentPriceListeners;

    private final List<Listener<Double>> predictedListeners;

    private final PriceGetter priceGetter;

    private final PredictionConsumer predictionGetter;

    private PriceDispatcher(PriceGetter getter, PredictionConsumer predictionConsumer) {
        priceGetter = getter;
        predictionGetter = predictionConsumer;
        currentPriceListeners = new ArrayList<>();
        predictedListeners = new ArrayList<>();
        priceGetter.setConsumer(this::notifyListeners);
        new Thread(priceGetter).start();
        new Thread(predictionGetter).start();
    }

    private void notifyListeners(BrentOil price){
        new Thread(()->{
            List<Listener<BrentOil>> listenersCopy;
            synchronized (currentPriceListeners){
                listenersCopy = new ArrayList<>(currentPriceListeners);
            }
            listenersCopy.forEach((listener)->listener.onUpdate(price));
        }).start();
    }

    private void notifyPredictionListeners(Double predictedPrice){
        new Thread(()->{
            List<Listener<Double>> listenersCopy;
            synchronized (currentPriceListeners){
                listenersCopy = new ArrayList<>(predictedListeners);
            }
            listenersCopy.forEach((listener)->listener.onUpdate(predictedPrice));
        }).start();
    }

    public void addListener(Listener<BrentOil> newListener){
        synchronized (currentPriceListeners){
            currentPriceListeners.add(newListener);
        }
    }

    public void addPredictedPriceListener(Listener<Double> newListener){
        synchronized (predictedListeners){
            predictedListeners.add(newListener);
        }
    }

    public void removeListenerByHash(int hash){
        synchronized (currentPriceListeners){
            for(int i = 0; i < currentPriceListeners.size(); i++){
                if(currentPriceListeners.get(i).hash() == hash){
                    currentPriceListeners.remove(i);
                    i--;
                }
            }
        }
    }

    public void removePredictedListenerByHash(int hash){
        synchronized (predictedListeners){
            for(int i = 0; i < predictedListeners.size(); i++){
                if(predictedListeners.get(i).hash() == hash){
                    predictedListeners.remove(i);
                    i--;
                }
            }
        }
    }

    @Autowired
    @Qualifier("dbSaver")
    public void addDataBaseListener(SavingCurrentPriceListener dataBase){
        addListener(dataBase);
    }

    @Autowired
    @Qualifier("kafkaListener")
    public void addKafkaListener(KafkaListener listener){
        addListener(listener);
    }

    public final static PriceDispatcher dispatcher = new PriceDispatcher(new XMarketsPriceGetter(), new PredictionConsumer());

}
