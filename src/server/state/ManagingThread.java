package server.state;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import server.config.Config;

public class ManagingThread extends Thread {
	private Socket socket;
	private BufferedReader in;
	private JSONParser parser = new JSONParser();
	private String identity;
	private boolean run = true;
	private MessageSendThread messageSendThread;

	public ManagingThread(Socket socket, MessageSendThread messageSendThread) {
		try {
			this.socket = socket;
			this.messageSendThread=messageSendThread;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			JSONObject e;
			this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
			while(run) {
				String message;
				if((message=this.in.readLine())!=null){
					e = (JSONObject)this.parser.parse(message);
					this.MessageReceive(e);
				}
			}
			this.messageSendThread.setRun(false);
		} catch (ParseException e) {
			this.messageSendThread.setRun(false);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(this.messageSendThread!=null)
					this.messageSendThread.setRun(false);
				if(ServerState.getInstance().getUserInfoMap().get(identity)!=null)
					quit(this.identity);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	//main logical part
	@SuppressWarnings({ "static-access" })
	public void MessageReceive(JSONObject message) throws IOException, ParseException {
		String type = (String)message.get("type");
		if(type.equals("newidentity")) {
			ServerState serverState=ServerState.getInstance();
			String identity = (String) message.get("identity");
			String pattern = "[A-Za-z]{1}[A-Za-z0-9]{2,15}";
			Pattern r = Pattern.compile(pattern);
			Matcher m = r.matcher(identity);
			if(m.matches()&&!serverState.getUserInfoMap().keySet().contains(identity)){
				Config config=serverState.getConfig();
				List<ServerInfo> serverInfoList=serverState.getServerInfoList();
				JSONObject lockIdR=new Message().getLockIdentityRequest(config.getServerid(), identity);
				sendCoorMessage(serverInfoList,lockIdR);
				boolean flag=true;
				while(true){
					Map<String,Map<String,String>> userInfoVoteSet=ServerState.getInstance().getUserInfoVoteSet();
					if(userInfoVoteSet.containsKey(identity)){
						Map<String,String> userInfoVotes=userInfoVoteSet.get(identity);
						if(userInfoVotes!=null&&userInfoVotes.size()==serverInfoList.size()){
							for(ServerInfo serverInfo:serverInfoList){
								flag=flag&&("true".equals(userInfoVotes.get(serverInfo.getServerid())));
							}
							break;
						}
					}
				}
				this.getMessageSendThread().getMessageQueue().add(new Message().getClientReply(String.valueOf(flag)));
				JSONObject leaseIdR=new Message().getReleasingIdentity(config.getServerid(), identity);
				sendCoorMessage(serverInfoList,leaseIdR);
				if(flag){
					String currentChatroom = "MainHall-"+config.getServerid();
					UserInfo userInfo = new UserInfo(identity,currentChatroom,socket,this);
					this.setIdentity(identity);
					Map<String,UserInfo> userInfoMap = serverState.getUserInfoMap();
					Map<String,LocalChatroomInfo> localChatroomInfoMap = serverState.getLocalChatroomInfoMap();
					LocalChatroomInfo localChatroomInfo=localChatroomInfoMap.get(currentChatroom);
					List<String> members = localChatroomInfo.getMembers();
					if(members!=null){
						for(String member : members) {
							userInfoMap.get(member).getManagingThread().getMessageSendThread().getMessageQueue().add(new Message().getChangeRoomReply(identity,"", currentChatroom));
						}
					}
					this.getMessageSendThread().getMessageQueue().add(new Message().getChangeRoomReply(this.identity,"", currentChatroom));
					ServerState.getInstance().newidentity(identity, userInfo, currentChatroom);
				}else{
					this.getMessageSendThread().getMessageQueue().add(new Message().getClientReply(String.valueOf(flag)));
					ServerState.getInstance().deleteUserInfoVoteSets(identity, serverInfoList);
				}
			}else{
				this.getMessageSendThread().getMessageQueue().add(new Message().getClientReply("false"));
			}
		} else if(type.equals("list")) {
			ServerState serverState=ServerState.getInstance();
			List<String> rooms=new ArrayList<String>();
			for(String room:serverState.getLocalChatroomInfoMap().keySet()){
				rooms.add(room);
			}
			for(String room:serverState.getRemoteChatroomInfoMap().keySet()){
				rooms.add(room);
			}
			this.getMessageSendThread().getMessageQueue().add(new Message().getList(rooms));
		} else if(type.equals("who")) {
			ServerState serverState=ServerState.getInstance();
			Map<String,UserInfo> userInfoMap = serverState.getUserInfoMap();
			UserInfo userInfo=userInfoMap.get(this.identity);
			String currentRoom=userInfo.getCurrentChatroom();
			LocalChatroomInfo localChatroomInfo=serverState.getLocalChatroomInfoMap().get(currentRoom);
			String owner=localChatroomInfo.getOwnerIdentity();
			List<String> peoples=localChatroomInfo.getMembers();
			this.getMessageSendThread().getMessageQueue().add(new Message().getWho(currentRoom, peoples, owner));
		} else if(type.equals("createroom")) {
			ServerState serverState=ServerState.getInstance();
			String roomid = (String) message.get("roomid");
			String pattern = "[A-Za-z]{1}[A-Za-z0-9]{2,15}";
			Pattern r = Pattern.compile(pattern);
			Matcher m = r.matcher(roomid);
			UserInfo userInfo=serverState.getUserInfoMap().get(this.identity);
			String currentRoomId=userInfo.getCurrentChatroom();
			if(m.matches()&&!this.identity.equals(serverState.getLocalChatroomInfoMap().get(currentRoomId).getOwnerIdentity())&&!serverState.getLocalChatroomInfoMap().containsKey(roomid)&&!serverState.getRemoteChatroomInfoMap().containsKey(roomid)){
				Config config=serverState.getConfig();
				List<ServerInfo> serverInfoList=serverState.getServerInfoList();
				JSONObject lockR=new Message().getLockRoomRequest(config.getServerid(), roomid);
				sendCoorMessage(serverInfoList,lockR);
				boolean flag=true;
				while(true){
					Map<String,Map<String,String>> chatroomVoteSet=ServerState.getInstance().getChatroomVoteSet();
					if(chatroomVoteSet.containsKey(roomid)){
						Map<String,String> chatroomVotes=chatroomVoteSet.get(roomid);
						if(chatroomVotes.size()==serverInfoList.size()){
							for(ServerInfo serverInfo:serverInfoList){
								flag=flag&&("true".equals(chatroomVotes.get(serverInfo.getServerid())));
							}
							break;
						}
					}
				}
				this.getMessageSendThread().getMessageQueue().add(new Message().getRoomCreateReply(roomid,String.valueOf(flag)));
				JSONObject leaseIdR=new Message().getReleaseRoomRespone(config.getServerid(),roomid, String.valueOf(flag));
				sendCoorMessage(serverInfoList,leaseIdR);
				if(flag){
					Map<String,UserInfo> userInfoMap = serverState.getUserInfoMap();
					Map<String,LocalChatroomInfo> localChatroomInfoMap = serverState.getLocalChatroomInfoMap();
					LocalChatroomInfo localChatroomInfo=localChatroomInfoMap.get(currentRoomId);
					List<String> members = localChatroomInfo.getMembers();
					for(String member : members) {
						userInfoMap.get(member).getManagingThread().getMessageSendThread().getMessageQueue().add(new Message().getChangeRoomReply(identity,currentRoomId, roomid));
					}
					ServerState.getInstance().createroom(roomid, currentRoomId, identity);
				}else{
					ServerState.getInstance().deleteChatroomVotes(roomid,serverInfoList);
					this.getMessageSendThread().getMessageQueue().add(new Message().getRoomCreateReply(roomid, String.valueOf(flag)));
				}
			}else{
				this.getMessageSendThread().getMessageQueue().add(new Message().getRoomCreateReply(roomid,"false"));
			}
		} else if(type.equals("join")) {
			ServerState serverState=ServerState.getInstance();
			String roomid = (String) message.get("roomid");
			UserInfo userInfo=serverState.getUserInfoMap().get(this.identity);
			String currentRoomId=userInfo.getCurrentChatroom();
			Map<String,LocalChatroomInfo> localChatroomInfoMap=serverState.getLocalChatroomInfoMap();
			if(!this.identity.equals(localChatroomInfoMap.get(currentRoomId).getOwnerIdentity())&&!roomid.equals(currentRoomId)){
				if(localChatroomInfoMap.keySet().contains(roomid)){
					Map<String,UserInfo> userInfoMap = serverState.getUserInfoMap();
					List<String> pmembers=localChatroomInfoMap.get(currentRoomId).getMembers();
					List<String> lmembers=localChatroomInfoMap.get(roomid).getMembers();
					for(String member:pmembers){
						userInfoMap.get(member).getManagingThread().getMessageSendThread().getMessageQueue().add(new Message().getChangeRoomReply(identity,currentRoomId, roomid));
					}
					for(String member:lmembers){
						userInfoMap.get(member).getManagingThread().getMessageSendThread().getMessageQueue().add(new Message().getChangeRoomReply(member,currentRoomId, roomid));
					}
					ServerState.getInstance().localjoin( roomid, currentRoomId, identity);
				}else{
					Map<String,RemoteChatroomInfo> remoteChatroomInfoMap=serverState.getRemoteChatroomInfoMap();
					if(remoteChatroomInfoMap.keySet().contains(roomid)){
						ServerInfo serverInfo=remoteChatroomInfoMap.get(roomid).getManagingServer();
						this.getMessageSendThread().getMessageQueue().add(new Message().getRouteReply(roomid, serverInfo.getServerAddress(),String.valueOf(serverInfo.getClientsPort()) ));
						Map<String,UserInfo> userInfoMap = serverState.getUserInfoMap();
						for(String member:localChatroomInfoMap.get(currentRoomId).getMembers()){
							userInfoMap.get(member).getManagingThread().getMessageSendThread().getMessageQueue().add(new Message().getChangeRoomReply(identity,currentRoomId, roomid));
						}
						ServerState.getInstance().remotejoin(currentRoomId, identity);
						this.run=false;
					}else{
						this.getMessageSendThread().getMessageQueue().add(new Message().getChangeRoomReply(identity,currentRoomId, currentRoomId));
					}
				}
			}else{
				this.getMessageSendThread().getMessageQueue().add(new Message().getChangeRoomReply(identity,currentRoomId, currentRoomId));
			}
		} else if(type.equals("movejoin")) {
			ServerState serverState=ServerState.getInstance();
			String former = (String) message.get("former");
			String roomid = (String) message.get("roomid");
			String identity = (String) message.get("identity");
			Config config=serverState.getConfig();
			Map<String,LocalChatroomInfo> localChatroomInfoMap = ServerState.getInstance().getLocalChatroomInfoMap();
			LocalChatroomInfo localChatroomInfo=localChatroomInfoMap.get(roomid);
			if(localChatroomInfo==null){
				roomid = "MainHall-"+config.getServerid();
				localChatroomInfo=localChatroomInfoMap.get(roomid);
			}
			UserInfo userInfo = new UserInfo(identity,roomid,socket,this);
			this.setIdentity(identity);
			Map<String,UserInfo> userInfoMap = serverState.getUserInfoMap();
			List<String> members = localChatroomInfo.getMembers();
			for(String member : members) {
				userInfoMap.get(member).getManagingThread().getMessageSendThread().getMessageQueue().add(new Message().getChangeRoomReply(identity,former, roomid));
			}
			ServerState.getInstance().movejoin(userInfo,roomid,identity);
			this.getMessageSendThread().getMessageQueue().add(new Message().getServerchange("true",config.getServerid()));
			this.getMessageSendThread().getMessageQueue().add(new Message().getChangeRoomReply(identity,former, roomid));
		} else if(type.equals("deleteroom")) {
			ServerState serverState=ServerState.getInstance();
			String roomid = (String) message.get("roomid");
			Map<String, UserInfo> userInfoMap=serverState.getUserInfoMap();
			Map<String, LocalChatroomInfo> localChatroomInfoMap=serverState.getLocalChatroomInfoMap();
			LocalChatroomInfo localChatroomInfo=localChatroomInfoMap.get(roomid);
			if(localChatroomInfo==null||!this.identity.equals(localChatroomInfo.getOwnerIdentity())){
				this.getMessageSendThread().getMessageQueue().add(new Message().getDeleteReply(roomid, "false"));
			}else{
				Config config=serverState.getConfig();
				String serverId=config.getServerid();
				List<ServerInfo> serverInfoList=serverState.getServerInfoList();
				JSONObject roomIdR=new Message().getDeleteRespone(serverId, roomid);
				sendCoorMessage(serverInfoList,roomIdR);
				List<String> members=localChatroomInfo.getMembers();
				for(String changeMember:members){
					for(String receiveMember:members){
						userInfoMap.get(receiveMember).getManagingThread().getMessageSendThread().getMessageQueue().add(new Message().getChangeRoomReply(changeMember,roomid, "MainHall-"+serverId));
					}
					for(String receiveMember:localChatroomInfoMap.get("MainHall-"+serverId).getMembers()){
						userInfoMap.get(receiveMember).getManagingThread().getMessageSendThread().getMessageQueue().add(new Message().getChangeRoomReply(changeMember,roomid, "MainHall-"+serverId));
					}
				}
				this.getMessageSendThread().getMessageQueue().add(new Message().getDeleteReply(roomid, "true"));
				ServerState.getInstance().deleteroom(roomid, members, "MainHall-"+serverId);
			}
		} else if(type.equals("message")) {
			ServerState serverState=ServerState.getInstance();
			String content = (String) message.get("content");
			Map<String, UserInfo> userInfoMap=serverState.getUserInfoMap();
			UserInfo userInfo=userInfoMap.get(this.identity);
			String currentRoomId=userInfo.getCurrentChatroom();
			Map<String,LocalChatroomInfo> localChatroomInfoMap=serverState.getLocalChatroomInfoMap();
			List<String> members=localChatroomInfoMap.get(currentRoomId).getMembers();
			for(String member:members){
				if(!member.equals(identity)){
					userInfoMap.get(member).getManagingThread().getMessageSendThread().getMessageQueue().add(new Message().getMessage(identity, content));
				}
			}
		} else if(type.equals("quit")) {
			this.run=false;
			quit(this.identity);
		}else{
			this.run=false;
			quit(this.identity);
		} 
	}

	public MessageSendThread getMessageSendThread() {
		return messageSendThread;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity=identity;
	}

	public void sendCoorMessage(List<ServerInfo> serverInfoList,JSONObject message) throws UnknownHostException, IOException{
		for(ServerInfo serverInfo:serverInfoList){
			Socket serverSocket = new Socket(serverInfo.getServerAddress(),serverInfo.getCoordinationPort());
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream(), "UTF-8"));
			writer.write(message + "\n");
			writer.flush();
			writer.close();
			serverSocket.close();
		}
	}

