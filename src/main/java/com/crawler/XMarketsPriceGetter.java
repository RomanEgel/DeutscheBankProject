package com.crawler;

import com.domain.BrentDates;
import com.domain.BrentOil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class XMarketsPriceGetter implements PriceGetter{
    private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public XMarketsPriceGetter(){

    }

    @Override
    public BrentOil getCurrentPrice() {
        Document doc = null;
        try {
            doc = Jsoup.connect("https://www.xmarkets.db.com/DE/ENG/Underlying-Detail/XC0009677409").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element elem = doc.getElementsByAttributeValue("class","section sec-0 first").first();
        String price = elem.getElementsByAttributeValue("field","bid").first().text();
        String date = elem.getElementsByAttributeValue("class","dateToUpdate").first().text();
        String time = elem.getElementsByAttributeValue("field", "quotetime").first().text();

        double value = 0.0;
        Date dt = null;
        try {
            value = Double.parseDouble(price);
            dt = format.parse(date.substring(0,date.length()-1) + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new BrentOil(dt,value);
    }

    @Override
    public ArrayList<BrentOil> getHistory(BrentDates brentDates) {
        return null;
    }
}
