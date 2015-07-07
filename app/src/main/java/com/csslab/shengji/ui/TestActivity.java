package com.csslab.shengji.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.csslab.shengji.tools.ClientManagement;

import java.lang.ref.WeakReference;

public class TestActivity extends Activity {
    private Button btn_start = null;
    private TextView tv_test = null;
    private boolean isServer = false;
    private String sIP = "";
    private final int SERVER_PORT = 8191;
    private ClientManagement client = null;
    private Handler mHandler = null;
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

                        //isServer = true;
                        sIP = "127.0.0.1";
                        client = new ClientManagement(sIP,SERVER_PORT,mHandler);
                    }
                });
                alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WifiInfo wInfo = ((WifiManager)getSystemService(WIFI_SERVICE)).getConnectionInfo();
                        int serverIP =wInfo.getIpAddress();
                        sIP = (serverIP & 0xff)+"."+(serverIP>>8 & 0xff)+"."+(serverIP>>16 & 0xff)+".1";
                        client = new ClientManagement(sIP,SERVER_PORT,mHandler);
                    }
                });
                alertDialogBuilder.show();
            }
        });
    }

    private void createServer(){

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
                    case 0:
                        break;
                    default:
                        break;
                }
            }
        }
    }

}
