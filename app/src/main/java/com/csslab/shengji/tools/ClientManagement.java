package com.csslab.shengji.tools;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.csslab.shengji.core.Poker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/7 0007.
 */
public class ClientManagement {
    private Handler mHandler = null;
    private Thread clientThread = null;
    private Thread rcvThread = null;
    private Socket clientSocket = null;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;
    private String srv_host;
    private int srv_port;

    public ClientManagement(String ip, int port, Handler h){
        srv_host = ip;
        srv_port = port;
        mHandler = h;
        rcvThread = new Thread(new clientWorker());
        rcvThread.start();
    }
    //用户设置昵称
    public void setPlayerUserName(String name){
        while(dos == null);
        sendMsg(MessageManagement.W_SET_NAME,name);
    }
    //用户喊牌（czc新增）
    public void setShoutPoker(Integer pokerItem) {
        sendMsg(MessageManagement.W_SHOUT, pokerItem + "");
    }
    //用户打牌
    public void playPoker(List<Poker> list){

    }
    //用户埋底
    public void putBottom(List<Poker> list){

    }

    private void sendMsg(int protocol,String data){
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("protocol",protocol);
            jsonObject.put("data",data);
            dos.writeUTF(jsonObject.toString());
            dos.flush();
        }
        catch (JSONException jex){
            Log.d("sj", "sendMsg "+jex.toString());
        }
        catch (IOException ex){
            Log.d("sj", ex.toString());
        }
    }

    public void stop(){

    }

    class clientWorker implements Runnable{
        @Override
        public void run() {
            try{
                if(clientSocket == null) {
                    //String ip = (serverIP & 0xff)+"."+(serverIP>>8 & 0xff)+"."+(serverIP>>16 & 0xff)+".1";
                    //String ip="10.0.2.2";
                    clientSocket = new Socket(srv_host,srv_port);
                    if(clientSocket.isClosed() == true && clientSocket.isConnected() == false){
                        Message msg = mHandler.obtainMessage();
                        msg.what = MessageManagement.O_ERROR;
                        msg.obj = "无法和游戏创建者连接上，请确认是否加入热点连接！";
                        mHandler.sendMessage(msg);
                    }
                    Log.d("sj", "client.isconnect" + clientSocket.isConnected());
                    Log.d("sj", "client.iscloseed" + clientSocket.isClosed());
                }
                ClientManagement.this.dis = new DataInputStream(clientSocket.getInputStream());
                ClientManagement.this.dos = new DataOutputStream(clientSocket.getOutputStream());
                rcvThread = new Thread(new ClientRcvWoker(ClientManagement.this.dis));
                rcvThread.start();
            }
            catch(UnknownHostException ex){ex.printStackTrace();}
            catch (IOException ex){ex.printStackTrace();}
        }
    }

    class ClientRcvWoker implements Runnable{
        DataInputStream dis;
        public ClientRcvWoker(DataInputStream is){
            this.dis = is;
        }
        @Override
        public void run() {
            //向服务端发送新用户消息
            while(true) {
                Boolean isServerCanRead = false;
                try {
                    String str = dis.readUTF();
                    if(str != null){
                        isServerCanRead = true;
                        Log.d("sj","run:"+str);
                        Message m = mHandler.obtainMessage();
                        try{
                            JSONObject jsonObject = new JSONObject(str);
                            m.what = jsonObject.getInt("protocol");
                            m.obj = jsonObject.getString("data");
                        }
                        catch (JSONException jex){
                            Log.d("sj", "run "+jex.toString());
                        }
                        mHandler.sendMessage(m);
                    }
                }
                catch (IOException ex) {
                    System.out.println("cannot read:"+ex.toString());
                    ex.printStackTrace();
                }
                try{
                    if(!isServerCanRead){
                        Thread.sleep(2000);
                    }
                }
                catch (InterruptedException ex){
                    System.out.println("Sleep error:"+ex.toString());
                    ex.printStackTrace();
                }
            }
        }
    }
}
