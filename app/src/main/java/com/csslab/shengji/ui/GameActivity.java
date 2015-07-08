package com.csslab.shengji.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.csslab.shengji.core.Player;
import com.csslab.shengji.core.Poker;
import com.csslab.shengji.core.PokerDesk;
import com.csslab.shengji.service.GameService;
import com.csslab.shengji.tools.ClientManagement;
import com.csslab.shengji.tools.MessageManagement;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends Activity {

    private List<Poker> mPokerList, sPokerList, remainPokerList;
    private Integer dw, dh, card_height, card_width, left_card_width1, left_card_width2;
    private ImageView[] imageViews, south_images, north_images, west_images, east_images;
    private boolean[] select_flag;
    private RelativeLayout main_poker_layout, east_pPoker_layout, south_pPoker_layout,
            west_pPoker_layout, north_pPoker_layout, center_pPoker_layout;
    private boolean isSouthEmpty, isCardEmpty, isPlaceCard, canShout;
    private TextView west_pName, east_pName, south_pName, north_pName, current_poker,
            current_score, statistics, remain_poker, text_tips;
    private LinearLayout btn_reselect, btn_send_poker, btn_place_poker, btn_look_poker,
            btn_shout_poker;
    private Integer currentRound, currentColor = 0;
    private Poker.PokerColor pokerColor = null;
    private List<Poker.PokerColor> pokerColorList = new ArrayList<Poker.PokerColor>();
    private String mPlayerName;

    private String sIP = "";
    private final int SERVER_PORT = 8191;
    private ClientManagement client = null;

    private GameService.GameBinder gameBinder = null;
    private ServiceConnection srv_conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            gameBinder = (GameService.GameBinder)service;
            gameBinder.createServer();
            sIP = "127.0.0.1";
            client = new ClientManagement(sIP,SERVER_PORT,mHandler);
            Log.d("sj", "--Service Connected--");
            client.setPlayerUserName(mPlayerName);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("sj","--Service disconnected--");
        }
    };

    /**
     * 继承Handle类实现主线程更新UI
     */
    private static class MyHandler extends Handler {

        private final WeakReference<GameActivity> mActivity;

        public MyHandler(GameActivity activity) {
            mActivity = new WeakReference<GameActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            GameActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case MessageManagement.R_GAME_TIPS:
                        activity.showTips((String)msg.obj);    //提示游戏创建状态、等待状态
                        break;
                    case MessageManagement.R_TAKEING:
                        activity.mPokerList = Poker.parseList(msg.obj.toString());
                        activity.setCard(activity.mPokerList);     //把摸到的牌显示出来
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private final MyHandler mHandler = new MyHandler(this);

    private final Runnable sRunnable = new Runnable() {
        @Override
        public void run() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        mPlayerName = intent.getStringExtra("playerName");
        if(intent.getBooleanExtra("gameHost",false)){
            Intent srv_intent = new Intent();
            srv_intent.setAction("com.csslab.shengji.service.GAME_SERVICE");
            boolean flag = bindService(srv_intent,srv_conn,BIND_AUTO_CREATE);
            if(flag == false){
                Toast.makeText(GameActivity.this, "错误：无法创建游戏！", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(GameActivity.this,"创建游戏成功，请打开热点等待其他玩家加入...",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            WifiInfo wInfo = ((WifiManager)getSystemService(WIFI_SERVICE)).getConnectionInfo();
            int serverIP =wInfo.getIpAddress();
            sIP = (serverIP & 0xff)+"."+(serverIP>>8 & 0xff)+"."+(serverIP>>16 & 0xff)+".1";
            //client = new ClientManagement(sIP,SERVER_PORT,mHandler);//真机
            client = new ClientManagement("10.0.2.2",8192,mHandler);//模拟器客户端测试专用
            client.setPlayerUserName(mPlayerName);
        }

        // 初始化所有布局和参数
        setAllLayout();

        // 初始化mPokerList
        mPokerList = new ArrayList<Poker>();

//        Poker p1 = new Poker(Poker.PokerColor.CLUB, 4, "a1_4");
//        Poker p2 = new Poker(Poker.PokerColor.HEARTS, 10, "a3_10");
//        Poker p3 = new Poker(Poker.PokerColor.SPADE, 7, "a4_7");
//        Poker p4 = new Poker(Poker.PokerColor.DIAMONDS, 13, "a2_13");
//        mPokerList.add(p1);
//        mPokerList.add(p2);
//        mPokerList.add(p3);
//        mPokerList.add(p4);
//
//        setCard(mPokerList);

    }

    /**
     *  提示连接状态及座位号
     * @param msg
     */
    private void showTips(String msg){
        Toast.makeText(GameActivity.this,msg,Toast.LENGTH_SHORT).show();
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

        // 打出的扑克牌的布局
        west_pPoker_layout = (RelativeLayout) findViewById(R.id.west_pPoker);
        north_pPoker_layout = (RelativeLayout) findViewById(R.id.north_pPoker);
        south_pPoker_layout = (RelativeLayout) findViewById(R.id.south_pPoker);
        east_pPoker_layout = (RelativeLayout) findViewById(R.id.east_pPoker);
        center_pPoker_layout = (RelativeLayout) findViewById(R.id.center_pPoker);

        RelativeLayout.LayoutParams wlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dh / 27 * 4);
        wlp.addRule(RelativeLayout.CENTER_VERTICAL);

        RelativeLayout.LayoutParams nlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dh / 27 * 4);
        nlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        RelativeLayout.LayoutParams slp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dh / 27 * 4);
        slp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        RelativeLayout.LayoutParams elp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dh / 27 * 4);
        elp.addRule(RelativeLayout.CENTER_VERTICAL);

        RelativeLayout.LayoutParams clp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dh / 27 * 4);
        clp.addRule(RelativeLayout.CENTER_IN_PARENT);

        west_pPoker_layout.setLayoutParams(wlp);
        north_pPoker_layout.setLayoutParams(nlp);
        south_pPoker_layout.setLayoutParams(slp);
        east_pPoker_layout.setLayoutParams(elp);
        center_pPoker_layout.setLayoutParams(clp);

        // 初始化提示信息
        text_tips = (TextView) findViewById(R.id.text_tips);

        // 初始化重选按钮
        btn_reselect = (LinearLayout) findViewById(R.id.btn_reselect);
        btn_reselect.setLayoutParams(new LinearLayout.LayoutParams(dw / 10, ViewGroup.LayoutParams.MATCH_PARENT));
        btn_reselect.setX(dw / 20 * 5);
        btn_reselect.setVisibility(View.INVISIBLE);

        // 初始化打牌按钮
        btn_send_poker = (LinearLayout) findViewById(R.id.btn_send_poker);
        btn_send_poker.setLayoutParams(new LinearLayout.LayoutParams(dw / 10, ViewGroup.LayoutParams.MATCH_PARENT));
        btn_send_poker.setX(dw / 20 * 7);
        btn_send_poker.setVisibility(View.INVISIBLE);

        // 初始化埋牌按钮
        btn_place_poker = (LinearLayout) findViewById(R.id.btn_place_poker);
        btn_place_poker.setLayoutParams(new LinearLayout.LayoutParams(dw / 10, ViewGroup.LayoutParams.MATCH_PARENT));
        btn_place_poker.setX(dw / 20 * 3);
        btn_place_poker.setVisibility(View.GONE);

        // 初始化叫牌按钮
        btn_shout_poker = (LinearLayout) findViewById(R.id.btn_shout_poker);
        btn_shout_poker.setLayoutParams(new LinearLayout.LayoutParams(dw / 10, ViewGroup.LayoutParams.MATCH_PARENT));
        btn_shout_poker.setX(dw / 20 * 3);
        btn_shout_poker.setVisibility(View.INVISIBLE);

        // 初始化查看底牌按钮
        btn_look_poker = (LinearLayout) findViewById(R.id.btn_look_poker);
        btn_look_poker.setLayoutParams(new LinearLayout.LayoutParams(dw / 10, ViewGroup.LayoutParams.MATCH_PARENT));
        btn_look_poker.setX(dw / 20 * 9);
        btn_look_poker.setVisibility(View.INVISIBLE);

        // 初始化玩家昵称
        west_pName = (TextView) findViewById(R.id.west_pName);
        east_pName = (TextView) findViewById(R.id.east_pName);
        north_pName = (TextView) findViewById(R.id.north_pName);
        south_pName = (TextView) findViewById(R.id.south_pName);

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

    /**
     * 设置本方扑克牌位置及牌面
     *
     * @param pokerList 传入的扑克牌列表
     */
    public void setCard(List<Poker> pokerList) {
        // 清空main_poker_layout中所有显示的扑克牌
        if (!isCardEmpty) {
            for (int i = 0; i < imageViews.length; i++) {
                main_poker_layout.removeView(imageViews[i]);
            }
        }
        // 获取传入的pokerList大小
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
     *  设置埋牌按钮
     */
    public void setPlacePokerBt() {
        if(mPokerList.size() > 25) {
            btn_place_poker.setVisibility(View.VISIBLE);
            btn_place_poker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击埋牌按钮的处理代码
                }
            });
        } else {
            btn_place_poker.setVisibility(View.GONE);
        }
    }

    /**
     *  处理叫牌按钮
     */
    public void setShoutPokerBt() {
        if(mPokerList.size() > 25) {
            btn_shout_poker.setVisibility(View.GONE);
        } else {
            if(canShout) {
                btn_shout_poker.setVisibility(View.VISIBLE);
                btn_shout_poker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 点击叫牌按钮的处理代码
                        Toast.makeText(GameActivity.this, "叫牌", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                btn_shout_poker.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     *  设置重选按钮
     */
    public void setReselectBt() {

    }

    /**
     *  设置出牌按钮
     */
    public void setSendPokerBt() {

    }

    /**
     *  设置看底牌按钮
     */
    public void setLookPokerBt() {

    }

}
