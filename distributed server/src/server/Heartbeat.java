package server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Heartbeat extends Thread {


	public  Heartbeat(){

	}

	@SuppressWarnings("unchecked")
	public void run() {
		while (true) {
			try {
				Thread.sleep(8000);
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			List<CurrentServerInfo> serverList=AuthorizeServerState.getInstance().getServerInfoList();
			for (CurrentServerInfo serverInfo : serverList) {
				String serverId = serverInfo.getServerid();
				String hostName = serverInfo.getServerAddress();
				int serverPort = serverInfo.getCoordinationPort();
				SSLSocket sslSocket = null;
				try{
					SSLSocketFactory sslSocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
					sslSocket = (SSLSocket) sslSocketfactory.createSocket(hostName, serverPort);
					BufferedWriter output = new BufferedWriter(new OutputStreamWriter(sslSocket.getOutputStream(), "UTF-8"));
					JSONObject HBMessage = new JSONObject();
					HBMessage.put("type", "heartbeat");
					output.write((HBMessage.toJSONString() + "\n"));
					output.flush();
					sslSocket.setSoTimeout(8000);
					JSONParser parser = new JSONParser();
					BufferedReader input = new BufferedReader(new InputStreamReader(sslSocket.getInputStream(), "UTF-8"));
					JSONObject message = (JSONObject) parser.parse(input.readLine());
					Boolean isWorking = Boolean.valueOf(String.valueOf(message.get("working")));
					if(!isWorking){
						AuthorizeServerState.getInstance().deleteServer(serverId);
					}
					input.close();
					output.close();
					sslSocket.close();
				} catch (Exception e) {
					AuthorizeServerState.getInstance().deleteServer(serverId);
				} finally {
					if (sslSocket != null) {
						try {
							sslSocket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}