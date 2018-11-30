package com.twoez;

import com.twoez.crawler.PriceGetter;
import com.twoez.domain.BrentOil;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;

public class PriceDispatcher {
    private final List<Listener<BrentOil>> currentPriceListeners;

    private PriceDispatcher(){
        currentPriceListeners = new ArrayList<>();
    }

    private void notifyListeners(BrentOil price){
        new Thread(()->{
            List<Listener<BrentOil>> listenersCopy;
            synchronized (currentPriceListeners){
                listenersCopy = new ArrayList<>(currentPriceListeners);
            }
            listenersCopy.forEach((listener)->{listener.onUpdate(price);});
        }).start();
    }

    public void setCurrentPriceUpdater(PriceGetter getter){
        getter.setConsumer(this::notifyListeners);
        new Thread(getter).start();
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
    public final static PriceDispatcher dispatcher = new PriceDispatcher();
}
