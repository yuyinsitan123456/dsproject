package heartbeat;


public class Client extends Thread{
    
    @Override
    public void run() {
        try{
            while(true){
                ClientSender.getInstance().send();
                synchronized(Client.class){
//                    this.wait(5000);
                    Thread.sleep(2000);
                }
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    /**
     * 程序的入口main方法
     */
    public static void main(String[] args){
        Client client = new Client();
        client.start();
    }
}