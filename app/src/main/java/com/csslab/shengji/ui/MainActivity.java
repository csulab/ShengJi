package com.csslab.shengji.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.csslab.shengji.tools.ClientManagement;


public class MainActivity extends Activity {

    private String playerName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_setting = (Button) findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder settingNameDB = new AlertDialog.Builder(MainActivity.this);
                settingNameDB.setTitle("请输入昵称");
                final EditText editText = new EditText(MainActivity.this);
                editText.setText(playerName);
                settingNameDB.setView(editText);
                settingNameDB.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        playerName = editText.getText().toString();
                        Toast.makeText(MainActivity.this, playerName, Toast.LENGTH_SHORT).show();
                    }
                });
                settingNameDB.setNegativeButton("取消", null);
                settingNameDB.show();
            }
        });

        Button btn_test = (Button) findViewById(R.id.btn_test);
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });

        Button btn_start_game = (Button) findViewById(R.id.btn_start_game);
        btn_start_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle("提示");
                alertDialogBuilder.setMessage("创建游戏还是加入游戏？");
                alertDialogBuilder.setPositiveButton("创建", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this,GameActivity.class);
                        intent.putExtra("gameHost",true);
                        intent.putExtra("playerName", playerName);
                        startActivity(intent);
                    }
                });
                alertDialogBuilder.setNegativeButton("加入", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this,GameActivity.class);
                        intent.putExtra("gameHost",false);
                        intent.putExtra("playerName", playerName);
                        startActivity(intent);
                    }
                });
                alertDialogBuilder.show();

            }
        });

    }

}
