package server.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;

import server.config.Config;

public class ServerState {
	//store different server state
	private static ServerState instance;
	private Config config;
	private String centraladdress;
	private int centralport;
	private List<ServerInfo> serverInfoList;
	private Map<String,UserInfo> userInfoMap;
	private Map<String,String> lockedUserSet;
	private Map<String,LocalChatroomInfo> localChatroomInfoMap;
	private Map<String,RemoteChatroomInfo> remoteChatroomInfoMap;
	private Map<String,String> lockedChatroomSet;
	private Map<String,Map<String,String>> chatroomVoteSet;
	private Map<String,Map<String,String>> userInfoVoteSet;

	private ServerState() {
		serverInfoList = new ArrayList<>();
		userInfoMap = new HashMap<String,UserInfo>();
		localChatroomInfoMap = new HashMap<String,LocalChatroomInfo>();
		remoteChatroomInfoMap = new HashMap<String,RemoteChatroomInfo>();
		lockedChatroomSet = new HashMap<String,String>();
		lockedUserSet = new HashMap<String,String>();
		chatroomVoteSet = new HashMap<String,Map<String,String>>();
		userInfoVoteSet = new HashMap<String,Map<String,String>>();
	}

	public static synchronized ServerState getInstance() {
		if(instance == null) {
			instance = new ServerState();
		}
		return instance;
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}
	
	public String getCentraladdress() {
		return centraladdress;
	}

	public void setCentraladdress(String centraladdress) {
		this.centraladdress = centraladdress;
	}
	
	public int getCentralport() {
		return centralport;
	}

	public void setCentralport(int centralport) {
		this.centralport = centralport;
	}

	public List<ServerInfo> getServerInfoList() {
		return serverInfoList;
	}

	public void setServerInfoList(List<ServerInfo> serverInfoList) {
		this.serverInfoList = serverInfoList;
	}

	public Map<String,UserInfo> getUserInfoMap() {
		return userInfoMap;
	}

	public void setUserInfoMap(Map<String,UserInfo> userInfoMap) {
		this.userInfoMap = userInfoMap;
	}

	public Map<String,String> getLockedUserSet() {
		return lockedUserSet;
	}

	public void setLockedUserSet(Map<String,String> lockedUserSet) {
		this.lockedUserSet = lockedUserSet;
	}

	public Map<String,LocalChatroomInfo> getLocalChatroomInfoMap() {
		return localChatroomInfoMap;
	}

	public void setLocalChatroomInfoMap(Map<String,LocalChatroomInfo> localChatroomInfoMap) {
		this.localChatroomInfoMap = localChatroomInfoMap;
	}

	public Map<String,RemoteChatroomInfo> getRemoteChatroomInfoMap() {
		return remoteChatroomInfoMap;
	}

	public void setRemoteChatroomInfoMap(Map<String,RemoteChatroomInfo> remoteChatroomInfoMap) {
		this.remoteChatroomInfoMap = remoteChatroomInfoMap;
	}

	public Map<String,String> getLockedChatroomSet() {
		return lockedChatroomSet;
	}

	public void setLockedChatroomSet(Map<String,String> lockedChatroomSet) {
		this.lockedChatroomSet = lockedChatroomSet;
	}

	public Map<String,Map<String,String>> getChatroomVoteSet() {
		return chatroomVoteSet;
	}

	public void setChatroomVoteSet(Map<String,Map<String,String>> chatroomVoteSet) {
		this.chatroomVoteSet = chatroomVoteSet;
	}

	public Map<String,Map<String,String>> getUserInfoVoteSet() {
		return userInfoVoteSet;
	}

	public void setUserInfoVoteSet(Map<String,Map<String,String>> userInfoVoteSet) {
		this.userInfoVoteSet = userInfoVoteSet;
	}

	public void addUser(UserInfo userInfo) {
		userInfoMap.put(userInfo.getIdentity(),userInfo);
	}

	public void deleteUser(String identity) {
		userInfoMap.remove(identity);
	}

	public void addRemoteChatroomInfo(RemoteChatroomInfo remoteChatroomInfo) {
		remoteChatroomInfoMap.put(remoteChatroomInfo.getChatroomId(),remoteChatroomInfo);
	}

	public void addRemoteChatroomInfo(String roomid,String serverid) {
		for(ServerInfo serverInfo:serverInfoList){
			if(serverid.equals(serverInfo.getServerid())){
				remoteChatroomInfoMap.put(roomid,new RemoteChatroomInfo(roomid,serverInfo));
				break;
			}
		}
	}

	public synchronized void deleteRemoteChatroomInfo(String chatroomId) {
		remoteChatroomInfoMap.remove(chatroomId);
	}

	public void addLocalChatroomInfo(LocalChatroomInfo localChatroomInfo) {
		localChatroomInfoMap.put(localChatroomInfo.getChatroomId(),localChatroomInfo);
	}

	public void deleteLocalChatroomInfo(String chatroomId) {
		localChatroomInfoMap.remove(chatroomId);
	}

