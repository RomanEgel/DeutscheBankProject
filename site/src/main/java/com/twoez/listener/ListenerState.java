package com.twoez.listener;

public class ListenerState {

    private boolean isActive;

    private boolean isPrediction;

    public ListenerState() {
        this.isActive = false;
    }

    public ListenerState(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public boolean isPrediction() {
        return isPrediction;
    }

    public void setPrediction(boolean prediction) {
        isPrediction = prediction;
    }

}
