package com.csslab.shengji.core;

/**
 * Created by Administrator on 2015/7/7 0007.
 */
public interface OnPlayerTakedListener {
    void onTaking(PlayerEvent event);
    void onTaked(PlayerEvent event);
    void onBottomTaking(PlayerEvent event);
    void onBottomTaked(PlayerEvent event);
    void onRevolution(Player event);
}