	public synchronized void addLockedChatroom(String lockedChatroom,String serverid) {
		lockedChatroomSet.put(lockedChatroom,serverid);
	}

	public synchronized void deleteLockedChatroom(String lockedChatroom) {
		lockedChatroomSet.remove(lockedChatroom);
	}

	public synchronized void addLockedUser(String lockedUser,String serverid) {
		lockedUserSet.put(lockedUser,serverid);
	}

	public synchronized void deleteLockedUser(String lockedUser) {
		lockedUserSet.remove(lockedUser);
	}

	public synchronized void addChatroomVote(String lockid,String serverid,String vote) {
		Map<String,String> chatroomVoteMap=chatroomVoteSet.get(lockid);
		if(chatroomVoteMap!=null){
			if(!chatroomVoteMap.containsKey(serverid)){
				chatroomVoteMap.put(serverid, vote);
				chatroomVoteSet.replace(lockid, chatroomVoteMap);
			}
		}else{
			Map<String, String> voteMap=new HashMap<String, String>();
			voteMap.put(serverid, vote);
			chatroomVoteSet.put(lockid,voteMap);
		}
	}
	public void deleteChatroomVote(String lockid,String serverid) {
		Map<String,String> chatroomVoteMap=chatroomVoteSet.get(lockid);
		if(chatroomVoteMap!=null){
			if(chatroomVoteMap.containsKey(serverid)){
				chatroomVoteMap.remove(serverid);
				if(!chatroomVoteMap.isEmpty()){
					chatroomVoteSet.replace(lockid, chatroomVoteMap);
				}else{
					chatroomVoteSet.remove(lockid);
				}
			}
		}
	}
	public synchronized void deleteChatroomVotes(String lockid,List<ServerInfo> serverInfoList) {
		for(ServerInfo serverInfo:serverInfoList){
			deleteChatroomVote(lockid,serverInfo.getServerid());
		}
	}
	public synchronized void addUserInfoVoteSet(String lockidentity,String serverid,String vote) {
		Map<String,String> userInfoVoteMap=userInfoVoteSet.get(lockidentity);
		if(userInfoVoteMap!=null){
			if(!userInfoVoteMap.containsKey(serverid)){
				userInfoVoteMap.put(serverid, vote);
				userInfoVoteSet.replace(lockidentity, userInfoVoteMap);
			}
		}else{
			Map<String, String> voteMap=new HashMap<String, String>();
			voteMap.put(serverid, vote);
			userInfoVoteSet.put(lockidentity,voteMap);
		}
	}
	public void deleteUserInfoVoteSet(String lockidentity,String serverid) {
		Map<String,String> userInfoVoteMap=userInfoVoteSet.get(lockidentity);
		if(userInfoVoteMap!=null){
			if(userInfoVoteMap.containsKey(serverid)){
				userInfoVoteMap.remove(serverid);
				if(!userInfoVoteMap.isEmpty()){
					userInfoVoteSet.replace(lockidentity, userInfoVoteMap);
				}else{
					userInfoVoteSet.remove(lockidentity);
				}
			}
		}
	}
	public synchronized void deleteUserInfoVoteSets(String lockidentity,List<ServerInfo> serverInfoList) {
		for(ServerInfo serverInfo:serverInfoList){
			deleteUserInfoVoteSet(lockidentity,serverInfo.getServerid());
		}
	}

	public void addMember(String roomid,String identity) {
		LocalChatroomInfo localChatroomInfo=this.localChatroomInfoMap.get(roomid);
		localChatroomInfo.addMember(identity);
	}
	public synchronized void addMembers(String roomid,List<String> identitys) {
		for(String changeMember:identitys){
			addMember(roomid,changeMember);
		}
	}
	public void deleteMember(String roomid,String identity) {
		LocalChatroomInfo localChatroomInfo=this.localChatroomInfoMap.get(roomid);
		localChatroomInfo.deleteMember(identity);
	}
	public void setCurrentChatroom(String identity, String currentChatroom) {
		UserInfo userInfo=this.userInfoMap.get(identity);
		userInfo.setCurrentChatroom(currentChatroom);
	}
	public void setCurrentChatrooms(List<String> identitys, String currentChatroom) {
		for(String changeMember:identitys){
			setCurrentChatroom(changeMember,currentChatroom);
		}
	}
	public void deleteChatroom(String currentChatroom) {
		this.localChatroomInfoMap.remove(currentChatroom);
	}
	public synchronized void newidentity(String identity,UserInfo userInfo,String currentChatroom){
		for(ServerInfo serverInfo:serverInfoList){
			deleteUserInfoVoteSet(identity,serverInfo.getServerid());
		}
		addUser(userInfo);
		addMember(currentChatroom,identity);
	}
	public synchronized void createroom(String roomid,String currentRoomId,String identity){
		for(ServerInfo serverInfo:serverInfoList){
			deleteChatroomVote(roomid,serverInfo.getServerid());
		}
		addLocalChatroomInfo(new LocalChatroomInfo(roomid,identity));
		addMember(roomid,identity);
		deleteMember(currentRoomId, identity);
		setCurrentChatroom(identity,roomid);
	}
	public synchronized void localjoin(String roomid,String currentRoomId,String identity){
		deleteMember(currentRoomId,identity);
		addMember(roomid,identity);
		setCurrentChatroom(identity, roomid);
	}
	public synchronized void remotejoin(String currentRoomId,String identity){
		deleteUser(identity);
		deleteMember(currentRoomId,identity);
	}
	public synchronized void movejoin(UserInfo userInfo,String roomid,String identity){
		addUser(userInfo);
		addMember(roomid,identity);
	}
	public synchronized void deleteroom(String roomid,List<String> identitys,String currentRoomId){
		setCurrentChatrooms(identitys,currentRoomId);
		for(String changeMember:identitys){
			addMember(currentRoomId,changeMember);
		}
		deleteChatroom(roomid);
	}
	public synchronized void quit(String currentRoomId,String identity){
		deleteUser(identity);
		deleteMember(currentRoomId,identity);
	}
	public synchronized void ownerquit(List<String> members,String roomid,String currentRoomId,String identity){
		setCurrentChatrooms(members,roomid);
		for(String changeMember:members){
			addMember(roomid,changeMember);
		}
		deleteUser(identity);
		deleteChatroom(currentRoomId);
	}

