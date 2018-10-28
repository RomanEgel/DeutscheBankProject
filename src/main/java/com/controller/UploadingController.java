package com.controller;


import com.domain.BrentDates;
import com.domain.BrentOil;
import com.repository.BrentOilRepository;
import jdk.nashorn.internal.parser.JSONParser;
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
import java.io.Reader;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

@Controller
public class UploadingController {

    @Autowired
    BrentOilRepository brentOilRepository;

    @Value("${intrinioUsername}")
    String username;

    @Value("${intrinioPassword}")
    String password;

    @PostMapping(value = "/uploadValues", consumes = {"application/json"})
    public ResponseEntity handleJsonUpload(@RequestBody BrentDates dates) {
        brentOilRepository.deleteAll();
        boolean EOF = false;
        int current_page = 1;
        BufferedReader responseBody = null;
        HttpClient client = HttpClientBuilder.create().build();

        HttpGet request = new HttpGet();

        //Set Http Headers
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Authorization", "Basic " +
                Base64.getEncoder().encodeToString((username + ":" + password).getBytes()));

        while(!EOF) {
            try {
                //Define a HttpGet request
                request.setURI(new URI("https://api.intrinio.com/prices?identifier=BNO:UP&start_date=" +
                        dates.getFromDate() + "&end_date=" + dates.getToDate() + "&frequency=daily&sort_order=asc&page_size=100&current_page=" + current_page));

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

                    loadInDBValues(jsonObject.getJSONArray("data"));

                    if(current_page == jsonObject.getInt("total_pages")){
                        EOF = true;
                    }

                    current_page++;

                }
            } catch (Exception e) {
                e.printStackTrace();
                EOF = true;
            } finally {
                if (responseBody != null)
                    try {
                        responseBody.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
            }
        }

        return new ResponseEntity(HttpStatus.OK);
    }


    @GetMapping(value = "/get_values", produces = {"application/json"} )
    public @ResponseBody Object[] showAllValues(){
        ArrayList<BrentOil> list = new ArrayList<>();
        brentOilRepository.findAll().forEach(brentOil1 -> list.add(brentOil1));

        return list.toArray();
    }


    public void loadInDBValues(JSONArray brentOils) throws JSONException, ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        brentOilRepository.deleteAll();
        JSONObject brent;
        for(int i=0; i< brentOils.length();i++){
            brent = brentOils.getJSONObject(i);
            Date date = format.parse(brent.getString("date"));
            brentOilRepository.save(new BrentOil(date,brent.getDouble("open")));
        }
    }

}
