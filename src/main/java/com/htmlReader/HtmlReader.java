package com.htmlReader;

import com.domain.BrentOil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HtmlReader implements Runnable {

    private SimpMessagingTemplate simpMessagingTemplate;

    private URL url;
    private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private volatile boolean endFlag = false;
    private BrentOil prevPrice = null;

    public HtmlReader(SimpMessagingTemplate smt){
        simpMessagingTemplate = smt;
        try {
            url = new URL("https://www.xmarkets.db.com/DE/ENG/Underlying-Detail/XC0009677409");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private BrentOil getCurrentPrice(){
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(url.openStream()));
        } catch (IOException ex){
            ex.printStackTrace();
            return null;
        }
        List<String> strings = new ArrayList<>(2);
        in.lines().filter(item -> { return item.contains("<span class=\"instrumentDetail-push\"")||item.contains("<span class=\"dateToUpdate\"");})
                .forEach(item ->{
                    strings.add(item);
                });
        BrentOil brentOil = new BrentOil(getDate(strings.get(0)),getPrice(strings.get(1)));
        return brentOil;
    }

    private void changeCurrentPrice(BrentOil brentOil) {
        simpMessagingTemplate.convertAndSend("/prices/getCurrent", brentOil);
    }

    public void endConnection(){
        simpMessagingTemplate.convertAndSend("/prices/getCurrent", "Disconnect");
    }

    private Date getDate(String findIn){
        String stringDate = findIn.substring(findIn.lastIndexOf("\"dd.MM.yyyy\""), findIn.indexOf(", </span><span source"));
        String stringTime = findIn.substring(findIn.indexOf("\"instrumentDetail-push\">"), findIn.lastIndexOf("</span>"));
        Date date;
        try {
            date = format.parse(stringDate.substring(stringDate.indexOf('>') + 1) + " " + stringTime.substring(stringTime.indexOf('>') + 1));
        } catch (ParseException ex){
            return null;
        }
        return date;
    }

    private double getPrice(String findIn){
        String result = findIn.substring(findIn.indexOf('>'), findIn.lastIndexOf("</span><span")).substring(1);
        return Double.valueOf(result);
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
            BrentOil bo = getCurrentPrice();
            if(!bo.equals(prevPrice)){
                changeCurrentPrice(bo);
                prevPrice = bo;
            }
        }
        System.out.println("disconnected");
    }


}
