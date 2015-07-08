package com.csslab.shengji.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.csslab.shengji.core.Poker;
import com.csslab.shengji.service.GameService;
import com.csslab.shengji.tools.ClientManagement;

import java.lang.ref.WeakReference;
import java.util.List;

public class TestActivity extends Activity {
    private Button btn_start = null;
    private TextView tv_test = null;

    private boolean isServer = false;
    private String sIP = "";
    private final int SERVER_PORT = 8191;
    private ClientManagement client = null;
    private Handler mHandler = null;

    private GameService.GameBinder gameBinder = null;
    private ServiceConnection srv_conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            gameBinder = (GameService.GameBinder)service;
            gameBinder.createServer();
            sIP = "127.0.0.1";
            client = new ClientManagement(sIP,SERVER_PORT,mHandler);
            Log.d("sj", "--Service Connected--");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("sj","--Service disconnected--");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mHandler = new UIHandler(this);
        btn_start = (Button)findViewById(R.id.btn_start_game);
        tv_test = (TextView)findViewById(R.id.tv_test);
        //注册服务

        //控件事件
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(TestActivity.this,"您是第一个进入游戏的，",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TestActivity.this);
                alertDialogBuilder.setTitle("提示");
                alertDialogBuilder.setMessage("创建游戏（是）还是加入游戏(否)？");
                alertDialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction("com.csslab.shengji.service.GAME_SERVICE");
                        boolean flag = bindService(intent,srv_conn,BIND_AUTO_CREATE);
                        if(flag == false){
                            Toast.makeText(TestActivity.this,"错误：无法创建游戏！",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(TestActivity.this,"创建游戏成功，请打开热点等待其他玩家加入...",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WifiInfo wInfo = ((WifiManager)getSystemService(WIFI_SERVICE)).getConnectionInfo();
                        int serverIP =wInfo.getIpAddress();
                        sIP = (serverIP & 0xff)+"."+(serverIP>>8 & 0xff)+"."+(serverIP>>16 & 0xff)+".1";
                        //client = new ClientManagement(sIP,8192,mHandler);
                        client = new ClientManagement("10.0.2.2",8192,mHandler);//模拟器客户端测试专用
                    }
                });
                alertDialogBuilder.show();
            }
        });
    }

    private void showTips(String msg){
        Toast.makeText(TestActivity.this,msg,Toast.LENGTH_SHORT).show();
    }
    private void showPoker(String poker){
        //tv_test.append(poker+" ");
        tv_test.setText(poker);
    }

    @Override
    protected void onDestroy() {
        gameBinder.stopServer();
        unbindService(srv_conn);
        super.onDestroy();
    }

    static class UIHandler extends Handler{
        private WeakReference<Activity> mActivity = null;
        public  UIHandler(Activity activity){
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if(mActivity.get() != null){
                switch (msg.what){
                    case ClientManagement.GAME_START_TIPS://显示原始消息
                        Log.d("sj", "Test Activity.handleMessage "+msg.obj.toString());
                        ((TestActivity)mActivity.get()).showTips((String) msg.obj);
                        break;
                    case ClientManagement.TAKEING:
                        Log.d("sj", "handleMessage "+msg.obj);
                        List<Poker> list = Poker.parseList(msg.obj.toString());
                        String cur_poker = "";
                        for(Poker p:list){
                            cur_poker += p.toString()+" ";
                        }
                        ((TestActivity) mActivity.get()).showPoker(cur_poker);
                        break;
                    default:
                        break;
                }
            }
        }
    }

}
