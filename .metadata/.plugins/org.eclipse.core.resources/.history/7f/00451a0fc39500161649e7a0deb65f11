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
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import server.config.Config;
import server.state.Message;
import server.state.ServerInfo;
import server.state.ServerState;

public class ServerListening  extends Thread  {
	private SSLServerSocket listeningServerSocket;
	private BufferedReader in;
	private BufferedWriter out;
	private JSONParser parser = new JSONParser();

	public ServerListening(SSLServerSocket serverSocket) {
		try {
			this.listeningServerSocket = serverSocket;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		SSLSocket socket=null;
		try {
			//keep listening other servers message
			while(true) {
				socket=(SSLSocket)listeningServerSocket.accept();
				System.out.println(Thread.currentThread().getName() 
						+ " - server conection accepted");
				this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
				MessageReceive((JSONObject)this.parser.parse(this.in.readLine()));
				this.out.close();
				this.in.close();
				socket.close();
			}
		} catch (ParseException e) {
			try {
				if(this.in!=null)this.in.close();
				if(socket!=null)socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(this.in!=null)
					this.in.close();
				if(this.out!=null)
					this.out.close();
				if(this.listeningServerSocket!=null)
					this.listeningServerSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	@SuppressWarnings({ "static-access" })
	public void MessageReceive(JSONObject message) throws IOException, ParseException {
		String type = (String)message.get("type");
		ServerState serverState=ServerState.getInstance();
		Config config=serverState.getConfig();
		if(type.equals("lockidentity")) {
			String lockidentity = (String) message.get("identity");
			String serverid = (String) message.get("serverid");
			String locked = (String) message.get("locked");
			Map<String,String> lockUsers=serverState.getLockedUserSet();
			List<ServerInfo> serverInfoList=serverState.getServerInfoList();
			if(locked==null){
				if(lockUsers.containsKey(lockidentity)||serverState.getUserInfoMap().containsKey(lockidentity)){
					JSONObject lockIdR=new Message().getLockIdentityResponse(config.getServerid(), lockidentity, "false");
					for(ServerInfo serverInfo:serverInfoList){
						if(serverid.equals(serverInfo.getServerid())){
							sendCoorMessage(serverInfo.getServerAddress(),serverInfo.getCoordinationPort(),lockIdR);
							break;
						}
					}
				}else{
					JSONObject lockIdR=new Message().getLockIdentityResponse(config.getServerid(), lockidentity, "true");
					ServerState.getInstance().addLockedUser(lockidentity, serverid);
					for(ServerInfo serverInfo:serverInfoList){
						if(serverid.equals(serverInfo.getServerid())){
							sendCoorMessage(serverInfo.getServerAddress(),serverInfo.getCoordinationPort(),lockIdR);
							break;
						}
					}
				}
			}else{
				ServerState.getInstance().addUserInfoVoteSet(lockidentity, serverid, locked);
			}
		} else if(type.equals("releaseidentity")) {
			String releaseidentity = (String) message.get("identity");
			String serverid = (String) message.get("serverid");
			Map<String,String> lockedUsers=ServerState.getInstance().getLockedUserSet();
			String lockServer=lockedUsers.get(releaseidentity);
			if(lockServer!=null&&lockServer.equals(serverid)){
				ServerState.getInstance().deleteLockedUser(releaseidentity);
			}
		} else if(type.equals("lockroomid")) {
			String lockroomid = (String) message.get("roomid");
			String serverid = (String) message.get("serverid");
			String locked = (String) message.get("locked");
			Map<String,String> lockRooms=serverState.getLockedChatroomSet();
			List<ServerInfo> serverInfoList=serverState.getServerInfoList();
			if(locked==null){
				if(lockRooms.containsKey(lockroomid)||serverState.getLocalChatroomInfoMap().containsKey(lockroomid)){
					JSONObject lockIdR=new Message().getLockRoomRespone(config.getServerid(), lockroomid, "false");
					for(ServerInfo serverInfo:serverInfoList){
						if(serverid.equals(serverInfo.getServerid())){
							sendCoorMessage(serverInfo.getServerAddress(),serverInfo.getCoordinationPort(),lockIdR);
							break;
						}
					}
				}else{
					JSONObject lockIdR=new Message().getLockRoomRespone(config.getServerid(), lockroomid, "true");
					ServerState.getInstance().addLockedChatroom(lockroomid, serverid);
					for(ServerInfo serverInfo:serverInfoList){
						if(serverid.equals(serverInfo.getServerid())){
							sendCoorMessage(serverInfo.getServerAddress(),serverInfo.getCoordinationPort(),lockIdR);
							break;
						}
					}
				}
			}else{
				ServerState.getInstance().addChatroomVote(lockroomid, serverid, locked);
			}
		} else if(type.equals("releaseroomid")) {
			String releaseroomid = (String) message.get("roomid");
			String serverid = (String) message.get("serverid");
			String approved = (String) message.get("approved");
			Map<String,String> lockedRooms=ServerState.getInstance().getLockedChatroomSet();
			String lockServer=lockedRooms.get(releaseroomid);
			if(lockServer!=null&&lockServer.equals(serverid)){
				ServerState.getInstance().deleteLockedChatroom(releaseroomid);
			}
			if("true".equals(approved)){
				ServerState.getInstance().addRemoteChatroomInfo(releaseroomid,serverid);
			}
		} else if(type.equals("deleteroom")){
			String roomid = (String) message.get("roomid");
			ServerState.getInstance().deleteRemoteChatroomInfo(roomid);
		} else if(type.equals("serverlist")){
			JSONArray serverlist = (JSONArray) message.get("servers");
			List<ServerInfo> serverInfoList=new ArrayList<ServerInfo>();
			for (int i = 0; i < serverlist.size(); i++) {
				String server = (String) serverlist.get(i);
				String[] serverinfo=server.split(" ");
				ServerInfo serverInfo = new ServerInfo(serverinfo[0],serverinfo[1],Integer.valueOf(serverinfo[2]),Integer.valueOf(serverinfo[3]));
				serverInfoList.add(serverInfo);
			}
			JSONObject mas=new Message().getHello(config.getServerid(),config.getServerAddress(),config.getClientsPort(),config.getCoordinationPort());
			sendCoorMessage(serverInfoList,mas);
			ServerState.getInstance().initServerList(serverlist);
		} else if(type.equals("helloServer")){
			String serverid = (String) message.get("serverid");
			String serversAddress=(String) message.get("serversAddress");
			int clientsPort = (int) message.get("clientsPort");
			int coordinationPort = (int) message.get("coordinationPort");
			ServerState.getInstance().addServerInfo(serverid,serversAddress,clientsPort,coordinationPort);
			JSONObject mas=new Message().getHelloagain(serverState.getLocalChatroomInfoMap().keySet(),serverid);
			sendCoorMessage(serversAddress,coordinationPort,mas);
		} else if(type.equals("helloagain")){
			String serverid = (String) message.get("serverid");
			JSONArray roomlist=(JSONArray) message.get("roomlist");
			ServerState.getInstance().addRemoteroom(serverid,roomlist);
		} else if(type.equals("usernumber")){
			JSONObject mas=new Message().getUsernumber(config.getServerid(),ServerState.getInstance().getUserInfoMap().keySet().size());
			MessageSend(mas);
		} else if(type.equals("heartbeat")){
			JSONObject heartbeat = new Message().getHeartbeat(true,config.getServerid());
			sendCoorMessage(ServerState.getInstance().getCentraladdress(),ServerState.getInstance().getCentralport(),heartbeat);
		} else if(type.equals("failserver")){
			JSONArray serverids = (JSONArray) message.get("serverids");
			ServerState.getInstance().deleteServerinfo(serverids);
		}
	}

	public void MessageSend(JSONObject msg) throws IOException {
		this.out.write(msg.toJSONString() + "\n");
		this.out.flush();
	}

	public void sendCoorMessage(String address,int port,JSONObject message) throws IOException{
		SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket serverSocket = (SSLSocket) sslsocketfactory.createSocket(address,port);
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream(), "UTF-8"));
		writer.write(message + "\n");
		writer.flush();
		writer.close();
		serverSocket.close();
	}
	
	public void sendCoorMessage(List<ServerInfo> serverInfoList,JSONObject message) throws IOException{
		for(ServerInfo serverInfo:serverInfoList){
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
