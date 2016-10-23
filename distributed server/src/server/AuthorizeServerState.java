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

	public void setServerInfoList(List<CurrentServerInfo> serverInfoList) {
		this.serverInfoList = serverInfoList;
	}

	public Map<String,AvailableUserInfo> getUserInfoMap() {
		return userInfoMap;
	}

	public void setUserInfoMap(Map<String,AvailableUserInfo> userInfoMap) {
		this.userInfoMap = userInfoMap;
	}

	public void addUser(AvailableUserInfo userInfo) {
		userInfoMap.put(userInfo.getName(),userInfo);
	}

	public void addServerInfo(CurrentServerInfo currentServerInfo) {
		serverInfoList.add(currentServerInfo);
	}

	public void deleteServerInfo(CurrentServerInfo currentServerInfo) {
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

	public void deleteServers(Set<String> noworkserverids) {
		for(String noworkserverid:noworkserverids){
			deleteServer(noworkserverid);
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

	public void changeworkstates() {
		for(CurrentServerInfo currentServerInfo:serverInfoList){
			currentServerInfo.setWork(false);
		}
	}

<<<<<<< HEAD
	public void changeuserstate(String identity,String serverid) {
		if(serverInfoList.size()!=0){
			for(CurrentServerInfo serverInfo:serverInfoList){
				if(serverInfo.getServerid().equals(userInfoMap.get(identity).getServer())){
					serverInfo.deleteNumber();
				}
				if(serverInfo.getServerid().equals(serverid)){
					serverInfo.addNumber();
				}
			}
		}
		userInfoMap.get(identity).setServer(serverid);
	}

=======
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
}
