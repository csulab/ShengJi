package com.csslab.shengji.core;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

/**
        * Created by Vadin on 2015/6/29.
        */
public class Player{
    //玩家事件相关
    //private Vector<PlayerOnGetcardListener> reposity = new Vector<>();//Vector是安全的
    //private PlayerOnGetcardListener listener = null;
    private OnPlayerTakedListener listener = null;
    //玩家所有牌
    private List<Poker> mPokerList = new ArrayList<Poker>();
    //黑 红 梅 方
    private List<Poker> mSpadeList = new ArrayList<Poker>();
    private List<Poker> mHeartsList = new ArrayList<Poker>();
    private List<Poker> mClubList = new ArrayList<Poker>();
    private List<Poker> mDiamondsList = new ArrayList<Poker>();
    //主牌
    private List<Poker> mMainPokerList = new ArrayList<Poker>();
    //玩家出过的牌
    private Stack<Poker> mUsedPokerStack = new Stack<Poker>();
    //已经出过的牌
    private List<Poker> historyPoker = new ArrayList<Poker>();
    //玩家昵称
    private String name;
    private int seat;//座位号
    //当前回合打牌
    private Poker roundPoker;

    public Player(String name){
        this.name = name;
    }
    public void setSeat(int index){
        this.seat = index;
    }
    public int getSeat(){
        return this.seat;
    }

    public String getName() {
        return name;
    }

    public void SetRoundPoker(int currentRound,int currentColor){
        this.roundPoker = new Poker(Poker.PokerColor.values()[currentColor],currentRound);
    }

