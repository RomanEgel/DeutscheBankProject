package com.twoez.listener;

public interface Listener<R> {
    void onUpdate(R value);

    int hash();

}
