package com.csslab.shengji.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/8.
 */
public class Rule {

    /**
     *  判断是否能叫牌  (新修改判断方法)
     * @param cardId 传入的牌列表
     * @param pokerNum 当前打的牌面数字
     * @param currentPokerStyle 当前打的牌花色类型， 0代表未叫牌，1,2,3,4代表一方块、一梅花、一红桃、
     *                          一黑桃叫牌，5,6,7,8代表两方块、两梅花、两红桃、两黑桃叫牌，9,10表示两小王、
     *                          两大王叫无主，11,12,13,14,15表示三王叫方块、梅花、红桃、黑桃、无主
     * @return
     */
    public static boolean canCallPoker(List<Poker> cardId, Integer pokerNum, Integer currentPokerStyle) {
        Integer jokeCount = 0;  // 王总数量
        Integer cJokeCount = 0;  // 大王数量
        Integer bJokeCount = 0;  // 小王数量
        Integer pokerNumCount = 0;  // 正主数量
        Integer diamondsCount = 0;  // 方块正主数量
        Integer clubCount = 0;  //  梅花正主数量
        Integer heartsCount = 0;  // 红桃正主数量
        Integer spadeCount = 0;  // 黑桃正主数量
        for(int i = 0; i < cardId.size(); i++) {
            Integer cardNum = cardId.get(i).getmCardSize();  // 获取牌面数字
            if(cardNum == 17) {
                bJokeCount++;
                jokeCount++;
            }
            if(cardNum == 18) {
                cJokeCount++;
                jokeCount++;
            }
            if(cardNum == pokerNum) {
                pokerNumCount++;
                switch (cardId.get(i).getmColor()) {
                    case DIAMONDS:
                        diamondsCount++;
                        break;
                    case CLUB:
                        clubCount++;
                        break;
                    case HEARTS:
                        heartsCount++;
                        break;
                    case SPADE:
                        spadeCount++;
                        break;
                    default:
                        break;
                }
            }
        }

        if(currentPokerStyle == 0) {
            if(pokerNumCount >= 1 && jokeCount >= 1) {
                return true;
            } else if (cJokeCount == 2 || bJokeCount == 2 || jokeCount >= 3) {
                return true;
            }
        }

        if(currentPokerStyle >= 1 && currentPokerStyle <= 4) {
            if((diamondsCount == 2 || clubCount == 2 || heartsCount == 2 || spadeCount == 2) && jokeCount > 0) {
                return true;
            } else if (cJokeCount == 2 || bJokeCount == 2 || jokeCount >= 3) {
                return true;
            }
        }

        if(currentPokerStyle == 5) {
            if((clubCount == 2 || heartsCount == 2 || spadeCount == 2) && jokeCount > 0) {
                return true;
            } else if (cJokeCount == 2 || bJokeCount == 2 || jokeCount >= 3) {
                return true;
            }
        }

        if(currentPokerStyle == 6) {
            if((heartsCount == 2 || spadeCount == 2) && jokeCount > 0) {
                return true;
            } else if (cJokeCount == 2 || bJokeCount == 2 || jokeCount >= 3) {
                return true;
            }
        }

        if(currentPokerStyle == 7) {
            if(spadeCount == 2 && jokeCount > 0) {
                return true;
            } else if (cJokeCount == 2 || bJokeCount == 2 || jokeCount >= 3) {
                return true;
            }
        }

        if(currentPokerStyle == 8) {
            if (cJokeCount == 2 || bJokeCount == 2 || jokeCount >= 3) {
                return true;
            }
        }

        if(currentPokerStyle == 9) {
            if (cJokeCount == 2) {
                return true;
            }
        }

        return false;
    }

