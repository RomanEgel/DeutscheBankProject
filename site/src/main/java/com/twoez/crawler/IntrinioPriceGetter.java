package com.twoez.crawler;

import com.twoez.domain.BrentDates;
import com.twoez.domain.BrentOil;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

public class IntrinioPriceGetter implements PriceGetter {

    private int currentIndex = 0;

    private String username;
    private String password;

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public IntrinioPriceGetter(String userName, String password){
        this.username = userName;
        this.password = password;
    }


    public IntrinioPriceGetter(){}

    @Override
    public BrentOil getCurrentPrice() {
        return null;
    }

    @Override
    public ArrayList<BrentOil> getHistory(BrentDates brentDates) {
        ArrayList<BrentOil> history = null;
        boolean EOF = false;
        int current_page = 1;
        BufferedReader responseBody = null;
        HttpClient client = HttpClientBuilder.create().build();

        HttpGet request = new HttpGet();

        //Set Http Headers
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Authorization", "Basic " +
                Base64.getEncoder().encodeToString((username + ":" + password).getBytes()));
        this.currentIndex = 0;
        while(!EOF) {
            try {
                //Define a HttpGet request
                request.setURI(new URI("https://api.intrinio.com/prices?identifier=BNO:UP&start_date=" +
                        brentDates.getFromDate() + "&end_date=" + brentDates.getToDate() + "&frequency=daily&sort_order=asc&page_size=100&page_number=" + current_page));
                //Invoke the service
                HttpResponse response = client.execute(request);

                //Verify if the response is valid
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    throw new RuntimeException("Failed with HTTP error code : " + statusCode);
                } else {
                    //If valid, get the response
                    responseBody = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    JSONObject jsonObject = new JSONObject(responseBody.readLine());
                    //save in
                    JSONArray array = jsonObject.getJSONArray("data");
                    if(this.currentIndex == 0) {
                        history = new ArrayList<>(jsonObject.getInt("result_count"));
                    }

                    for(int i = 0; i< array.length();i++){
                        try {
                            history.add(this.currentIndex,getBrentOilFromJson(array.getJSONObject(i)));
                            currentIndex++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    if(current_page == jsonObject.getInt("total_pages")){
                        EOF = true;
                    }
                    current_page++;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (responseBody != null)
                    try {
                        responseBody.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                        return null;
                    }
            }
        }
        return history;
    }


    private BrentOil getBrentOilFromJson(JSONObject brent){
        try {
            Date date = format.parse(brent.getString("date"));
            return new BrentOil(date,brent.getDouble("open"));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
