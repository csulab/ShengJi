package com.csslab.shengji.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.csslab.shengji.core.Player;
import com.csslab.shengji.core.Poker;
import com.csslab.shengji.core.PokerDesk;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends Activity {

    private List<Poker> mPokerList, sPokerList, remainPokerList;
    private Integer dw, dh, card_height, card_width, left_card_width1, left_card_width2;
    private ImageView[] imageViews, south_images, north_images, west_images, east_images;
    private boolean[] select_flag;
    private RelativeLayout main_poker_layout, east_pPoker_layout, south_pPoker_layout,
            west_pPoker_layout, north_pPoker_layout;
    private boolean isSouthEmpty, isCardEmpty, isPlaceCard, canShout;
    private TextView west_player, east_player, south_player, north_player, current_poker,
            current_score, statistics, remain_poker;
    private LinearLayout reselect, send_poker, place_poker, look_poker, shout_poker;
    private Integer currentRound, currentColor = 0;
    private Poker.PokerColor pokerColor = null;
    private List<Poker.PokerColor> pokerColorList = new ArrayList<Poker.PokerColor>();
    private Player sPlayer;
    private PokerDesk pokerDesk;

    /**
     * 继承Handle类实现主线程更新UI
     */
    private static class MyHandle extends Handler {

        private final WeakReference<GameActivity> mActivity;

        public MyHandle(GameActivity activity) {
            mActivity = new WeakReference<GameActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            GameActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case 0:
                        if (msg.obj == null && msg.obj instanceof Poker) {
                            System.out.println("obj error");
                        } else {
                            activity.mPokerList = activity.sPlayer.getAllList();
                            try {
                                activity.setCard(activity.mPokerList);
                            } catch (Exception ex) {
                                System.out.println(ex.toString());
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private final MyHandle mHandle = new MyHandle(this);

    private final Runnable sRunnable = new Runnable() {
        @Override
        public void run() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // 初始化所有布局和参数
        setAllLayout();

        // 初始化mPokerList
        mPokerList = new ArrayList<Poker>();

        Poker p1 = new Poker(Poker.PokerColor.CLUB, 4, "a1_4");
        Poker p2 = new Poker(Poker.PokerColor.HEARTS, 10, "a3_10");
        Poker p3 = new Poker(Poker.PokerColor.SPADE, 7, "a4_7");
        Poker p4 = new Poker(Poker.PokerColor.DIAMONDS, 13, "a2_13");
        mPokerList.add(p1);
        mPokerList.add(p2);
        mPokerList.add(p3);
        mPokerList.add(p4);

        setCard(mPokerList);

    }

    /**
     * 重写返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("提示");
            alertDialogBuilder.setMessage("确定退出吗？");
            alertDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    GameActivity.this.finish();
                }
            });
            alertDialogBuilder.setNegativeButton("取消", null);
            alertDialogBuilder.show();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 设置本方扑克牌位置及牌面
     *
     * @param pokerList 传入的扑克牌列表
     */
    public void setCard(List<Poker> pokerList) {
        // 清空card_layout中所有显示的扑克牌
        if (!isCardEmpty) {
            for (int i = 0; i < imageViews.length; i++) {
                main_poker_layout.removeView(imageViews[i]);
            }
        }
        // 获取传入的cardList大小
        int size = pokerList.size();

        // 设置本方牌对应的图片容器，并初始化
        // 设置本方牌的选定状态，初始为false
        imageViews = new ImageView[size];
        select_flag = new boolean[size];
        for (int i = 0; i < size; i++) {
            imageViews[i] = new ImageView(this);
            // 指定每个imageView的宽和高
            imageViews[i].setLayoutParams(new ViewGroup.LayoutParams(card_width, card_height));
            // 分别设置正常牌数时及加上底牌时的显示
            if (size <= 25) {
                // 下面一行扑克牌位置设定
                if (i <= 12) {
                    imageViews[i].setY(card_height / 7 * 4);
                    imageViews[i].setX(left_card_width1 * (12 - i));
                }
                // 上面一行扑克牌位置设定
                if (i >= 13) {
                    imageViews[i].setY(card_height / 7);
                    imageViews[i].setX(left_card_width1 * (24 - i) + left_card_width1 / 2);
                }
            } else {
                // 下面一行扑克牌位置设定
                if (i <= 16) {
                    imageViews[i].setY(card_height / 7 * 4);
                    imageViews[i].setX(left_card_width2 * (16 - i));
                }
                // 上面一行扑克牌位置设定
                if (i >= 17) {
                    imageViews[i].setY(card_height / 7);
                    imageViews[i].setX(left_card_width2 * (32 - i) + left_card_width2 / 2);
                }
            }

            select_flag[i] = false;
        }

        // 设置扑克牌的图片，并装载到对应的imageView中
        for (int i = 0; i < size; i++) {
            // 通过名称获取图片id
            ApplicationInfo appInfo = getApplicationContext().getApplicationInfo();
            Integer imageId = getResources().getIdentifier(pokerList.get(i).getmPokerImage(), "drawable", appInfo.packageName);
            imageViews[i].setImageResource(imageId);
        }

        // 绑定每一个imageView的点击事件，并按倒序顺序添加到扑克牌布局中
        for (int i = size - 1; i >= 0; i--) {
            cardClick(imageViews[i], i);
            main_poker_layout.addView(imageViews[i]);
            isCardEmpty = false;
        }

        // 设置按钮是否显示
//        setPlaceCardButton();
//        setReselectButton();
//        setSendButton();
//        setLookCardButton();

        // 更新得分及余牌数量
//        setAllStatistics();
    }

    /**
     * 扑克牌点击事件的设置
     *
     * @param imageView 传入的扑克牌对应imageView
     * @param i         传入的扑克牌对应位置
     */
    public void cardClick(ImageView imageView, final int i) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardSelect(i);
            }
        });
    }

    /**
     * 处理扑克牌选定或取消选定的方法
     *
     * @param i 传入的处理扑克牌位置
     */
    public void cardSelect(int i) {
        if (!select_flag[i]) {
            imageViews[i].setY(imageViews[i].getY() - card_height / 7);
            select_flag[i] = true;
        } else {
            imageViews[i].setY(imageViews[i].getY() + card_height / 7);
            select_flag[i] = false;
        }
    }

    /**
     * 初始化所有布局及控件
     */
    public void setAllLayout() {
        // 获取屏幕宽度和高度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        dw = dm.widthPixels;
        dh = dm.heightPixels;

        // 计算扑克牌的宽度和高度像素，以及左边扑克牌露出的宽度像素
        card_height = dh * 2 / 9;
        card_width = card_height / 74 * 56;
        left_card_width1 = (dw * 5 / 6 - card_width) / 12;
        left_card_width2 = (dw * 5 / 6 - card_width) / 16;

        // 本方扑克牌摆放位置的布局
        main_poker_layout = (RelativeLayout) findViewById(R.id.main_poker_layout);

        west_pPoker_layout = (RelativeLayout) findViewById(R.id.west_pPoker);
        north_pPoker_layout = (RelativeLayout) findViewById(R.id.north_pPoker);
        south_pPoker_layout = (RelativeLayout) findViewById(R.id.south_pPoker);
        east_pPoker_layout = (RelativeLayout) findViewById(R.id.east_pPoker);

        RelativeLayout.LayoutParams wlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dh / 27 * 4);
        wlp.addRule(RelativeLayout.CENTER_VERTICAL);

        RelativeLayout.LayoutParams nlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dh / 27 * 4);
        nlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        RelativeLayout.LayoutParams slp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dh / 27 * 4);
        slp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        RelativeLayout.LayoutParams elp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dh / 27 * 4);
        elp.addRule(RelativeLayout.CENTER_VERTICAL);

        west_pPoker_layout.setLayoutParams(wlp);
        north_pPoker_layout.setLayoutParams(nlp);
        south_pPoker_layout.setLayoutParams(slp);
        east_pPoker_layout.setLayoutParams(elp);

        // 初始化重选按钮
        reselect = (LinearLayout) findViewById(R.id.btn_reselect);
        reselect.setLayoutParams(new LinearLayout.LayoutParams(dw / 10, ViewGroup.LayoutParams.MATCH_PARENT));
        reselect.setX(dw / 20 * 5);

        // 初始化打牌按钮
        send_poker = (LinearLayout) findViewById(R.id.btn_send_poker);
        send_poker.setLayoutParams(new LinearLayout.LayoutParams(dw / 10, ViewGroup.LayoutParams.MATCH_PARENT));
        send_poker.setX(dw / 20 * 7);

        // 初始化埋牌按钮
        place_poker = (LinearLayout) findViewById(R.id.btn_place_poker);
        place_poker.setLayoutParams(new LinearLayout.LayoutParams(dw / 10, ViewGroup.LayoutParams.MATCH_PARENT));
        place_poker.setX(dw / 20 * 3);

        // 初始化叫牌按钮
        shout_poker = (LinearLayout) findViewById(R.id.btn_shout_poker);
        shout_poker.setLayoutParams(new LinearLayout.LayoutParams(dw / 10, ViewGroup.LayoutParams.MATCH_PARENT));
        shout_poker.setX(dw / 20 * 3);

        // 初始化查看底牌按钮
        look_poker = (LinearLayout) findViewById(R.id.btn_look_poker);
        look_poker.setLayoutParams(new LinearLayout.LayoutParams(dw / 10, ViewGroup.LayoutParams.MATCH_PARENT));
        look_poker.setX(dw / 20 * 9);

        // 初始化玩家昵称
        west_player = (TextView) findViewById(R.id.west_pName);
        east_player = (TextView) findViewById(R.id.east_pName);
        north_player = (TextView) findViewById(R.id.north_pName);
        south_player = (TextView) findViewById(R.id.south_pName);

        // 初始化所有统计数据
        current_poker = (TextView) findViewById(R.id.current_poker);
        current_score = (TextView) findViewById(R.id.current_score);
        statistics = (TextView) findViewById(R.id.statistics);
        remain_poker = (TextView) findViewById(R.id.remain_poker);

        // 设置本方牌为空
        isSouthEmpty = true;
        isCardEmpty = true;

        // 设置为非埋牌状态
        isPlaceCard = false;

        // 初始化为可叫牌
        canShout = true;
    }

}
