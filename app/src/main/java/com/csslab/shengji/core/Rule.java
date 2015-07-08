package com.csslab.shengji.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/8.
 */
public class Rule {

    /**
     *  判断是否能叫牌
     * @param cardId 传入的牌列表
     * @param pokerNum 当前打的牌面数字
     * @param currentPokerStyle 当前打的牌花色类型， 0代表未叫牌，1代表一主叫牌，2,3,4,5代表两方块、
     *                          梅花、红桃、黑桃叫牌，6,7代表两小王、两大王叫牌，8代表三王叫牌
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

        if(currentPokerStyle == 1) {
            if((diamondsCount == 2 || clubCount == 2 || heartsCount == 2 || spadeCount == 2) && jokeCount > 0) {
                return true;
            } else if (cJokeCount == 2 || bJokeCount == 2 || jokeCount >= 3) {
                return true;
            }
        }

        if(currentPokerStyle == 2) {
            if((clubCount == 2 || heartsCount == 2 || spadeCount == 2) && jokeCount > 0) {
                return true;
            } else if (cJokeCount == 2 || bJokeCount == 2 || jokeCount >= 3) {
                return true;
            }
        }

        if(currentPokerStyle == 3) {
            if((heartsCount == 2 || spadeCount == 2) && jokeCount > 0) {
                return true;
            } else if (cJokeCount == 2 || bJokeCount == 2 || jokeCount >= 3) {
                return true;
            }
        }

        if(currentPokerStyle == 4) {
            if(spadeCount == 2 && jokeCount > 0) {
                return true;
            } else if (cJokeCount == 2 || bJokeCount == 2 || jokeCount >= 3) {
                return true;
            }
        }

        if(currentPokerStyle == 5) {
            if (cJokeCount == 2 || bJokeCount == 2 || jokeCount >= 3) {
                return true;
            }
        }

        if(currentPokerStyle == 6) {
            if (cJokeCount == 2) {
                return true;
            }
        }

        return false;
    }

    public static List<Poker.PokerColor> getCallPokerColor(List<Poker> cardId, Integer pokerNum, Integer currentPokerStyle) {
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

        List<Poker.PokerColor> callPokerColor = new ArrayList<Poker.PokerColor>();

        if(jokeCount >= 3) {
            callPokerColor.add(Poker.PokerColor.DIAMONDS);
            callPokerColor.add(Poker.PokerColor.CLUB);
            callPokerColor.add(Poker.PokerColor.HEARTS);
            callPokerColor.add(Poker.PokerColor.SPADE);
            callPokerColor.add(Poker.PokerColor.JOKER);
        } else {
            // 未喊主情况
            if(currentPokerStyle == 0) {
                if(diamondsCount > 0 && jokeCount > 0) {
                    callPokerColor.add(Poker.PokerColor.DIAMONDS);
                }
                if(clubCount > 0 && jokeCount > 0) {
                    callPokerColor.add(Poker.PokerColor.CLUB);
                }
                if(heartsCount > 0 && jokeCount > 0) {
                    callPokerColor.add(Poker.PokerColor.HEARTS);
                }
                if(spadeCount > 0 && jokeCount > 0) {
                    callPokerColor.add(Poker.PokerColor.SPADE);
                }
                if(cJokeCount == 2 || bJokeCount == 2) {
                    callPokerColor.add(Poker.PokerColor.JOKER);
                }
            }
            // 一主牌喊主情况
            if(currentPokerStyle == 1) {
                if(diamondsCount == 2 && jokeCount > 0) {
                    callPokerColor.add(Poker.PokerColor.DIAMONDS);
                }
                if(clubCount == 2 && jokeCount > 0) {
                    callPokerColor.add(Poker.PokerColor.CLUB);
                }
                if(heartsCount == 2 && jokeCount > 0) {
                    callPokerColor.add(Poker.PokerColor.HEARTS);
                }
                if(spadeCount == 2 && jokeCount > 0) {
                    callPokerColor.add(Poker.PokerColor.SPADE);
                }
                if(cJokeCount == 2 || bJokeCount == 2) {
                    callPokerColor.add(Poker.PokerColor.JOKER);
                }
            }
            // 两方块喊主
            if(currentPokerStyle == 2) {
                if(clubCount == 2 && jokeCount > 0) {
                    callPokerColor.add(Poker.PokerColor.CLUB);
                }
                if(heartsCount == 2 && jokeCount > 0) {
                    callPokerColor.add(Poker.PokerColor.HEARTS);
                }
                if(spadeCount == 2 && jokeCount > 0) {
                    callPokerColor.add(Poker.PokerColor.SPADE);
                }
                if(cJokeCount == 2 || bJokeCount == 2) {
                    callPokerColor.add(Poker.PokerColor.JOKER);
                }
            }
            // 两梅花喊主
            if(currentPokerStyle == 3) {
                if(heartsCount == 2 && jokeCount > 0) {
                    callPokerColor.add(Poker.PokerColor.HEARTS);
                }
                if(spadeCount == 2 && jokeCount > 0) {
                    callPokerColor.add(Poker.PokerColor.SPADE);
                }
                if(cJokeCount == 2 || bJokeCount == 2) {
                    callPokerColor.add(Poker.PokerColor.JOKER);
                }
            }
            // 两红桃喊主
            if(currentPokerStyle == 4) {
                if(spadeCount == 2 && jokeCount > 0) {
                    callPokerColor.add(Poker.PokerColor.SPADE);
                }
                if(cJokeCount == 2 || bJokeCount == 2) {
                    callPokerColor.add(Poker.PokerColor.JOKER);
                }
            }
            // 两黑桃喊主
            if(currentPokerStyle == 5) {
                if(cJokeCount == 2 || bJokeCount == 2) {
                    callPokerColor.add(Poker.PokerColor.JOKER);
                }
            }
            // 两小王喊主
            if(currentPokerStyle == 6) {
                if(cJokeCount == 2) {
                    callPokerColor.add(Poker.PokerColor.JOKER);
                }
            }
        }
        return callPokerColor;
    }

}
