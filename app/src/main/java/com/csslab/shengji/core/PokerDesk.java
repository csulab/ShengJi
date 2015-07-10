package com.csslab.shengji.core;


import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

/**
 * Created by Vadin on 2015/6/29.
 */
public class PokerDesk {
    //事件相关

    //PokerDeskStatus桌面状态
    public enum DeskStatus{TAKEING,TAKED,TAKE_BOTTOME,PUT_BOTTOM,REVOLUTION}//拿牌，拿完，拿底，扣底，返牌
    private Stack<Poker> mPokerStack = new Stack<Poker>();//桌上的牌
    private List<Poker> shoutPoker = new ArrayList<Poker>();//当前叫牌
    private Player[] players = new Player[5];
    private int boss = 1;//本轮先手，从谁打起
    private int score = 0;//当前得分
    private int currentRound = 3;
    private int currentColor = 5;//默认无花色，无主,黑,红,梅,方543210
    private int currentItem = 0;//当前叫牌选项，0代表未叫牌，1,2,3,4代表一方块、一梅花、一红桃、一黑桃叫牌，5,6,7,8代表两方块、两梅花、两红桃、两黑桃叫牌，9,10表示两小王、两大王叫无主，11,12,13,14,15表示三王叫方块、梅花、红桃、黑桃、无主
    private final int CARD_INTERVAL = 150;//每位玩家发牌时间间隔
    private Player shotPlayer = null;
    /*public PokerDesk(){

    }*/
    public PokerDesk(Set<Player> playerSet) throws Exception{
        if(playerSet.size()!=4){
            Log.d("sj", "PokerDesk: less than 4 players.");
        }
        else{
            Log.d("sj", "create PokerDesk.");
            int index = 1;
            Iterator<Player> iterator = playerSet.iterator();
            while(iterator.hasNext()){
                Player p =  iterator.next();
                if(p.isListenerEmpty()){
                    Log.d("sj", "PokerDesk player no listener:"+p.getName());
                    throw new Exception("PokerDesk player no listener:"+p.getName());
                }
                players[index++] = p;
            }
            shuffle();
            putPoker();
        }
    }
    public PokerDesk(Player player1,Player player2,Player player3,Player player4){
        player1.setSeat(1);
        player2.setSeat(2);
        player3.setSeat(3);
        player4.setSeat(4);
        players[1] = player1;
        players[2] = player2;
        players[3] = player3;
        players[4] = player4;
        shuffle();
        putPoker();
    }
    //生成两副牌
    private List<Poker> genPokerList(){
        List<Poker> initPokerList = new ArrayList<Poker>();
        for(int i=3;i<16;i++){//14 is ace,15 is two
            for(Poker.PokerColor color : Poker.PokerColor.values()){
                if(color== Poker.PokerColor.KING||color== Poker.PokerColor.JOKER) {
                    continue;
                }
                Poker p = new Poker(color, i);
                if(p.getmCardSize() == currentRound){
                    p.setValue(Poker.BOSS);
                }
                initPokerList.add(p);
                initPokerList.add(p);
                // 设置牌命名
                switch(p.getmColor()) {
                    case SPADE:
                        p.setmPokerImage("a4_" + i);
                        break;
                    case HEARTS:
                        p.setmPokerImage("a3_" + i);
                        break;
                    case CLUB:
                        p.setmPokerImage("a1_" + i);
                        break;
                    case DIAMONDS:
                        p.setmPokerImage("a2_" + i);
                        break;
                    default:
                        break;
                }
            }
        }
        //小王
        Poker joker = new Poker(Poker.PokerColor.JOKER, Poker.JOKER);
        joker.setmPokerImage("a5_16");
        initPokerList.add(joker);
        initPokerList.add(joker);
        //大王
        Poker king = new Poker(Poker.PokerColor.KING, Poker.KING);
        king.setmPokerImage("a5_17");
        initPokerList.add(king);
        initPokerList.add(king);
        return initPokerList;
    }
    //洗牌
    private void shuffle(){
        List<Poker> initPokerList = genPokerList();
        Random rnd = new Random();
        while(initPokerList.size() > 0){
            int cardIndex = rnd.nextInt(initPokerList.size());
            this.mPokerStack.push(initPokerList.get(cardIndex));
            initPokerList.remove(cardIndex);
        }

    }
    // 获取牌桌的牌
    public Stack<Poker> getmPokerStack() {
        return mPokerStack;
    }
    public List<Poker> getRemainPoker(){
        List<Poker> list = new ArrayList<>();
        for(Poker p:mPokerStack){
            list.add(p);
        }
        return list;
    }

    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public void setShotPlayer(Player shotPlayer) {
        this.shotPlayer = shotPlayer;
    }

