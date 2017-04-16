package com.talkie.talkie_android;

import java.io.Serializable;

public class Tuple<L, P> implements Serializable {
    private final L key;
    private final P value;

    public Tuple(L key, P value) {
        this.key = key;
        this.value = value;
    }

    public L getKey() {
        return key;
    }

    public P getValue() {
        return value;
    }
}
