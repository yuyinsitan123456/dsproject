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
	//	private Map<String,Integer> usernumber;

	private AuthorizeServerState() {
		serverInfoList = new ArrayList<>();
		userInfoMap = new HashMap<String,AvailableUserInfo>();
		//		usernumber = new HashMap<String,Integer>();
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

	//	public Map<String,Integer> getUsernumber() {
	//		return usernumber;
	//	}
	//
	//	public void setUsernumber(String serverid,Integer number) {
	//		this.usernumber.put(serverid, number);
	//	}

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

}
