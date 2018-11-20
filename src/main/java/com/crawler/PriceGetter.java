package com.crawler;

import com.domain.BrentDates;
import com.domain.BrentOil;

import java.util.ArrayList;

public interface PriceGetter {
    BrentOil getCurrentPrice();

    ArrayList<BrentOil> getHistory(BrentDates brentDates);
}
