package com.twoez;

public interface Listener<R> {
    void onUpdate(R value);

    int hash();
}
