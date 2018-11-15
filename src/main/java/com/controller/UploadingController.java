package com.controller;


import com.manager.DBManager;
import com.domain.BrentDates;
import com.domain.BrentOil;
import com.prediction.KalmanFilter;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class UploadingController {

    @Autowired
    DBManager dbManager;

    private BrentOil[] brentOilPrices = null;
    private int currentIndex = 0;

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Value("${intrinioUsername}")
    private String username;

    @Value("${intrinioPassword}")
    private String password;

    @PostMapping(value = "/uploadValues", consumes = {"application/json"})
    public ResponseEntity handleJsonUpload(@RequestBody BrentDates dates) {
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
                        dates.getFromDate() + "&end_date=" + dates.getToDate() + "&frequency=daily&sort_order=asc&page_size=100&page_number=" + current_page));

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
                        brentOilPrices = new BrentOil[jsonObject.getInt("result_count")];
                    }

                    for(int i = 0; i< array.length();i++){
                        try {
                           brentOilPrices[this.currentIndex] = getBrentOilFromJson(array.getJSONObject(i));
                           currentIndex++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    }

                    if(current_page == jsonObject.getInt("total_pages")){
                        EOF = true;
                    }

                    current_page++;

                }
            } catch (Exception e) {
                e.printStackTrace();
                new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
                EOF = true;
            } finally {
                if (responseBody != null)
                    try {
                        responseBody.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                        new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
            }
        }

        dbManager.setPrices(brentOilPrices);
        new Thread(dbManager).start();
        //predictPrice(brentOilPrices);

        return new ResponseEntity(HttpStatus.OK);
    }


    @GetMapping(value = "/get_values", produces = {"application/json"} )
    public @ResponseBody Object[] showAllValues(){
        if(brentOilPrices != null){
            return brentOilPrices;
        } else {
            ArrayList<BrentOil> list = dbManager.getPricesFromDB();
            if(list.size()==0 || list.isEmpty()){
                return null;
            }
            return list.toArray();
        }
    }


    public BrentOil getBrentOilFromJson(JSONObject brent){
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

    public List<BrentOil> predictPrice(BrentOil[] prices){
        List<BrentOil> predictedPrices = new ArrayList<BrentOil>(Arrays.asList(prices));
        double volatility = Collections.max(predictedPrices,
                (o1, o2) -> o1.getOpen() > o2.getOpen() ? 1 : 0).getOpen() -
                Collections.min(predictedPrices, (o1, o2) -> o1.getOpen() > o2.getOpen() ? 0 : 1).getOpen();
        KalmanFilter kalmanFilter = new KalmanFilter(1, volatility, 1, volatility / 2);
        kalmanFilter.setState(predictedPrices.get(0).getOpen() + 1,0.1);
        for (int i = 0; i < predictedPrices.size() - 2;i++) {
            kalmanFilter.correct(predictedPrices.get(i).getOpen());
            predictedPrices.get(i).setOpen(kalmanFilter.getState());
            System.out.println("price " + i + " : predicted " +  predictedPrices.get(i).getOpen() +
                    ", actual " +  predictedPrices.get(i+1).getOpen());
        }
        return predictedPrices;
    }

}