    public Player getShotPlayer() {
        return shotPlayer;
    }

    //发牌
    private void putPoker(){
        Log.d("sj", "putPoker");

        new Thread(){
            public void run() {
                while(mPokerStack.size() > 8){
                    //System.out.println(mPokerStack.pop().toString());
                    //players[boss].takePoker(mPokerStack.pop(),currentRound);
                    players[1].takePoker(mPokerStack.pop(), currentRound);
                    //System.out.println(players[1].getName() + " take poker");
                    Log.d("sj", "players[1].getName()" +  "take poker");
                    //询问是否叫主
                    try {
                        Thread.sleep(CARD_INTERVAL);
                    } catch (InterruptedException e) {
                        System.out.println("desk error");
                        e.printStackTrace();
                    }
                    //players[(boss + 1) > 4 ? (boss + 1) % 4:(boss + 1)].takePoker(mPokerStack.pop(),currentRound);
                    players[2].takePoker(mPokerStack.pop(),currentRound);
                    //System.out.println(players[2].getName() + " take poker");
                    Log.d("sj", "players[2].getName()" +  "take poker");
                    //询问是否叫主
                    try {
                        Thread.sleep(CARD_INTERVAL);
                    } catch (InterruptedException e) {
                        System.out.println("desk error");
                        e.printStackTrace();
                    }
                    //players[(boss + 2) > 4 ? (boss + 2) % 4:(boss + 2)].takePoker(mPokerStack.pop(),currentRound);
                    players[3].takePoker(mPokerStack.pop(),currentRound);
                    //System.out.println(players[3].getName() + " take poker");
                    Log.d("sj", "players[3].getName()" +  "take poker");
                    //询问是否叫主
                    try {
                        Thread.sleep(CARD_INTERVAL);
                    } catch (InterruptedException e) {
                        System.out.println("desk error");
                        e.printStackTrace();
                    }
                    //players[(boss + 3) > 4 ? (boss + 3) % 4:(boss + 3)].takePoker(mPokerStack.pop(),currentRound);
                    players[4].takePoker(mPokerStack.pop(),currentRound);
                    //System.out.println(players[4].getName() + " take poker");
                    Log.d("sj", "players[4].getName()" +  "take poker");
                    //询问是否叫主
                    try {
                        Thread.sleep(CARD_INTERVAL);
                    } catch (InterruptedException e) {
                        System.out.println("desk error");
                        e.printStackTrace();
                    }
                    //System.out.println(a + " " + b + " " + c + " " + d);
                }
                System.out.println("#####发牌完毕，等待扣底#####");
            };
        }.start();

        //整理玩家手中的牌
		/*for(Player p:players){
			if(p != null){
				p.setSpeialCard(currentRound);
			}
		}*/


    }
    //显示当前桌面信息
    public String toString(){
        String str = "";

        str += "当前打"+Poker.getPoker(currentRound)+"\n";
        str += "桌面底牌有：";
        for(Poker p:mPokerStack){
            str += "["+p.toString()+"] ";
        }
        return str;
    }
    //牌桌状态
    public Map<String,Integer> getStatus(){
        Map<String,Integer> map = new HashMap<>();
        map.put("round",currentRound);
        map.put("item",currentItem);
        map.put("color",currentColor);
        return map;
    }
}
