package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import server.state.Message;
import server.tools.ComLineValues;

public class AuthorizeServer {

	public static void main(String[] args) throws IOException {

		int serversPort = 4441;
		BufferedReader in;
		DataOutputStream out;
		String keyFilepath = null;
		SSLServerSocketFactory sslserversocketfactory =null;
		String ssl="all";
		//		String trustFilepath = null;

		ComLineValues comLineValues = new ComLineValues();
		CmdLineParser cparser = new CmdLineParser(comLineValues);

		try {
			cparser.parseArgument(args);
			keyFilepath = comLineValues.getKeyFilepath();
			serversPort = comLineValues.getServersPort();
			ssl=comLineValues.getSsl();
			//			trustFilepath = comLineValues.getTrustFilepath();
		} catch (CmdLineException ce) {
			ce.printStackTrace();
		}

		JSONParser parser = new JSONParser();
		// Read configuration in the config file
		ServerSocket listeningServerSocket = null;
		//Specify the keystore details (this can be specified as VM arguments as well)
		//the keystore file contains an application's own certificate and private key
		//keytool -genkey -keystore <keystorename> -keyalg RSA
		System.setProperty("javax.net.ssl.keyStore",keyFilepath);
		System.setProperty("javax.net.ssl.trustStore",keyFilepath);
		//Password to access the private key from the keystore file
		System.setProperty("javax.net.ssl.keyStorePassword","888888");
		System.setProperty("javax.net.ssl.trustStorePassword", "888888");

		// Enable debugging to view the handshake and communication which happens between the SSLClient and the SSLServer
		System.setProperty("javax.net.debug","none");

		try {
			// Create a server socket listening on given port
			// Create SSL server socket
			sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();
			if("all".equals(ssl)){
				listeningServerSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(serversPort);
			}else{
				listeningServerSocket=new ServerSocket(serversPort);
			}
			Socket serverSocket =null;

			new Heartbeat(ssl).start();

			while (true) {

				//Accept an incoming client connection request
				//Accept client connection
				serverSocket = listeningServerSocket.accept();
				//				System.out.println(Thread.currentThread().getName() 
				//						+ " - server conection accepted");
				in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream(), "UTF-8"));
				out = new DataOutputStream(serverSocket.getOutputStream());
				MessageReceive(out,(JSONObject)parser.parse(in.readLine()),ssl);
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
	public static void MessageReceive(DataOutputStream out,JSONObject message, String ssl) throws IOException, ParseException {
		System.out.println("Receive:"+message);
		String type = (String)message.get("type");
		if(type.equals("serverList")) {
			String serverid = (String) message.get("serverid");
			String serversAddress=(String) message.get("serversAddress");
			int clientsPort =Integer.parseInt((String) message.get("clientsPort"));
			int coordinationPort = Integer.parseInt((String) message.get("coordinationPort"));
			List<CurrentServerInfo> serverList=AuthorizeServerState.getInstance().getServerInfoList();
			List<String> sendList = new ArrayList<String>();
			for(CurrentServerInfo server:serverList){
				String e= server.getServerid()+" "+server.getServerAddress()+" "+server.getClientsPort()+" "+server.getCoordinationPort();
				sendList.add(e);
			}
			JSONObject mas=(JSONObject) new Message().getServerList(sendList);
			sendCoorMessage(serversAddress,coordinationPort,mas,ssl);
			AuthorizeServerState.getInstance().addServerInfo(new CurrentServerInfo(serverid, serversAddress, clientsPort, coordinationPort,0));
		}else if(type.equals("login")){
			String name = (String) message.get("username");
			String password = (String) message.get("password");
			AvailableUserInfo availableUserInfo=AuthorizeServerState.getInstance().getUserInfoMap().get(name);
			if(availableUserInfo!=null&&password.equals(availableUserInfo.getPassword())&&"".equals(availableUserInfo.getServer())){
				List<CurrentServerInfo> serverList=AuthorizeServerState.getInstance().getServerInfoList();
				//				JSONObject mas1=new Message().requireUserNumber();
				String flagserverid=getserverid(serverList);
				if(flagserverid!=null){
					for(CurrentServerInfo serverInfo:serverList){
						if(flagserverid.equals(serverInfo.getServerid())){
							String serversAddress=(String)serverInfo.getServerAddress();
							int clientsPort = (int) serverInfo.getClientsPort();
							JSONObject mas2=new Message().getUserAuthorizeSuccess(serversAddress,String.valueOf(clientsPort),"id");
							System.out.println("send to client:"+message);
							out.write((mas2.toJSONString() + "\n").getBytes("UTF-8"));
							out.flush();
							break;
						}
					}
				}
				availableUserInfo.setServer(flagserverid);
			}else{
				JSONObject mas=new Message().getUserAuthorizeFail();
				System.out.println("send to client:"+message);
				out.write((mas.toJSONString() + "\n").getBytes("UTF-8"));
				out.flush();
			}
		}else if(type.equals("heartbeat")){
			String serverid = (String) message.get("serverid");
			boolean work = Boolean.parseBoolean((String) message.get("working"));
			AuthorizeServerState.getInstance().changeworkstate(serverid,work);
		}else if(type.equals("change")){
			String serverid = (String) message.get("serverid");
			String identity = (String) message.get("identity");
			AuthorizeServerState.getInstance().changeuserstate(identity,serverid);
		}
	}

	public static void sendCoorMessage(String address,int port,JSONObject message,String ssl) throws IOException{
		SSLSocketFactory sslsocketfactory =null;
		Socket serverSocket=null;
		if("all".equals(ssl)){
			sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			serverSocket = (SSLSocket) sslsocketfactory.createSocket(address,port);
		}else{
			serverSocket =new Socket(address,port);
		}
		DataOutputStream writer =new DataOutputStream((serverSocket.getOutputStream()));
		System.out.println("send to otherserver:"+message);
		writer.write((message + "\n").getBytes("UTF-8"));
		writer.flush();
		writer.close();
		serverSocket.close();
	}

	public static String getserverid(List<CurrentServerInfo> serverList) throws IOException, ParseException{
		//		SSLSocketFactory sslsocketfactory =null;
		String flagserverid=null;
		//		if(serverList.size()!=0){
		//			Socket serverSocket = null;
		//			if("all".equals(ssl)){
		//				sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		//				serverSocket = (SSLSocket) sslsocketfactory.createSocket(serverList.get(0).getServerAddress(),serverList.get(0).getCoordinationPort());
		//			}else{
		//				serverSocket =new Socket(serverList.get(0).getServerAddress(),serverList.get(0).getCoordinationPort());
		//			}
		//			DataOutputStream writer =new DataOutputStream(serverSocket.getOutputStream());
		//			System.out.println("send to chatserver:"+message);
		//			writer.write((message + "\n").getBytes("UTF-8"));
		//			writer.flush();
		//			BufferedReader reader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream(), "UTF-8"));
		//			JSONParser parser = new JSONParser();
		//			JSONObject mas = (JSONObject) parser.parse(reader.readLine());
		//			System.out.println("chatserverReceive:"+mas);
		//			writer.close();
		//			reader.close();
		//			serverSocket.close();
		//			int flag=Integer.parseInt((String)mas.get("number"));
		//			flagserverid=(String)mas.get("serverid");
		//			for(int i=1;i<serverList.size();i++){
		//				Socket serverSocket1 =null;
		//				if("all".equals(ssl)){
		//					serverSocket1 = (SSLSocket) sslsocketfactory.createSocket(serverList.get(i).getServerAddress(),serverList.get(i).getCoordinationPort());
		//				}else{
		//					serverSocket1 =new Socket(serverList.get(i).getServerAddress(),serverList.get(i).getCoordinationPort());
		//				}
		//				DataOutputStream writer1 =new DataOutputStream((serverSocket1.getOutputStream()));
		//				System.out.println("send to chatserver:"+message);
		//				writer1.write((message + "\n").getBytes("UTF-8"));
		//				writer1.flush();
		//				BufferedReader reader1 = new BufferedReader(new InputStreamReader(serverSocket1.getInputStream(), "UTF-8"));
		//				mas = (JSONObject) parser.parse(reader1.readLine());
		//				System.out.println("chatserverReceive:"+mas);
		//				if(Integer.parseInt((String)mas.get("number"))<flag){
		//					flag=Integer.parseInt((String)mas.get("number"));
		//					flagserverid=(String)mas.get("serverid");
		//				}
		//				writer1.close();
		//				reader1.close();
		//				serverSocket1.close();
		//			}
		//		}
		if(serverList.size()!=0){
			for(AvailableUserInfo userInfo:AuthorizeServerState.getInstance().getUserInfoMap().values()){
				for(CurrentServerInfo serverInfo:serverList){
					if(serverInfo.getServerid().equals(userInfo.getServer())){
						serverInfo.addNumber();
						break;
					}
				}
			}
			flagserverid=serverList.get(0).getServerid();
			int flag=serverList.get(0).getNumber();
			for(int i=1;i<serverList.size();i++){
				if(serverList.get(i).getNumber()<flag){
					flag=serverList.get(i).getNumber();
					flagserverid=serverList.get(i).getServerid();
				}
			}
		}
		return flagserverid;
	}

}