	@SuppressWarnings("static-access")
	public void quit(String identity) throws UnknownHostException, IOException{
		ServerState serverState=ServerState.getInstance();
		Map<String, UserInfo> userInfoMap=serverState.getUserInfoMap();
		UserInfo userInfo=userInfoMap.get(identity);
		String currentRoomId=userInfo.getCurrentChatroom();
		Map<String,LocalChatroomInfo> localChatroomInfoMap=serverState.getLocalChatroomInfoMap();
		if(!identity.equals(localChatroomInfoMap.get(currentRoomId).getOwnerIdentity())){
			for(String member:localChatroomInfoMap.get(currentRoomId).getMembers()){
				userInfoMap.get(member).getManagingThread().getMessageSendThread().getMessageQueue().add(new Message().getChangeRoomReply(identity, currentRoomId, ""));
			}
			ServerState.getInstance().quit(currentRoomId,identity);
		}else{
			Config config=serverState.getConfig();
			String serverId=config.getServerid();
			List<String> members=localChatroomInfoMap.get(currentRoomId).getMembers();
			List<ServerInfo> serverInfoList=serverState.getServerInfoList();
			JSONObject roomIdR=new Message().getDeleteRespone(serverId, currentRoomId);
			sendCoorMessage(serverInfoList,roomIdR);
			for(String changeMember:members){
				userInfoMap.get(changeMember).getManagingThread().getMessageSendThread().getMessageQueue().add(new Message().getChangeRoomReply(identity, currentRoomId, ""));
				if(!changeMember.equals(identity)){
					for(String receiveMember:members){
						userInfoMap.get(receiveMember).getManagingThread().getMessageSendThread().getMessageQueue().add(new Message().getChangeRoomReply(changeMember,currentRoomId, "MainHall-"+serverId));
					}
					for(String receiveMember:localChatroomInfoMap.get("MainHall-"+serverId).getMembers()){
						userInfoMap.get(receiveMember).getManagingThread().getMessageSendThread().getMessageQueue().add(new Message().getChangeRoomReply(changeMember,currentRoomId, "MainHall-"+serverId));
					}
				}
			}
			ServerState.getInstance().ownerquit(members,"MainHall-"+serverId,currentRoomId,identity);
		}
	}
}
