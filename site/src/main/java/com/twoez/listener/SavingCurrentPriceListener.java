package com.twoez.listener;

import com.twoez.domain.BrentOil;
import com.twoez.repository.BrentOilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("dbSaver")
public class SavingCurrentPriceListener implements Listener<BrentOil> {

    private BrentOilRepository brentOilRepository;

    @Override
    public void onUpdate(BrentOil value) {
        brentOilRepository.save(value);
    }

    @Override
    public int hash() {
        return brentOilRepository.hashCode();
    }

    @Autowired
    public void setBrentOilRepository(BrentOilRepository brentOilRepository) {
        this.brentOilRepository = brentOilRepository;
    }
}