    /**
     *  返回能叫什么牌List<Integer> 对应于currentPokerStyle  (新修改的方法)
     * @param cardId   传入的牌列表
     * @param pokerNum   当前打的牌面数字
     * @param currentPokerStyle   当前打的牌花色类型， 0代表未叫牌，1,2,3,4代表一方块、一梅花、一红桃、
     *                          一黑桃叫牌，5,6,7,8代表两方块、两梅花、两红桃、两黑桃叫牌，9,10表示两小王、
     *                          两大王叫无主，11,12,13,14,15表示三王叫方块、梅花、红桃、黑桃、无主
     * @return
     */
    public static List<Integer> getCallPokerStyle(List<Poker> cardId, Integer pokerNum, Integer currentPokerStyle) {
        Integer jokeCount = 0;  // 王总数量
        Integer cJokeCount = 0;  // 大王数量
        Integer bJokeCount = 0;  // 小王数量
        Integer pokerNumCount = 0;  // 正主数量
        Integer diamondsCount = 0;  // 方块正主数量
        Integer clubCount = 0;  //  梅花正主数量
        Integer heartsCount = 0;  // 红桃正主数量
        Integer spadeCount = 0;  // 黑桃正主数量
        for(int i = 0; i < cardId.size(); i++) {
            Integer cardNum = cardId.get(i).getmCardSize();  // 获取牌面数字
            if(cardNum == 17) {
                bJokeCount++;
                jokeCount++;
            }
            if(cardNum == 18) {
                cJokeCount++;
                jokeCount++;
            }
            if(cardNum == pokerNum) {
                pokerNumCount++;
                switch (cardId.get(i).getmColor()) {
                    case DIAMONDS:
                        diamondsCount++;
                        break;
                    case CLUB:
                        clubCount++;
                        break;
                    case HEARTS:
                        heartsCount++;
                        break;
                    case SPADE:
                        spadeCount++;
                        break;
                    default:
                        break;
                }
            }
        }

        List<Integer> callPokerStyle = new ArrayList<Integer>();

        // 未喊主情况
        if(currentPokerStyle == 0) {
            if(diamondsCount > 0 && jokeCount > 0) {
                callPokerStyle.add(1);
            }
            if(diamondsCount == 2 && jokeCount > 0) {
                callPokerStyle.add(5);
            }
            if(clubCount > 0 && jokeCount > 0) {
                callPokerStyle.add(2);
            }
            if(clubCount == 2 && jokeCount > 0) {
                callPokerStyle.add(6);
            }
            if(heartsCount > 0 && jokeCount > 0) {
                callPokerStyle.add(3);
            }
            if(heartsCount == 2 && jokeCount > 0) {
                callPokerStyle.add(7);
            }
            if(spadeCount > 0 && jokeCount > 0) {
                callPokerStyle.add(4);
            }
            if(spadeCount == 2 && jokeCount > 0) {
                callPokerStyle.add(8);
            }
            if(bJokeCount == 2) {
                callPokerStyle.add(9);
            }
            if(cJokeCount == 2) {
                callPokerStyle.add(10);
            }
            if(jokeCount >= 3) {
                callPokerStyle.add(11);
                callPokerStyle.add(12);
                callPokerStyle.add(13);
                callPokerStyle.add(14);
                callPokerStyle.add(15);
            }
        }
        // 一主牌喊主情况
        if(currentPokerStyle >= 1 && currentPokerStyle <= 4) {
            if(diamondsCount == 2 && jokeCount > 0) {
                callPokerStyle.add(5);
            }
            if(clubCount == 2 && jokeCount > 0) {
                callPokerStyle.add(6);
            }
            if(heartsCount == 2 && jokeCount > 0) {
                callPokerStyle.add(7);
            }
            if(spadeCount == 2 && jokeCount > 0) {
                callPokerStyle.add(8);
            }
            if(bJokeCount == 2) {
                callPokerStyle.add(9);
            }
            if(cJokeCount == 2) {
                callPokerStyle.add(10);
            }
            if(jokeCount >= 3) {
                callPokerStyle.add(11);
                callPokerStyle.add(12);
                callPokerStyle.add(13);
                callPokerStyle.add(14);
                callPokerStyle.add(15);
            }
        }
        // 两方块喊主
        if(currentPokerStyle == 5) {
            if(clubCount == 2 && jokeCount > 0) {
                callPokerStyle.add(6);
            }
            if(heartsCount == 2 && jokeCount > 0) {
                callPokerStyle.add(7);
            }
            if(spadeCount == 2 && jokeCount > 0) {
                callPokerStyle.add(8);
            }
            if(bJokeCount == 2) {
                callPokerStyle.add(9);
            }
            if(cJokeCount == 2) {
                callPokerStyle.add(10);
            }
            if(jokeCount >= 3) {
                callPokerStyle.add(11);
                callPokerStyle.add(12);
                callPokerStyle.add(13);
                callPokerStyle.add(14);
                callPokerStyle.add(15);
            }
        }
        // 两梅花喊主
        if(currentPokerStyle == 6) {
            if(heartsCount == 2 && jokeCount > 0) {
                callPokerStyle.add(7);
            }
            if(spadeCount == 2 && jokeCount > 0) {
                callPokerStyle.add(8);
            }
            if(bJokeCount == 2) {
                callPokerStyle.add(9);
            }
            if(cJokeCount == 2) {
                callPokerStyle.add(10);
            }
            if(jokeCount >= 3) {
                callPokerStyle.add(11);
                callPokerStyle.add(12);
                callPokerStyle.add(13);
                callPokerStyle.add(14);
                callPokerStyle.add(15);
            }
        }
        // 两红桃喊主
        if(currentPokerStyle == 7) {
            if(spadeCount == 2 && jokeCount > 0) {
                callPokerStyle.add(8);
            }
            if(bJokeCount == 2) {
                callPokerStyle.add(9);
            }
            if(cJokeCount == 2) {
                callPokerStyle.add(10);
            }
            if(jokeCount >= 3) {
                callPokerStyle.add(11);
                callPokerStyle.add(12);
                callPokerStyle.add(13);
                callPokerStyle.add(14);
                callPokerStyle.add(15);
            }
        }
        // 两黑桃喊主
        if(currentPokerStyle == 8) {
            if(bJokeCount == 2) {
                callPokerStyle.add(9);
            }
            if(cJokeCount == 2) {
                callPokerStyle.add(10);
            }
            if(jokeCount >= 3) {
                callPokerStyle.add(11);
                callPokerStyle.add(12);
                callPokerStyle.add(13);
                callPokerStyle.add(14);
                callPokerStyle.add(15);
            }
        }
        // 两小王喊主
        if(currentPokerStyle == 9) {
            if(cJokeCount == 2) {
                callPokerStyle.add(10);
            }
        }

        return callPokerStyle;
    }

