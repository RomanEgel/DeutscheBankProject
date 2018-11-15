package com.controller;

import com.htmlReader.HtmlReader;
import com.htmlReader.HtmlReaderState;
import com.repository.BrentOilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@Controller
public class MainController {

    private HtmlReader htmlReader = null;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/")
    public String greeting(){
        return "greeting";
    }

    @GetMapping("/getCurrent")
    public String getCurrent(){
        return "getCurrent";
    }

    @MessageMapping("/state")
    public void changeState(HtmlReaderState state){
        if(state.getIsActive()){
            if(htmlReader==null) {
                htmlReader = new HtmlReader(simpMessagingTemplate);
                new Thread(htmlReader).start();
            }
        } else {
            if(htmlReader != null) {
                htmlReader.setEndFlag(true);
                htmlReader.endConnection();
            }
        }
    }

    @GetMapping("/getValues")
    public String getValues(){
        return "getValues";
    }

    @GetMapping("/uploadValues")
    public String uploadValues(){
        return "uploadValues";
    }

    @GetMapping("/uploadCurrent")
    public String uploadCurrent(){
        return "getCurrent";
    }


}
