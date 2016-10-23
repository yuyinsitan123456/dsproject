package server;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
<<<<<<< HEAD
import java.net.Socket;
import java.net.UnknownHostException;
=======
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONObject;

import server.state.Message;

public class Heartbeat extends Thread {

<<<<<<< HEAD
	private String ssl="all";
	public  Heartbeat(String ssl){
		this.ssl=ssl;
=======

	public  Heartbeat(){

>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
	}

	@Override
	public void run() {
		while (true) {
			try {
<<<<<<< HEAD
				Thread.sleep(2000);
=======
				Thread.sleep(10000);
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}

			AuthorizeServerState.getInstance().changeworkstates();

			List<CurrentServerInfo> serverList=AuthorizeServerState.getInstance().getServerInfoList();
			for (CurrentServerInfo serverInfo : serverList) {
				String hostName = serverInfo.getServerAddress();
				int serverPort = serverInfo.getCoordinationPort();
				try{
					@SuppressWarnings("static-access")
					JSONObject mas=(JSONObject) new Message().getHeartbeat();
					sendCoorMessage(hostName,serverPort,mas);
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			try {
<<<<<<< HEAD
				Thread.sleep(2000);
=======
				Thread.sleep(10000);
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}

			Set<String> workserverids=new HashSet<String>();
			Set<String> noworkserverids=new HashSet<String>();

			for (CurrentServerInfo serverInfo : serverList) {
				if(!serverInfo.getWork()){
					noworkserverids.add(serverInfo.getServerid());
				}else{
					workserverids.add(serverInfo.getServerid());
				}
			}
			if(!noworkserverids.isEmpty()){
				AuthorizeServerState.getInstance().deleteServers(noworkserverids);
<<<<<<< HEAD
=======

>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
				for(String workserverid:workserverids){
					for (CurrentServerInfo serverInfo : serverList) {
						@SuppressWarnings("static-access")
						JSONObject mas=new Message().sendfailserver(noworkserverids);
						if(workserverid.equals(serverInfo.getServerid())){
							try {
								sendCoorMessage(serverInfo.getServerAddress(),serverInfo.getCoordinationPort(),mas);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

<<<<<<< HEAD
	public void sendCoorMessage(String address,int port,JSONObject message) throws IOException {
		SSLSocketFactory sslsocketfactory =null;
		Socket serverSocket=null;
		try {
			if("all".equals(ssl)){
				sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

				serverSocket = (SSLSocket) sslsocketfactory.createSocket(address,port);

			}else{
				serverSocket =new Socket(address,port);
			}
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream(), "UTF-8"));
			System.out.println("send to chatserver:"+message);
			writer.write(message + "\n");
			writer.flush();
			writer.close();
			serverSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(serverSocket!=null)serverSocket.close();
		}
=======
	public void sendCoorMessage(String address,int port,JSONObject message) throws IOException{
		SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket serverSocket = (SSLSocket) sslsocketfactory.createSocket(address,port);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream(), "UTF-8"));
		System.out.println("send to chatserver:"+message);
		writer.write(message + "\n");
		writer.flush();
		writer.close();
		serverSocket.close();
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
	}
}