    /**
     *   将能叫什么牌列表转换为显示的字符串数组
     * @param pokerStyleList  传入的能叫什么牌列表
     * @return
     */
    public static  String[] pokerStyleToString(List<Integer> pokerStyleList) {
        String[] styleStr = new String[pokerStyleList.size()];
        for(int i = 0; i < pokerStyleList.size(); i++) {
            switch (pokerStyleList.get(i)) {
                case 1 :
                    styleStr[i] = "方块";
                    break;
                case 2 :
                    styleStr[i] = "梅花";
                    break;
                case 3 :
                    styleStr[i] = "红桃";
                    break;
                case 4 :
                    styleStr[i] = "黑桃";
                    break;
                case 5 :
                    styleStr[i] = "方块（一对）";
                    break;
                case 6 :
                    styleStr[i] = "梅花（一对）";
                    break;
                case 7 :
                    styleStr[i] = "红桃（一对）";
                    break;
                case 8 :
                    styleStr[i] = "黑桃（一对）";
                    break;
                case 9 :
                    styleStr[i] = "无主（小王）";
                    break;
                case 10 :
                    styleStr[i] = "无主（大王）";
                    break;
                case 11 :
                    styleStr[i] = "方块（三王）";
                    break;
                case 12 :
                    styleStr[i] = "梅花（三王）";
                    break;
                case 13 :
                    styleStr[i] = "红桃（三王）";
                    break;
                case 14 :
                    styleStr[i] = "黑桃（三王）";
                    break;
                case 15 :
                    styleStr[i] = "无主（三王）";
                    break;
                default:
                    break;
            }
        }
        return styleStr;
    }

}
