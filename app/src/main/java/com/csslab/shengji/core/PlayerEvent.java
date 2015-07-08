package com.csslab.shengji.core;

import java.util.EventObject;

/**
 * Created by Vadin on 2015/6/29.
 */
public class PlayerEvent extends EventObject {
    private Poker mPoker;
    public PlayerEvent(Object source,Poker p) {
        super(source);
        this.mPoker = p;
    }
    public Poker getNewPoker(){return this.mPoker;}
}
