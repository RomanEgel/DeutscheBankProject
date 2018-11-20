package com.actionHandlers;

import com.domain.BrentOil;
import com.repository.BrentOilRepository;

import java.util.ArrayList;

public class HistoryPriceSaver implements Runnable {

    private BrentOilRepository brentOilRepository;

    public HistoryPriceSaver(BrentOilRepository bro){
        brentOilRepository = bro;
    }

    private ArrayList<BrentOil> history = null;

    private boolean active = false;

    public ArrayList<BrentOil> getHistory() {
        if (history != null)
            return history;
        return getPricesFromDB();
    }

    public void setHistory(ArrayList<BrentOil> history) {
        this.history = history;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void run() {
        synchronized (HistoryPriceSaver.class) {
            active = true;
            for (int i = 0; i < history.size(); i++) {
                brentOilRepository.save(history.get(i));
            }
            active = false;
        }
    }

    private ArrayList<BrentOil> getPricesFromDB() {
        ArrayList<BrentOil> list = new ArrayList<>();
        brentOilRepository.findAll().forEach(brentOil1 -> list.add(brentOil1));
        return list;
    }
}
