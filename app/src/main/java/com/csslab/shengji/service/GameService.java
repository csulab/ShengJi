package com.csslab.shengji.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.csslab.shengji.core.OnPlayerTakedListener;
import com.csslab.shengji.core.Player;
import com.csslab.shengji.core.PlayerEvent;
import com.csslab.shengji.core.PokerDesk;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameService extends Service {
    private  int count = 0;
    private String msg ="init value:";
    private InfoBinder infoBinder = new InfoBinder();
    //business logic
    private PokerDesk pd = null;
    private Player[] players = new Player[5];
    //客户端消息
    /*private Queue<String> sendQueue = null;
    private Queue<String> rcvQueue = null;
    private Socket clientSocket;
    private Thread clientSendThread = null;
    private Thread clientRcvThread = null;
    */
    //服务端
    private static final int SERVERPORT = 8191;
    private static final int CLIENTPORT = 8192;
    //private static final int CLIENTPORT = 8191;
    ServerSocket Server;
    private Thread listenerThread = null;//监听线程
    private Thread srvSendThread = null;
    private Thread srvRcvThread = null;
    ExecutorService mThreadPool = Executors.newFixedThreadPool(8); //线程池

    private Boolean isServer = false;
    private Queue<String> srvQueue = null;
    private Vector<Socket> clients = null;
    private Map<Player,Socket> client_map = null;
    private int player_count = 0;

    public class InfoBinder extends Binder {
        public String getMsg(){
            return msg;
        }
        public int getCount(){
            return count;
        }
        public int getMsgCount(){return 0;}

        public void sendMsg(String value){
            //sendQueue.offer(value);
        }
        public String readMsg(){
            return null;
        }
        //服务端相关
        public void createServer(){
            if(!isServer){
                Log.d("sj","device is server.now create.");
                clients = new Vector<Socket>();
                client_map = new HashMap<>();
                //服务端监听线程
                listenerThread = new Thread(){
                    @Override
                    public void run() {
                        while(true){
                            try{
                                if(Server == null){
                                    Server = new ServerSocket(SERVERPORT);
                                }
                                Socket s = Server.accept();
                                player_count++;
                                //clients.add(s);
                                //mThreadPool.execute(new ServerSender(s));
                                //mThreadPool.execute(new ServerReceiver(s));
                                Player p = new Player("player"+player_count);
                                p.setSeat(player_count);
                                client_map.put(p,s);
                                players[player_count] = p;
                                sendSrvMsg(p,"Welcome,"+p.getName()+",your seat is "+player_count);
                                try{
                                    Thread.sleep(1000);
                                }
                                catch (InterruptedException ex){
                                    Log.d("sj", "run " + ex.toString());
                                }
                                if(player_count == 4){
                                    try{
                                        Thread.sleep(1000);
                                    }
                                    catch (InterruptedException ex){
                                        Log.d("sj", "run "+ex.toString());
                                    }
                                    Log.d("sj","start game right now.");
                                    sendAll("start game right now.");
                                    try{
                                        Thread.sleep(1000);
                                    }
                                    catch (InterruptedException ex){
                                        Log.d("sj", "run "+ex.toString());
                                    }
                                    beginGame();
                                }
                                else{
                                    sendAll("waitting for other "+(4-player_count)+" player(s)");
                                    Log.d("sj", "waitting for other "+(4-player_count)+" players");
                                }
                                new Thread(new ServerReceiver(p)).start();
                            }
                            catch (IOException ex){
                                System.out.println("Server error:"+ex.toString());
                            }
                        }
                    }
                };
                listenerThread.start();
                isServer = true;
            }
        }
        protected void sendAll(String msg){
            for(Map.Entry<Player,Socket> entry:client_map.entrySet()){
                try{
                    DataOutputStream dos = new DataOutputStream(entry.getValue().getOutputStream());
                    dos.writeUTF(msg);
                    dos.flush();
                }
                catch (IOException ex){
                    Log.d("sj", "sendAll "+ex.toString());
                }
            }
        }
        protected void sendSrvMsg(Player player,String msg){
            try{
                DataOutputStream dos =new DataOutputStream(client_map.get(player).getOutputStream());
                dos.writeUTF(msg);
                dos.flush();
            }
            catch (IOException ex){
                Log.d("sj",ex.toString());
            }
        }
        protected void beginGame(){
            //Player p2 = new Player("p"+2);
            //Player p3 = new Player("p"+3);
            //Player p4 = new Player("p"+4);
            //players[2] = p2;
            //players[3] = p3;
            //players[4] = p4;

            for(Map.Entry<Player,Socket> entry:client_map.entrySet()){
                entry.getKey().setPlayerEvent(new OnPlayerTakedListener() {
                    @Override
                    public void onTaked(PlayerEvent event) {
                        try{
                            Log.d("sj", "onTaked "+event.getNewPoker().toString());
                            DataOutputStream dos =new DataOutputStream(client_map.get((Player)event.getSource()).getOutputStream());
                            dos.writeUTF(event.getNewPoker().toString());
                            dos.flush();
                        }
                        catch (IOException ex){
                            Log.d("sj",ex.toString());
                        }
                    }
                });
            }
            pd = new PokerDesk(client_map.keySet());
            //pd = new PokerDesk(players[1],players[2],p3,p4);
        }
        public void stopServer(){
            if(clients != null){
                if(clients.size() > 0){
                    for(Socket client:clients){
                        try
                        {
                            client.close();
                        }
                        catch (IOException ex){
                            ex.printStackTrace();
                        }
                        finally {
                            client = null;
                            clients.remove(client);
                        }
                    }
                }
            }
            clients = null;
            if(srvSendThread != null){
                srvSendThread.interrupt();
            }
            if(srvRcvThread != null){
                srvRcvThread.interrupt();
            }
        }

    }
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("sj","Service is Binded");
        return infoBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //客户端收发数据
        srvQueue = new LinkedList<String>();
        //srvQueue.offer("Server is Ok.");
        Log.d("sj", "Service is created");
    }

    class ServerReceiver implements  Runnable{
        private Socket socket;
        private Player player;
        private DataInputStream dis = null;
        public  ServerReceiver(Socket s){
            this.socket = s;
            try{
                dis = new DataInputStream(socket.getInputStream());
            }
            catch (IOException ex){
                Log.d("sj", "ServerReceiver "+ex.toString());
            }
        }
        public ServerReceiver(Player player){
            this.socket = client_map.get(player);
            try{
                dis = new DataInputStream(socket.getInputStream());
            }
            catch (IOException ex){
                Log.d("sj", "ServerReceiver "+ex.toString());
            }
        }
        @Override
        public void run() {
            Log.d("sj", "Server receive!");
            while(true){
                try{
                    //synchronized (this){
                    String str = dis.readUTF();
                    if(str != null){
                        //srvQueue.offer(str);
                        Log.d("sj","server rcv:"+str);
                    }
                    //}
                }
                catch (IOException ex){
                    //System.out.println("Server Rcv Error:"+ex.toString());
                }
            }
        }
    }

    class ServerSender implements Runnable{
        private Socket socket;
        public ServerSender(Socket s){
            this.socket = s;
        }
        @Override
        public void run() {
            System.out.println("Server send!");
            while(true){
                try{
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    synchronized (this){
                        if(!srvQueue.isEmpty()){
                            System.out.println("Server send msg");
                            dos.writeUTF(srvQueue.poll());
                            dos.flush();
                        }
                    }
                }
                catch (IOException ex){
                    //System.out.println("Server Sender Error:"+ex.toString());
                }
            }
        }
    }
}
