package com.actionHandlers;

import com.crawler.PriceGetter;
import com.domain.BrentOil;
import org.springframework.messaging.simp.SimpMessagingTemplate;


public class CurrentPriceViewer implements Runnable {

    private SimpMessagingTemplate simpMessagingTemplate;
    private PriceGetter priceGetter;

    private volatile boolean endFlag = false;
    private BrentOil prevPrice = null;

    public CurrentPriceViewer(SimpMessagingTemplate smt, PriceGetter priceGetter){
        simpMessagingTemplate = smt;
        this.priceGetter = priceGetter;
    }

    private void changeCurrentPrice(BrentOil brentOil) {
        simpMessagingTemplate.convertAndSend("/prices/getCurrent", brentOil);
    }

    public void endConnection(){
        simpMessagingTemplate.convertAndSend("/prices/getCurrent", "Disconnect");
    }



    public boolean isEndFlag() {
        return endFlag;
    }

    public void setEndFlag(boolean endFlag) {
        this.endFlag = endFlag;
    }

    @Override
    public void run() {
        while(!endFlag){
            BrentOil bo = priceGetter.getCurrentPrice();
            if(!bo.equals(prevPrice)){
                changeCurrentPrice(bo);
                prevPrice = bo;
            }
        }
        System.out.println("disconnected");
    }


}
