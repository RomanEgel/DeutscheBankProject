package com.controller;

import com.repository.BrentOilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired
    BrentOilRepository brentOilRepository;

    @GetMapping("/")
    public String greeting(){
        return "greeting";
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
        return "uploadCurrent";
    }


}