    public int restPoker(){
        return mPokerList.size();
    }
    //叫主
    public void shout(){

    }
    //过主
    public List<Poker> send(){
        return null;
    }
    //返牌 相对于过主
    public void receive(){

    }
    //重新排序
    public void sortPokerList(int currentRound,Poker.PokerColor color){
        mPokerList.clear();
        switch(color){
            //case SPADE:default;
            case HEARTS:
                mPokerList.addAll(mDiamondsList);
                mPokerList.addAll(mClubList);
                mPokerList.addAll(mSpadeList);
                mPokerList.addAll(mHeartsList);
                break;
            case CLUB:
                mPokerList.addAll(mDiamondsList);
                mPokerList.addAll(mHeartsList);
                mPokerList.addAll(mSpadeList);
                mPokerList.addAll(mClubList);
                break;
            case DIAMONDS:
                mPokerList.addAll(mClubList);
                mPokerList.addAll(mHeartsList);
                mPokerList.addAll(mSpadeList);
                mPokerList.addAll(mDiamondsList);
                break;
            default:
                mPokerList.addAll(mDiamondsList);
                mPokerList.addAll(mClubList);
                mPokerList.addAll(mHeartsList);
                mPokerList.addAll(mSpadeList);
                break;
        }
        mPokerList.addAll(mMainPokerList);
    }
    //拼凑扑克牌
    private void joinPokerList(){
        mPokerList.clear();
        mPokerList.addAll(mDiamondsList);
        mPokerList.addAll(mClubList);
        mPokerList.addAll(mHeartsList);
        mPokerList.addAll(mSpadeList);
        mPokerList.addAll(mMainPokerList);
    }
    //拿牌
    public void takePoker(Poker p,int CurrentRound){
        notifyPlayerEvent(new PlayerEvent(this, p));
        if(p.getmCardSize() > 14 || p.getmCardSize() == CurrentRound){
            if(mMainPokerList.size()==0){
                mMainPokerList.add(p);
                joinPokerList();//排序
                return;
            }
            else{
                int len = mMainPokerList.size();
                for(int i=0;i<len;i++){
                    if(p.getValue() < mMainPokerList.get(i).getValue()){
                        mMainPokerList.add(i,p);
                        joinPokerList();
                        return;
                    }
                }
                mMainPokerList.add(p);
                joinPokerList();
                return;
            }
        }
        else{
            switch(p.getmColor().ordinal()){
                case 0:
                    if(mSpadeList.size()==0){
                        mSpadeList.add(p);
                        joinPokerList();
                    }
                    else{
                        int len = mSpadeList.size();
                        for(int i=0;i<len;i++){
                            if(p.getmCardSize() > mSpadeList.get(i).getmCardSize()){
                                mSpadeList.add(i,p);
                                joinPokerList();
                                return;
                            }
                        }
                        mSpadeList.add(p);
                        joinPokerList();
                    }
                    break;
                case 1:
                    if(mHeartsList.size()==0){
                        mHeartsList.add(p);
                        joinPokerList();
                    }
                    else{
                        int len = mHeartsList.size();
                        for(int i=0;i<len;i++){
                            if(p.getmCardSize() > mHeartsList.get(i).getmCardSize()){
                                mHeartsList.add(i,p);
                                joinPokerList();
                                return;
                            }
                        }
                        mHeartsList.add(p);
                        joinPokerList();
                    }
                    break;
                case 2:
                    if(mClubList.size()==0){
                        mClubList.add(p);
                        joinPokerList();
                    }
                    else{
                        int len = mClubList.size();
                        for(int i=0;i<len;i++){
                            if(p.getmCardSize() > mClubList.get(i).getmCardSize()){
                                mClubList.add(i,p);
                                joinPokerList();
                                return;
                            }
                        }
                        mClubList.add(p);
                        joinPokerList();
                    }
                    break;
                case 3:
                    if(mDiamondsList.size()==0){
                        mDiamondsList.add(p);
                        joinPokerList();
                    }
                    else{
                        int len = mDiamondsList.size();
                        for(int i=0;i<len;i++){
                            if(p.getmCardSize() > mDiamondsList.get(i).getmCardSize()){
                                mDiamondsList.add(i,p);
                                joinPokerList();
                                return;
                            }
                        }
                        mDiamondsList.add(p);
                        joinPokerList();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    //拿底牌
    public void takePoker(List<Poker> pokerList,int currentRound,int currentColor){
        this.SetRoundPoker(currentRound, currentColor);
        for(Poker p:pokerList){
            takePoker(p,currentRound);
        }
        this.sortPokerList(currentRound, roundPoker.getmColor());
    }

    //出牌
    public void playCard(List<Poker> pokerList){
        for(Poker p:pokerList){
            mUsedPokerStack.push(p);
        }
        mPokerList.removeAll(pokerList);
    }
    //出牌
//    public List<Poker> playCard(Dictionary<Integer, List<Poker>> curDeskPoker){
//        List<Poker> showPoker = new ArrayList<Poker>();
//        if(curDeskPoker != null && curDeskPoker.size()>0){
//            Boolean isFriendWin = true;
//            //判断上家出牌
//            //curDeskPoker.get(1)
//            //上家牌某门花色里面绝对大
//            //主牌里面绝对大
//        }
//        else{
//            //如果没有上家
//            //打对家缺的某一门花色
//            //副牌有Ace 先出Ace
//            //如果某门Ace已经出了，尝试打该门花色最大的牌
//            //主牌超过3对，吊主
//
//        }
//
//
//
//        mPokerList.removeAll(showPoker);
//        return showPoker;
//    }
    //抄底
//    public void getBottomPoke(List<Poker> pokerList){
//        mPokerList.addAll(pokerList);
//    }
    //埋底
//    public List<Poker> putBottomPoker(){
//        //埋掉底牌
//        return null;
//    }
    public String toString(){
        String str = "";
		/*for(Poker p:mPokerList){
			str += "["+p.toString()+"] ";
		}*/
        for(Poker p:mMainPokerList){
            str += "["+p.toString()+"] ";
        }
        str += "\n";
        for(Poker p:mSpadeList){
            str += "["+p.toString()+"] ";
        }
        str += "\n";
        for(Poker p:mHeartsList){
            str += "["+p.toString()+"] ";
        }
        str += "\n";
        for(Poker p:mClubList){
            str += "["+p.toString()+"] ";
        }
        str += "\n";
        for(Poker p:mDiamondsList){
            str += "["+p.toString()+"] ";
        }
        str = name+"'s card list:\n" + str;
        return str;
    }

    public List<Poker> getAllList(){
//        while(mPokerList.size()<25);
        return mPokerList;
    }

    public static Player GetPlayerBySeat(int seat){
        return null;
    }

    //事件相关操作
    //通知处理事件
    public void notifyPlayerEvent(PlayerEvent event){
        if(listener != null)
        {
            listener.onTaked(event);
        }
        else{
            Log.d("sj", name + " OnGetcardListener is null");
        }
    }
    //设置事件
    public void setPlayerEvent(OnPlayerTakedListener listener){
        this.listener = listener;
    }

}