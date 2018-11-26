package com.twoez.crawler;

import com.twoez.domain.BrentDates;
import com.twoez.domain.BrentOil;

import java.util.ArrayList;

public interface PriceGetter {
    BrentOil getCurrentPrice();

    ArrayList<BrentOil> getHistory(BrentDates brentDates);
}
