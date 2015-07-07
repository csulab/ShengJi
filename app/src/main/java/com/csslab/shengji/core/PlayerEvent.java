package com.csslab.shengji.core;

import java.util.EventObject;

/**
 * Created by Vadin on 2015/6/29.
 */
public class PlayerEvent extends EventObject {
    private Poker mPoker;
    private final int POKER_SUM = 25;
    public PlayerEvent(Object source,Poker p) {
        super(source);
        this.mPoker = p;
    }
    public Poker getNewPoker(){return this.mPoker;}
}
