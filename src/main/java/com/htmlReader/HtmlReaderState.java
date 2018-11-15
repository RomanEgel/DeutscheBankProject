package com.htmlReader;

public class HtmlReaderState {
   //private enum state{Off,Active,Closing,Opening}

    private boolean isActive;

    public HtmlReaderState() {
        this.isActive = false;
    }

    public HtmlReaderState(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }
}
