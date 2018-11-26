package com.twoez.historyuploader;

import com.twoez.common.HTMLReaderHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;

public class JSONPriceGetter {

    public static final String SOURCE_NAME="HistoryProfinance";

    private final List<String> timeResolutions;

    private final Map<Timestamp, Double> prices = new HashMap<>();

    private final String urlUnformattedString = "https://charts.profinance.ru/html/tw/history?symbol=27&resolution=%s&from=582768000&to=%s";

    private final String TIMESTAMP_FIELD_NAME = "t";

    private final String PRICE_FIELD_NAME = "o";

    private final Long currentTimestamp;

    JSONPriceGetter(){
        timeResolutions = new ArrayList<>(Arrays.asList("W","D","240","120","60","30","15","5","3","1"));
        currentTimestamp = System.currentTimeMillis() / 1000;
    }

    public Map<Timestamp, Double> getPrices(){

        if(prices.size() > 0){
            return prices;
        }

        for(int i = 0; i < timeResolutions.size(); i++){
            try {
                fill(timeResolutions.get(i));
            } catch (IOException ex){
                return null;
            }
        }

        return prices;
    }

    private void fill(String resolution) throws IOException {
        URL temporaryUrl = new URL(String.format(urlUnformattedString,resolution,currentTimestamp));
        JSONObject response = new JSONObject(new HTMLReaderHelper(temporaryUrl).stringIterator().next());
        JSONArray timestampJSONArray = getTimestamps(response);
        JSONArray priceJSONArray = getPrices(response);
        assert timestampJSONArray.length() == priceJSONArray.length();
        for(int i = 0; i < timestampJSONArray.length(); i++){
            prices.put(new Timestamp(1000 * timestampJSONArray.getLong(i)), priceJSONArray.getDouble(i));
        }
    }

    private JSONArray getTimestamps(JSONObject response){
        return response.getJSONArray(TIMESTAMP_FIELD_NAME);
    }

    private JSONArray getPrices(JSONObject response){
        return response.getJSONArray(PRICE_FIELD_NAME);
    }
}
