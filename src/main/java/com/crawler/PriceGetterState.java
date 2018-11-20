package com.crawler;

public class PriceGetterState {

    private boolean isActive;

    public PriceGetterState() {
        this.isActive = false;
    }

    public PriceGetterState(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }
}
