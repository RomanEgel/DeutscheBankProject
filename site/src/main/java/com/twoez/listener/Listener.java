package com.twoez.listener;

public interface Listener<R> {
    void onUpdate(R value);

    String hash();

}
