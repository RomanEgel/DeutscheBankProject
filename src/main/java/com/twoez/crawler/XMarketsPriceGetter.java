package com.twoez.crawler;

import com.twoez.domain.BrentDates;
import com.twoez.domain.BrentOil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class XMarketsPriceGetter implements PriceGetter{
    private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private URL url;

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
        return new BrentOil(getDate(entries.get(0)),getPrice(entries.get(1)));
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

    @Override
    public ArrayList<BrentOil> getHistory(BrentDates brentDates) {
        return null;
    }
}
