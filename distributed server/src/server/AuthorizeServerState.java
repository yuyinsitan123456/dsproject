package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AuthorizeServerState {
	//store different server state
	private static AuthorizeServerState instance;
	private List<CurrentServerInfo> serverInfoList;
	private Map<String,AvailableUserInfo> userInfoMap;

	private AuthorizeServerState() {
		serverInfoList = new ArrayList<>();
		userInfoMap = new HashMap<String,AvailableUserInfo>();
		AvailableUserInfo availableUserInfo1 = new AvailableUserInfo("a","a");
		AvailableUserInfo availableUserInfo2 = new AvailableUserInfo("b","b");
		AvailableUserInfo availableUserInfo3 = new AvailableUserInfo("c","c");
		AvailableUserInfo availableUserInfo4 = new AvailableUserInfo("d","d");
		userInfoMap.put(availableUserInfo1.getName(),availableUserInfo1);
		userInfoMap.put(availableUserInfo2.getName(),availableUserInfo2);
		userInfoMap.put(availableUserInfo3.getName(),availableUserInfo3);
		userInfoMap.put(availableUserInfo4.getName(),availableUserInfo4);
	}

	public static synchronized AuthorizeServerState getInstance() {
		if(instance == null) {
			instance = new AuthorizeServerState();
		}
		return instance;
	}

	public List<CurrentServerInfo> getServerInfoList() {
		return serverInfoList;
	}

	public synchronized void setServerInfoList(List<CurrentServerInfo> serverInfoList) {
		this.serverInfoList = serverInfoList;
	}

	public Map<String,AvailableUserInfo> getUserInfoMap() {
		return userInfoMap;
	}

	public synchronized void setUserInfoMap(Map<String,AvailableUserInfo> userInfoMap) {
		this.userInfoMap = userInfoMap;
	}

	public synchronized void addUser(AvailableUserInfo userInfo) {
		userInfoMap.put(userInfo.getName(),userInfo);
	}

	public synchronized void addServerInfo(CurrentServerInfo currentServerInfo) {
		serverInfoList.add(currentServerInfo);
	}

	public synchronized void deleteServerInfo(CurrentServerInfo currentServerInfo) {
		serverInfoList.remove(currentServerInfo);
	}

	public void deleteServer(String serverid) {
		for(CurrentServerInfo currentServerInfo:serverInfoList){
			if(serverid.equals(currentServerInfo.getServerid())){
				serverInfoList.remove(currentServerInfo);
				break;
			}
		}
	}

	public synchronized void deleteServers(Set<String> noworkserverids) {
		for(String noworkserverid:noworkserverids){
			deleteServer(noworkserverid);
			for (String username:userInfoMap.keySet()){
				if(noworkserverid.equals(userInfoMap.get(username).getServer())){
					userInfoMap.get(username).setServer("");
				}
			}
		}
	}

	public void changeworkstate(String serverid,boolean work) {
		for(CurrentServerInfo currentServerInfo:serverInfoList){
			if(serverid.equals(currentServerInfo.getServerid())){
				currentServerInfo.setWork(work);
				break;
			}
		}
	}

	public synchronized void changeworkstates() {
		for(CurrentServerInfo currentServerInfo:serverInfoList){
			currentServerInfo.setWork(false);
		}
	}

	public synchronized void changeuserstate(String username,String serverid) {
		if(serverInfoList.size()!=0){
			for(CurrentServerInfo serverInfo:serverInfoList){
				if(serverInfo.getServerid().equals(userInfoMap.get(username).getServer())){
					serverInfo.deleteNumber();
				}
				if(serverInfo.getServerid().equals(serverid)){
					serverInfo.addNumber();
				}
			}
		}
		userInfoMap.get(username).setServer(serverid);
	}

}
