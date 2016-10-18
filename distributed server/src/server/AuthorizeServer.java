package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import server.state.Message;

public class AuthorizeServer {

	public static void main(String[] args) throws IOException {

		int serversPort = 4441;
		BufferedReader in;
		BufferedWriter out;
		JSONParser parser = new JSONParser();
		// Read configuration in the config file
		SSLServerSocket listeningServerSocket = null;
		//Specify the keystore details (this can be specified as VM arguments as well)
		//the keystore file contains an application's own certificate and private key
		//keytool -genkey -keystore <keystorename> -keyalg RSA
		System.setProperty("javax.net.ssl.keyStore","\\DS.jks");
		System.setProperty("javax.net.ssl.trustStore","\\DS.jks");
		//Password to access the private key from the keystore file
		System.setProperty("javax.net.ssl.keyStorePassword","888888");

		// Enable debugging to view the handshake and communication which happens between the SSLClient and the SSLServer
		System.setProperty("javax.net.debug","none");

		try {
			// Create a server socket listening on given port
			//Create SSL server socket
			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();

			listeningServerSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(serversPort);
			SSLSocket serverSocket =null;
			while (true) {

				//Accept an incoming client connection request
				//Accept client connection
				serverSocket = (SSLSocket) listeningServerSocket.accept();
				System.out.println(Thread.currentThread().getName() 
						+ " - server conection accepted");
				in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream(), "UTF-8"));
				out = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream(), "UTF-8"));
				MessageReceive(out,(JSONObject)parser.parse(in.readLine()));
				out.close();
				in.close();
				serverSocket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(listeningServerSocket != null) {
				try {
					listeningServerSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("static-access")
	public static void MessageReceive(BufferedWriter out,JSONObject message) throws IOException {
		String type = (String)message.get("type");
		if(type.equals("serverList")) {
			String serverid = (String) message.get("serverid");
			String serversAddress=(String) message.get("serversAddress");
			int clientsPort =(int)(long) message.get("clientsPort");
			int coordinationPort = (int)(long) message.get("coordinationPort");
			AuthorizeServerState.getInstance().addServerInfo(new CurrentServerInfo(serverid, serversAddress, clientsPort, coordinationPort));
			List<CurrentServerInfo> serverList=AuthorizeServerState.getInstance().getServerInfoList();
			List<String> sendList = new ArrayList<String>();
			for(CurrentServerInfo server:serverList){
				String e= server.getServerid()+" "+server.getServerAddress()+" "+server.getClientsPort()+" "+server.getCoordinationPort();
				sendList.add(e);
			}
			JSONObject mas=(JSONObject) new Message().getServerList(sendList);
			sendCoorMessage(serversAddress,coordinationPort, mas);
		}else if(type.equals("backnumber")){
			String serverid = (String) message.get("serverid");
			Integer number = (Integer) message.get("number");
			AuthorizeServerState.getInstance().setUsernumber(serverid, number);
		}else if(type.equals("login")){
			String name = (String) message.get("name");
			String password = (String) message.get("password");
			AvailableUserInfo availableUserInfo = new AvailableUserInfo("a","a");
			if(name.equals(availableUserInfo.getName())&&password.equals(availableUserInfo.getPassword())){
				List<CurrentServerInfo> serverList=AuthorizeServerState.getInstance().getServerInfoList();
				JSONObject mas1=new Message().requireUserNumber();
				sendCoorMessage(serverList,mas1);
				String flagserverid=null;
				int flag=0;
				while(true){
					Map<String,Integer> usernumber=AuthorizeServerState.getInstance().getUsernumber();
					if(usernumber.size()==serverList.size()){
						for(String serverid:usernumber.keySet()){
							int number=usernumber.get(serverid).intValue();
							if(number>flag){
								flag=number;
								flagserverid=serverid;
							}
						}
						usernumber.clear();
						break;
					}
				}
				for(CurrentServerInfo serverInfo:serverList){
					if(flagserverid.equals(serverInfo.getServerid())){
						String serversAddress=(String)serverInfo.getServerAddress();
						int clientsPort = (int) serverInfo.getClientsPort();
						JSONObject mas2=new Message().getUserAuthorizeSuccess(flagserverid,serversAddress,clientsPort,"id");
						out.write((mas2.toJSONString() + "\n"));
						out.flush();
						break;
					}
				}
			}else{
				JSONObject mas=new Message().getUserAuthorizeFail();
				out.write((mas.toJSONString() + "\n"));
				out.flush();
			}
		}
	}

	public static void sendCoorMessage(String address,int port,JSONObject message) throws IOException{
		SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket serverSocket = (SSLSocket) sslsocketfactory.createSocket(address,port);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream(), "UTF-8"));
		writer.write(message + "\n");
		writer.flush();
		writer.close();
		serverSocket.close();
	}

	public static void sendCoorMessage(List<CurrentServerInfo> serverList,JSONObject message) throws IOException{
		for(CurrentServerInfo serverInfo:serverList){
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket serverSocket = (SSLSocket) sslsocketfactory.createSocket(serverInfo.getServerAddress(),serverInfo.getCoordinationPort());
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream(), "UTF-8"));
			writer.write(message + "\n");
			writer.flush();
			writer.close();
			serverSocket.close();
		}
	}
}
