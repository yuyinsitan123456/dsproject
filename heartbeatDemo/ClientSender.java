package heartbeat;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

 public class ClientSender{
        private ClientSender(){}
        Socket sender = null;
        private static  ClientSender  instance;
        public static ClientSender getInstance(){
        	synchronized(Client.class){
        	if(instance==null){
                    instance = new ClientSender();
                }
            }
            return instance;
        }
        
        public void send(){
            try{
                sender = new Socket(InetAddress.getLocalHost(),25535);
                while(true){
                    ObjectOutputStream out = new ObjectOutputStream(sender.getOutputStream());
                    Entity obj = new Entity();
                    obj.setName("zhangjiawei");
                    obj.setSex("男");
                    out.writeObject(obj);
                    out.flush();
                    Thread.sleep(5000);
                }
            }catch(Exception e){
                
            }
        }
    }