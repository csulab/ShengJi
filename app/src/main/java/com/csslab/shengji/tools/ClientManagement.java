package com.csslab.shengji.tools;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2015/7/7 0007.
 */
public class ClientManagement {
    public final static int TAKEING = 1000;
    public final static int TAKEED = 999;
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

    class clientWorker implements Runnable{
        @Override
        public void run() {
            try{
                if(clientSocket == null) {
                    //String ip = (serverIP & 0xff)+"."+(serverIP>>8 & 0xff)+"."+(serverIP>>16 & 0xff)+".1";
                    //String ip="10.0.2.2";
                    clientSocket = new Socket(srv_host,srv_port);
                    Log.d("sj", "client.isconnect" + clientSocket.isConnected());
                    Log.d("sj", "client.iscloseed"+clientSocket.isClosed());
                }
                if(clientSocket.isConnected() == false && clientSocket.isClosed() == true){
                    Log.d("sj", "Server is closed!");
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
            while(true) {
                Boolean isServerCanRead = false;
                try {
                    String str = dis.readUTF();
                    if(str != null){
                        isServerCanRead = true;
                        Log.d("sj","rcvWoker:"+str);
                        Message msg = mHandler.obtainMessage();
                        msg.what = 0;
                        msg.obj = str;
                        mHandler.sendMessage(msg);
                        //测试：回发给服务端
                        try{
                            dos.writeUTF("client rcv:"+str);
                            dos.flush();
                        }
                        catch (IOException ex){
                            Log.d("sj", ex.toString());
                        }
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
