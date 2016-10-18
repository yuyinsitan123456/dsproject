<<<<<<< HEAD
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
=======
import org.json.simple;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import javax.net.ssl;

public class Heartbeat extends Thread {

    private Server server;

    public  Heartbeat(Server server){
        this.server = server;
        try {
            server.start();
        } catch(Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(8000);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            JSONObject HBMessage = new JSONObject();
            HBMessage.put("type", "heartbeat");

            JSONParser parser = new JSONParser();

            ArrayList<ServerInfo> serverInfos = new ArrayList<>(Server.serverList);
            for (ServerInfo serverInfo : serverInfos) {
                String serverId = serverInfo.getServerId();
                if (!(serverId.equals("central")) && (!(serverId.equals(server.currentServerId)))){

                    String hostName = serverInfo.getServerAddress();
                    int serverPort = serverInfo.getServersPort();

                    sslSocket sslSocket = null;
                    try{
                        sslSocketFactory sslSocketfactory = (sslSocketFactory) sslSocketFactory.getDefault();
                        sslSocket = (sslSocket) sslSocketfactory.createSocket(hostName, serverPort);

                        DataInputStream input = new DataInputStream(sslSocket.getInputStream());
                        DataOutputStream output =new DataOutputStream(sslSocket.getOutputStream());

                        output.write((HBMessage.toJSONString() + "\n").getBytes("UTF-8"));
                        output.flush();

                        sslSocket.setSoTimeout(8000);
                        JSONObject message;
                        message = (JSONObject) parser.parse(input.readLine());
                        Boolean isWorking = Boolean.valueOf(String.valueOf(message.get("working")));

                        if(!isWorking){
                            server.deleteServer(serverId);
                        }

                    } catch (Exception e) {
                        server.deleteServer(serverId);
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
}
>>>>>>> 868c35ff475fc4f99e289b5a98f28d9317d0281c
