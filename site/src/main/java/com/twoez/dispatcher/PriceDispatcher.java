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
        predictionGetter.setPolicy(this::notifyPredictionListeners);
        new Thread(priceGetter).start();
        new Thread(predictionGetter).start();
    }

    private void notifyListeners(BrentOil price){
        notifyListeners(price,currentPriceListeners);
    }

    private void notifyPredictionListeners(Double predictedPrice){
        notifyListeners(predictedPrice,predictedListeners);
    }

    private <T> void notifyListeners(T value, List<Listener<T>> list){
        new Thread(()->{
            List<Listener<T>> listenersCopy;
            synchronized (list){
                listenersCopy = new ArrayList<>(list);
            }
            for (int i = listenersCopy.size() - 1; i >= 0; i--) {
                listenersCopy.get(i).onUpdate(value);
            }
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

    public void removeListenerByHash(String hash){
        removeListener(currentPriceListeners,hash);
    }

    public void removePredictedListenerByHash(String hash){
        removeListener(predictedListeners,hash);
    }

    private <T> void removeListener(List<Listener<T>> listeners, String hash){
        synchronized (listeners){
            for(int i = 0; i < listeners.size(); i++){
                if(listeners.get(i).hash() == hash){
                    listeners.remove(i);
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