	public synchronized void addServerList(JSONArray serverlist) {
		for (int i = 0; i < serverlist.size(); i++) {
			String server = (String) serverlist.get(i);
			String[] serverinfo=server.split(" ");
			ServerInfo serverInfo = new ServerInfo(serverinfo[0],serverinfo[1],Integer.valueOf(serverinfo[2]),Integer.valueOf(serverinfo[3]));
			serverInfoList.add(serverInfo);
		}
	}
	public synchronized void initServerList(JSONArray serverlist) {
		for (int i = 0; i < serverlist.size(); i++) {
			String server = (String) serverlist.get(i);
			String[] serverinfo=server.split(" ");
			ServerInfo serverInfo = new ServerInfo(serverinfo[0],serverinfo[1],Integer.valueOf(serverinfo[2]),Integer.valueOf(serverinfo[3]));
			serverInfoList.add(serverInfo);
		}
		localChatroomInfoMap.put("MainHall-"+config.getServerid(),new LocalChatroomInfo("MainHall-"+config.getServerid(),"") );
	}
	public synchronized void addServerInfo(String serverid, String serverAddress, int clientsPort, int coordinationPort) {
		ServerInfo serverInfo = new ServerInfo(serverid,serverAddress,clientsPort,coordinationPort);
		serverInfoList.add(serverInfo);
		remoteChatroomInfoMap.put("MainHall-"+serverid,new RemoteChatroomInfo("MainHall-"+serverid,serverInfo));
	}
	public synchronized void addRemoteroom(String serverid,JSONArray roomlist) {
		for (int i = 0; i < roomlist.size(); i++) {
			String room = (String) roomlist.get(i);
			ServerInfo currentserverInfo = null;
			for(ServerInfo serverInfo:serverInfoList){
				if(serverid.equals(serverInfo.getServerid())){
					currentserverInfo = serverInfo;
					break;
				}
			}
			remoteChatroomInfoMap.put(room,new RemoteChatroomInfo(room,currentserverInfo));
		}
	}
	
	public synchronized void deleteServerinfo(JSONArray serverids) {
<<<<<<< HEAD
		if(serverids==null){
			return;
		}
		for (int i = 0; i < serverids.size(); i++) {
			String serverid = (String) serverids.get(i);
			List<ServerInfo> tempList= new ArrayList<ServerInfo>();
			for(ServerInfo serverInfo:serverInfoList){
				if(serverid.equals(serverInfo.getServerid())){
					tempList.add(serverInfo);
				}
			}
			serverInfoList.removeAll(tempList);
			List<String> tempList2= new ArrayList<String>();
			for(String roomid:remoteChatroomInfoMap.keySet()){
				if(serverid.equals(remoteChatroomInfoMap.get(roomid).getManagingServer().getServerid())){
					tempList2.add(roomid);
				}
			}
			for(String roomid:tempList2){
				remoteChatroomInfoMap.remove(roomid);
			}
=======
		for (int i = 0; i < serverids.size(); i++) {
			String serverid = (String) serverids.get(i);
			for(ServerInfo serverInfo:serverInfoList){
				if(serverid.equals(serverInfo.getServerid())){
					serverInfoList.remove(serverInfo);
				}
			}
			for(String roomid:remoteChatroomInfoMap.keySet()){
				if(serverid.equals(remoteChatroomInfoMap.get(roomid).getManagingServer().getServerid())){
					remoteChatroomInfoMap.remove(roomid);
				}
			}
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
		}
	}
}
