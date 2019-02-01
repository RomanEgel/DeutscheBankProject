package com.twoez.crawler;

import com.twoez.common.HTMLReaderHelper;
import com.twoez.domain.BrentOil;
import com.twoez.domain.BrentOilPK;
import com.twoez.domain.SourceName;
import com.twoez.repository.BrentOilRepository;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

@Component
public class XMarketsPriceGetter implements PriceGetter{
    private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private Consumer<BrentOil> listener;

    private URL url;


    private BrentOilRepository brentOilRepository;

    public XMarketsPriceGetter(){
        try {
            url = new URL("https://www.xmarkets.db.com/DE/ENG/Underlying-Detail/XC0009677409");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BrentOil getCurrentPrice() {
        HTMLReaderHelper helper = new HTMLReaderHelper(url);
        HTMLReaderHelper.StringIterator iterator = helper.stringIterator();
        List<String> entries = new ArrayList<>(2);
        while(entries.size() < 2){
            String item = iterator.next();
            if(item.contains("<span class=\"instrumentDetail-push\"")||item.contains("<span class=\"dateToUpdate\"")){
                entries.add(item);
            }
        }
        return new BrentOil(new BrentOilPK(getDate(entries.get(0)),SourceName.RealTimeXMarkets),getPrice(entries.get(1)));
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
        return DateUtils.addHours(date,2);
    }

    private double getPrice(String findIn){
        String result = findIn.substring(findIn.indexOf('>'), findIn.lastIndexOf("</span><span")).substring(1);
        return Double.valueOf(result);
    }

    public void setConsumer(Consumer<BrentOil> consumer){
        listener = consumer;
    }

    @Override
    public void run() {
        if(listener == null){
            throw new RuntimeException("Listener was not provided");
        }
        BrentOil price = getCurrentPrice();
        BrentOil previousPrice = null;
        int count = 0;
        while (true){
             if(price.equals(previousPrice) || (previousPrice != null && price.getId().getPrice_timestamp().before(previousPrice.getId().getPrice_timestamp()))){
                 try {
                     Thread.sleep(300);
                     price = getCurrentPrice();
                     count++;
                     if(count > 15){
                         listener.accept(price);
                         count = 0;
                     }
                 } catch (InterruptedException ex){
                     return;
                 }
             } else{
                 listener.accept(price);
                 previousPrice = price;
                 price = getCurrentPrice();
                 count = 0;
             }
        }
    }
}
