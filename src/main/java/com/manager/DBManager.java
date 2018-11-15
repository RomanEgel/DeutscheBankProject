package com.manager;

import com.domain.BrentOil;
import com.repository.BrentOilRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class DBManager implements Runnable{

    BrentOilRepository brentOilRepository;

    BrentOil[] prices;

    public DBManager(BrentOilRepository rep){
        brentOilRepository = rep;
    }

    public void setPrices(BrentOil[] arr){
        prices = arr;
    }

    public ArrayList<BrentOil> getPricesFromDB(){
        ArrayList<BrentOil> list = new ArrayList<>();
        brentOilRepository.findAll().forEach(brentOil1 -> list.add(brentOil1));
        return list;
    }

    @Override
    public void run() {
        brentOilRepository.deleteAll();
        for (BrentOil price : prices) {
            brentOilRepository.save(price);
        }
        System.out.println("DBManager thread is done");
    }
}
