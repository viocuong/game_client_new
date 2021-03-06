/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
// Bug dị : khai bao ois trước oos lỗi :)
package client.controllers;
import Models.com.Pair;
import Models.com.Request;
import Models.com.User;
import java.net.Socket;
import java.sql.*;
import client.view.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
/**
 *
 * @author cuongnv
 */
public class Controller {
    private Socket socketClient;
    private String serverHost="localhost";
    private Connection con;
    private ObjectInputStream ois;
    //private ObjectOutputStream oos;
    private ObjectOutputStream oos;
    private int serverPort=8888;
    private LoginView loginView;
    private Game game;
    private Map<String, Pair<User,Integer>> listPlayer;
    private User myAccount;
    //private DatagramSocket clientUDP;
    public Controller(){
        
        loginView = new LoginView();
        loginView.setVisible(true);
        loginView.addListentBtnLogin(new listentBtnLogin());
        //openConnection(serverHost, serverPort);
        openConnection(serverHost, serverPort);
        new Receiving().start();
        //new updatePlayerOnline().start();
    }
    class Receiving extends Thread{
        public Receiving(){
            
        }
        @Override
        public void run(){
            
            try {
                while(true){
                    
                    Request respond = (Request)ois.readObject();
                    System.out.println(respond.getRequestName());
                    switch(respond.getRequestName()){
                        case "login":
                            handleLogin(respond);
                            break;
                        case "sendListPlayerOnline":
                            
                            handleListPlayerOnline(respond);
                            break;
                    }
                }
            } catch (IOException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public void getUserOnline(){
        Request req = new Request("getListPlayerOnline");
        System.out.println(req.getRequestName());
        send(req);
    }
    public void handleLogin(Request res){
        try{
        if(res.getObject() instanceof User){
            myAccount =(User) res.getObject();
            loginView.dispose();
            game = new Game();
            game.setActionListener(new ListentBtnPlayer());
            game.showMyAccount(myAccount);
            oos.reset();
            getUserOnline();
            game.showListPlayer(listPlayer);
            //game.addListentBtnPlayer(new ListentBtnPlayer());
        }
        else loginView.showMessage("Đăng nhập thất bại");
        }
        catch(Exception ex){
            
        }
    }
    public class listentBtnLogin implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent ae) {
           
            //openConnection(serverHost, serverPort);
            User user = loginView.getUser();
            
            Request req = new Request("login",(Object)user);
            send(req);
            //getUserOnline();
            //loginView.showMessage(s);
            //closeConnection();
        }
    }
    public class updatePlayerOnline extends Thread{
        public updatePlayerOnline(){
            //openConnection(serverHost, serverPort);
        }
        public void run(){
            
            while(true){
                try {
                    sleep(2000);
                    getUserOnline();
                    showListPlayer();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }
    }
    //xu ly su kien khi click vao nguoi choi muon thach dau
    public class ListentBtnPlayer implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent ae) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            JButton btn = (JButton) ae.getSource();
            System.out.println(btn.getText());
            //System.out.println("fkegk");
        }
    }
    public void handleListPlayerOnline(Request res){
        listPlayer =(Map<String, Pair<User,Integer>>) res.getObject();
        System.out.println(listPlayer.size());
        for(Map.Entry<String, Pair<User, Integer>> p : listPlayer.entrySet()){
            System.out.println(p.getKey()+" "+p.getValue().getKey().getUserName()+" "+p.getValue().getValue()+" "+p.getValue().getKey().getScore());
        }
        game.showListPlayer(listPlayer);    
    }
    public void showListPlayer(){ 
        game.showListPlayer(listPlayer);
    }
    public String receive(){
        String res=null;
        try {
            
            res = (String) ois.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }
    public void closeConnection(){
        try {
            System.out.println("close!");
            socketClient.close();
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void openConnection(String serverHost, int port){
        try {
            //System.out.println("helllllo");
            socketClient = new Socket(serverHost, port);
            this.oos = new ObjectOutputStream(socketClient.getOutputStream());
            this.ois = new ObjectInputStream(socketClient.getInputStream());
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void send(Request request){
        //openConnection(serverHost, serverPort);
        
        try {
            oos.writeObject(request);
            
            //oos = new ObjectOutputStream(socketClient.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void sendUDP(Request request){
        
    }
   
}
