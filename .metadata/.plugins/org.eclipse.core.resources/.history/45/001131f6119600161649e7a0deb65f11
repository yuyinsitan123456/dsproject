package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import server.state.Message;

public class AuthorizeServer {

	public static void main(String[] args) throws IOException {

		int serversPort = 4441;
		BufferedReader in;
		DataOutputStream out;
		JSONParser parser = new JSONParser();
		// Read configuration in the config file
		SSLServerSocket listeningServerSocket = null;
		//Specify the keystore details (this can be specified as VM arguments as well)
		//the keystore file contains an application's own certificate and private key
		//keytool -genkey -keystore <keystorename> -keyalg RSA
		System.setProperty("javax.net.ssl.keyStore","DS.jks");
		System.setProperty("javax.net.ssl.trustStore","DS.jks");
		//Password to access the private key from the keystore file
		System.setProperty("javax.net.ssl.keyStorePassword","888888");
		System.setProperty("javax.net.ssl.trustStorePassword", "888888");

		// Enable debugging to view the handshake and communication which happens between the SSLClient and the SSLServer
		System.setProperty("javax.net.debug","none");

		try {
			// Create a server socket listening on given port
			//Create SSL server socket
			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();

			listeningServerSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(serversPort);

			SSLSocket serverSocket =null;

			new Heartbeat().start();

			while (true) {

				//Accept an incoming client connection request
				//Accept client connection
				serverSocket = (SSLSocket) listeningServerSocket.accept();
				System.out.println(Thread.currentThread().getName() 
						+ " - server conection accepted");
				in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream(), "UTF-8"));
				out = new DataOutputStream(serverSocket.getOutputStream());
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
	public static void MessageReceive(DataOutputStream out,JSONObject message) throws IOException, ParseException {
		System.out.println(message);
		String type = (String)message.get("type");
		if(type.equals("serverList")) {
			String serverid = (String) message.get("serverid");
			String serversAddress=(String) message.get("serversAddress");
			int clientsPort =(int)(long) message.get("clientsPort");
			int coordinationPort = (int)(long) message.get("coordinationPort");
			List<CurrentServerInfo> serverList=AuthorizeServerState.getInstance().getServerInfoList();
			List<String> sendList = new ArrayList<String>();
			for(CurrentServerInfo server:serverList){
				String e= server.getServerid()+" "+server.getServerAddress()+" "+server.getClientsPort()+" "+server.getCoordinationPort();
				sendList.add(e);
			}
			JSONObject mas=(JSONObject) new Message().getServerList(sendList);
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket serverSocket = (SSLSocket) sslsocketfactory.createSocket(serversAddress,coordinationPort);
			DataOutputStream writer = new DataOutputStream(serverSocket.getOutputStream());
			writer.write((mas + "\n").getBytes("UTF-8"));
			writer.flush();
			writer.close();
			serverSocket.close();
			AuthorizeServerState.getInstance().addServerInfo(new CurrentServerInfo(serverid, serversAddress, clientsPort, coordinationPort));
		}else if(type.equals("login")){
			String name = (String) message.get("username");
			String password = (String) message.get("password");
			AvailableUserInfo availableUserInfo = new AvailableUserInfo("a","a");
			if(name.equals(availableUserInfo.getName())&&password.equals(availableUserInfo.getPassword())){
				List<CurrentServerInfo> serverList=AuthorizeServerState.getInstance().getServerInfoList();
				JSONObject mas1=new Message().requireUserNumber();
				String flagserverid=null;
				if(serverList.size()!=0){
					SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
					SSLSocket serverSocket = (SSLSocket) sslsocketfactory.createSocket(serverList.get(0).getServerAddress(),serverList.get(0).getCoordinationPort());
					DataOutputStream writer =new DataOutputStream(serverSocket.getOutputStream());
					writer.write((mas1 + "\n").getBytes("UTF-8"));
					writer.flush();
					BufferedReader reader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream(), "UTF-8"));
					JSONParser parser = new JSONParser();
					JSONObject mas = (JSONObject) parser.parse(reader.readLine());
					writer.close();
					reader.close();
					serverSocket.close();
					int flag=(int)(long)mas.get("number");
					flagserverid=(String)mas.get("serverid");
					for(int i=1;i<serverList.size();i++){
						SSLSocketFactory sslsocketfactory1 = (SSLSocketFactory) SSLSocketFactory.getDefault();
						SSLSocket serverSocket1 = (SSLSocket) sslsocketfactory1.createSocket(serverList.get(i).getServerAddress(),serverList.get(i).getCoordinationPort());
						DataOutputStream writer1 =new DataOutputStream((serverSocket1.getOutputStream()));
						writer1.write((mas1 + "\n").getBytes("UTF-8"));
						writer1.flush();
						BufferedReader reader1 = new BufferedReader(new InputStreamReader(serverSocket1.getInputStream(), "UTF-8"));
						mas = (JSONObject) parser.parse(reader1.readLine());
						if((int)(long)mas.get("number")<flag){
							flag=(int)(long)mas.get("number");
							flagserverid=(String)mas.get("serverid");
						}
						writer1.close();
						reader1.close();
						serverSocket1.close();
					}
				}
				if(flagserverid!=null){
					for(CurrentServerInfo serverInfo:serverList){
						if(flagserverid.equals(serverInfo.getServerid())){
							String serversAddress=(String)serverInfo.getServerAddress();
							int clientsPort = (int) serverInfo.getClientsPort();
							JSONObject mas2=new Message().getUserAuthorizeSuccess(serversAddress,clientsPort,"id");
							out.write((mas2.toJSONString() + "\n").getBytes("UTF-8"));
							out.flush();
							break;
						}
					}
				}
			}else{
				JSONObject mas=new Message().getUserAuthorizeFail();
				out.write((mas.toJSONString() + "\n").getBytes("UTF-8"));
				out.flush();
			}
		}else if(type.equals("heartbeat")){
			String serverid = (String) message.get("serverid");
			boolean work = (boolean) message.get("working");
			AuthorizeServerState.getInstance().changeworkstate(serverid,work);
		}
	}

}
