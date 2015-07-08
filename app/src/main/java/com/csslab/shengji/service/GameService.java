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
import com.csslab.shengji.tools.ClientManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.jar.JarException;

public class GameService extends Service {
    private  int count = 0;
    private String msg ="init value:";
    private GameBinder gameBinder = new GameBinder();
    //business logic
    private PokerDesk pd = null;
    private Player[] players = new Player[5];
    //服务端
    private static final int SERVERPORT = 8191;
    private static final int CLIENTPORT = 8192;
    //private static final int CLIENTPORT = 8191;
    ServerSocket Server;
    private Thread listenerThread = null;//监听线程
    private Thread srvRcvThread = null;
    //ExecutorService mThreadPool = Executors.newFixedThreadPool(8); //线程池

    private Boolean isServer = false;
    private Map<Player,Socket> client_map = null;
    private int player_count = 0;

    public class GameBinder extends Binder {
        //服务端相关
        public void createServer(){
            if(!isServer){
                Log.d("sj","device is server.now create.");
                //clients = new Vector<Socket>();
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
                                Player p = new Player("player"+player_count);
                                p.setSeat(player_count);
                                client_map.put(p,s);
                                players[player_count] = p;
                                try{
                                    sendToPlayer(p,ClientManagement.GAME_START_TIPS,"欢迎你，"+p.getName()+"！分配的座位号是"+player_count);
                                    Thread.sleep(1000);
                                }
                                catch (InterruptedException ex){
                                    Log.d("sj", "run " + ex.toString());
                                }
                                if(player_count == 4){
                                    try{
                                        Thread.sleep(1000);
                                        Log.d("sj","start game right now.");
                                        sendToPlayer(ClientManagement.GAME_START_TIPS,"游戏即将开始！");
                                        beginGame();
                                        Thread.sleep(1000);
                                    }
                                    catch (InterruptedException ex){
                                        Log.d("sj", "run "+ex.toString());
                                    }
                                }
                                else{
                                    sendToPlayer(ClientManagement.GAME_START_TIPS,"等待其他"+(4-player_count)+"个玩家加入！");
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
        private void sendAll(String msg){
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
        //send to all player
        protected void sendToPlayer(int protocol,String data){
            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("protocol",protocol);
                jsonObject.put("data",data);
                sendAll(jsonObject.toString());
            }
            catch (JSONException jex){
                Log.d("sj", "sendToPlayer "+jex.toString());
            }
        }
        protected void sendToPlayer(Player player,int protocol,String data){
            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("protocol",protocol);
                jsonObject.put("data",data);
                sendSrvMsg(player,jsonObject.toString());
            }
            catch (JSONException jex){
                Log.d("sj", "sendToPlayer "+jex.toString());
            }
        }
        private void sendSrvMsg(Player player,String msg){
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

            for(final Map.Entry<Player,Socket> entry:client_map.entrySet()){
                entry.getKey().setPlayerEvent(new OnPlayerTakedListener() {
                    @Override
                    public void onTaking(PlayerEvent event) {
                        String raw_data = ((Player)event.getSource()).getPokerListJsonString();
                        Log.d("listener", "onTaking ");
                        sendToPlayer(entry.getKey(),ClientManagement.TAKEING,raw_data);
                    }

                    @Override
                    public void onTaked(PlayerEvent event) {

                    }

                    @Override
                    public void onBottomTaked(PlayerEvent event) {

                    }

                    @Override
                    public void onBottomTaking(PlayerEvent event) {

                    }

                    @Override
                    public void onRevolution(Player event) {

                    }
                });
            }
            try{
                pd = new PokerDesk(client_map.keySet());
            }
            catch (Exception ex){
                Log.d("sj", "beginGame :"+ex.toString());
            }
            //pd = new PokerDesk(players[1],players[2],p3,p4);
        }
        public void stopServer(){
            if(client_map != null){
                if(!client_map.isEmpty()){
                    for(Map.Entry<Player,Socket> entry:client_map.entrySet()){
                        try{
                            entry.getValue().close();
                        }
                        catch (IOException ex){
                            Log.d("sj", "stopServer "+ex.toString());
                        }
                        finally {
                            //client_map.remove(entry.getKey());
                        }
                    }
                }
                if(!client_map.isEmpty()){
                    client_map.clear();
                }
                client_map = null;
            }
            if(srvRcvThread != null){
                srvRcvThread.interrupt();
            }
        }

    }
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("sj","Service is Binded");
        return gameBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
                    String str = dis.readUTF();
                    if(str != null){
                        Log.d("sj","server rcv:"+str);
                    }
                }
                catch (IOException ex){
                    Log.d("sj", "run "+ex.toString());
                }
            }
        }
    }
}
