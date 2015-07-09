package com.csslab.shengji.tools;

/**
 * Created by Administrator on 2015/7/8 0008.
 */
public class MessageManagement {
    //other msg
    public final static int O_ERROR = 0;
    //write msg,player can write
    public final static int W_NEW_USER_JOIN = 101;//新用户加入
    public final static int W_SET_NAME = 100;//用户设置名称
    public final static int W_SHOUT = 99;//用户叫牌
    public final static int W_SHOUT_GET_BOTTOM = 98;//叫牌取底
    public final static int W_SHOUT_PUT_BOTTOM = 97;//叫牌埋底
    public final static int W_REVOLUTION = 96;//用户反牌
    public final static int W_REVOLUTION_PUT_BOTTOM = 95;//反牌取底
    public final static int W_REVOLUTION_GET_BOTTOM = 94;//反牌埋底
    public final static int W_PLAY = 93;//用户打牌
    //read msg,player can read
    public final static int R_GAME_CREATED = 1001;//游戏创建成功，可以开始设置用户名了
    public final static int R_GAME_TIPS = 1000;//游戏提示
    public final static int R_USER_READY = 999;//其他用户准备加入
    public final static int R_USER_SIT = 898;//用户(自己)入座
    public final static int R_DESK_READY = 998;//牌桌准备好，可以出牌
    public final static int R_SHOUT = 997;//玩家可以喊牌
    public final static int R_SHOUT_MSG = 897;//其他用户喊牌消息
    public final static int R_SHOUT_RESULT = 898;//用户（自己）喊牌结果,如果最终喊成功由该用户摸底
    public final static int R_TAKEING = 996;//用户摸牌
    public final static int R_TAKEED = 995;//摸牌完毕
    public final static int R_REVOLUTION_RESULT = 994;//反牌
    public final static int R_PLAYING = 993;//出牌阶段
    public final static int R_PLAY_TURN = 992;//轮到出牌
    public final static int R_SCORE = 991;//分数更新
    public final static int R_NEW_TURN = 990;//新一轮出牌开始
    public final static int R_NEW_ROUND = 899;//新的一局开始

}
