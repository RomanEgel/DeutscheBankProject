package com.twoez.dispatcher;

import com.twoez.crawler.PriceGetter;
import com.twoez.domain.BrentOil;
import com.twoez.listener.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class PriceDispatcher {
    private List<Listener<BrentOil>> currentPriceListeners;

    private PriceGetter priceGetter;


    private void notifyListeners(BrentOil price){
        new Thread(()->{
            List<Listener<BrentOil>> listenersCopy;
            synchronized (currentPriceListeners){
                listenersCopy = new ArrayList<>(currentPriceListeners);
            }
            listenersCopy.forEach((listener)->listener.onUpdate(price));
        }).start();
    }

    @PostConstruct
    public void initPriceDispatcher(){
        priceGetter.setConsumer(this::notifyListeners);
        new Thread(priceGetter).start();
    }

    public void addListener(Listener<BrentOil> newListener){
        synchronized (currentPriceListeners){
            currentPriceListeners.add(newListener);
        }
    }

    public void removeListenerByClient(SimpMessagingTemplate client){
        int clientHash = client.hashCode();
        synchronized (currentPriceListeners){
            for(int i = 0; i < currentPriceListeners.size(); i++){
                if(currentPriceListeners.get(i).hash() == clientHash){
                    currentPriceListeners.remove(i);
                    i--;
                }
            }
        }
    }

    @Autowired
    public void setPriceGetter(PriceGetter priceGetter) {
        this.priceGetter = priceGetter;
    }

    @Autowired
    @Qualifier("dbSaver")
    public void setCurrentPriceListeners(List<Listener<BrentOil>> currentPriceListeners) {
        this.currentPriceListeners = currentPriceListeners;
    }
}
