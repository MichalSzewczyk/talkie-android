package com.talkie.talkie_android;

import java.io.Serializable;

public class Tuple<L, P> implements Serializable {
    private final L l;
    private final P p;

    public Tuple(L l, P p) {
        this.l = l;
        this.p = p;
    }

    public L getL() {
        return l;
    }

    public P getP() {
        return p;
    }
}
