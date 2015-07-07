package com.csslab.shengji.gamelogic;

/**
 * Created by Administrator on 2015/7/6 0006.
 */
public class Poker {
    //花色枚举变量
    public enum PokerColor {DIAMONDS,CLUB, HEARTS,SPADE, JOKER, KING };//{方块,梅花,红桃,黑桃}//SPADE, HEARTS, CLUB, DIAMONDS
    public enum PokerType{MAINPOKER,STRONGPOKER,NORMALPOKER}//主牌（大小王，2，3等），硬主（当前所喊花色），副牌
    public static final int JOKER = 17;//小王
    public static final int KING = 18;//大王
    public static final int BOSS = 16;//当前打几

    private PokerColor mColor;//花色
    private int mCardSize;//牌面大小
    private String mPokerImage;//
    private int value;//牌的实际大小值


    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Poker(int cardSize) {
        this.mCardSize = cardSize;
        value = cardSize;
    }

    public Poker(PokerColor color, int cardSize) {
        mColor = color;
        mCardSize = cardSize;
        value = cardSize;
    }

    public Poker(PokerColor color, int cardSize, String PokerImage) {
        this.mColor = color;
        this.mCardSize = cardSize;
        this.mPokerImage = PokerImage;
    }

    public String toString() {
        if(mCardSize >1 && mCardSize <11 ){
            return mColor.toString()+mCardSize;
        }
        if(mCardSize >10 && mCardSize <14){
            String size = "";
            switch (mCardSize) {
                case 11:
                    size="J";
                    break;
                case 12:
                    size="Q";
                    break;
                case 13:
                    size="K";
                    break;
                default:
                    break;
            }
            return mColor.toString()+size;
        }
        if(mCardSize == 14){
            return mColor.toString()+"A";
        }
        if(mCardSize == 15){
            return mColor.toString()+"2";
        }
        else{
            String color="";
            if(mCardSize == JOKER){
                color = "小王";
            }
            if(mCardSize == KING){
                color = "大王";
            }
            return color;
        }
    }

    public PokerColor getmColor() {
        return mColor;
    }

    public void setmColor(PokerColor mColor) {
        this.mColor = mColor;
    }

    public int getmCardSize() {
        return mCardSize;
    }

    public void setmCardSize(int mCardSize) {
        this.mCardSize = mCardSize;
    }

    public String getmPokerImage() {
        return mPokerImage;
    }

    public void setmPokerImage(String mPokerImage) {
        this.mPokerImage = mPokerImage;
    }
    public static String getPoker(int mCardSize){
        if(mCardSize >1 && mCardSize <11 ){
            return ""+mCardSize;
        }
        if(mCardSize >10 && mCardSize <14){
            String size = "";
            switch (mCardSize) {
                case 11:
                    size="J";
                    break;
                case 12:
                    size="Q";
                    break;
                case 13:
                    size="K";
                    break;
                default:
                    break;
            }
            return size;
        }
        if(mCardSize == 14){
            return "A";
        }
        if(mCardSize == 15){
            return "2";
        }
        else{
            String color="";
            if(mCardSize == JOKER){
                color = "小王";
            }
            if(mCardSize == KING){
                color = "大王";
            }
            return color;
        }
    }
}
