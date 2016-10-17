package server.state;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
@SuppressWarnings("unchecked")
public class Message {
	// for editing message
	public Message() {
		super();
	}

	public static JSONObject getLockIdentityRequest(String serverid,String identity) {
		JSONObject lockIdentityRequest = new JSONObject();
		lockIdentityRequest.put("type", "lockidentity");
		lockIdentityRequest.put("serverid", serverid);
		lockIdentityRequest.put("identity", identity);
		return lockIdentityRequest;
	}

	public static JSONObject getLockIdentityResponse(String serverid,String identity,String locked) {
		JSONObject lockIdentityResponse = new JSONObject();
		lockIdentityResponse.put("type", "lockidentity");
		lockIdentityResponse.put("serverid", serverid);
		lockIdentityResponse.put("identity", identity);
		lockIdentityResponse.put("locked", locked);
		return lockIdentityResponse;
	}

	public static JSONObject getClientReply(String approved) {
		JSONObject clientReply = new JSONObject();
		clientReply.put("type", "newidentity");
		clientReply.put("approved", approved);
		return clientReply;
	}

	public static JSONObject getReleasingIdentity(String serverid,String identity) {
		JSONObject releasing = new JSONObject();
		releasing.put("type", "releaseidentity");
		releasing.put("serverid", serverid);
		releasing.put("identity", identity);
		return releasing;
	}

	public static JSONObject getChangeRoomReply(String identity,String former,String roomid) {
		JSONObject changeRoom = new JSONObject();
		changeRoom.put("type", "roomchange");
		changeRoom.put("identity", identity);
		changeRoom.put("former", former);
		changeRoom.put("roomid", roomid);
		return changeRoom;
	}

	public static JSONObject getList(List<String> rooms) {
		JSONObject list = new JSONObject();
		list.put("type", "roomlist");
		JSONArray roomsJSONs=new JSONArray();
		for(String room:rooms){
			roomsJSONs.add(room);
		}
		list.put("rooms", roomsJSONs);
		return list;
	}

	public static JSONObject getWho(String roomid,List<String> identities,String owner) {
		JSONObject who = new JSONObject();
		who.put("type", "roomcontents");
		who.put("roomid", roomid);
		JSONArray idJSONs=new JSONArray();
		for(String identity:identities){
			idJSONs.add(identity);
		}
		who.put("identities",idJSONs);
		who.put("owner", owner);
		return who;
	}

	public static JSONObject getRoomCreateReply(String roomid,String approved) {
		JSONObject roomCreatFail = new JSONObject();
		roomCreatFail.put("type", "createroom");
		roomCreatFail.put("roomid", roomid);
		roomCreatFail.put("approved", approved);
		return roomCreatFail;
	}

	public static JSONObject getLockRoomRequest(String serverid,String roomid) {
		JSONObject lockRoomRequest = new JSONObject();
		lockRoomRequest.put("type", "lockroomid");
		lockRoomRequest.put("serverid", serverid);
		lockRoomRequest.put("roomid", roomid);
		return lockRoomRequest;
	}

	public static JSONObject getLockRoomRespone(String serverid,String roomid,String locked) {
		JSONObject lockRoomRespone = new JSONObject();
		lockRoomRespone.put("type", "lockroomid");
		lockRoomRespone.put("serverid", serverid);
		lockRoomRespone.put("roomid", roomid);
		lockRoomRespone.put("locked", locked);
		return lockRoomRespone;
	}

	public static JSONObject getReleaseRoomRespone(String serverid,String roomid,String approved) {
		JSONObject releaseRoomRespone = new JSONObject();
		releaseRoomRespone.put("type", "releaseroomid");
		releaseRoomRespone.put("serverid", serverid);
		releaseRoomRespone.put("roomid", roomid);
		releaseRoomRespone.put("approved", approved);
		return releaseRoomRespone;
	}

	public static JSONObject getMoveJoinReply(String identity, String former, String roomid) {
		JSONObject movejoin = new JSONObject();
		movejoin.put("type", "movejoin");
		movejoin.put("former", former);
		movejoin.put("roomid", roomid);
		movejoin.put("identity", identity);
		return movejoin;
	}

	public static JSONObject getRouteReply(String roomid, String host, String port) {
		JSONObject routeReply = new JSONObject();
		routeReply.put("type", "route");
		routeReply.put("roomid", roomid);
		routeReply.put("host", host);
		routeReply.put("port", port);
		return routeReply;
	}

	public static JSONObject getDeleteReply(String roomid, String approved) {
		JSONObject delete = new JSONObject();
		delete.put("type", "deleteroom");
		delete.put("roomid", roomid);
		delete.put("approved", approved);
		return delete;
	}

	public static JSONObject getDeleteRespone(String serverid, String roomid) {
		JSONObject deleteResponed = new JSONObject();
		deleteResponed.put("type", "deleteroom");
		deleteResponed.put("serverid", serverid);
		deleteResponed.put("roomid", roomid);
		return deleteResponed;
	}

	public static JSONObject getMessage(String identity, String content) {
		JSONObject message = new JSONObject();
		message.put("type", "message");
		message.put("identity", identity);
		message.put("content", content);
		return message;
	}

	public static JSONObject getServerchange(String approved,String serverid) {
		JSONObject serverchange = new JSONObject();
		serverchange.put("type", "serverchange");
		serverchange.put("approved", approved);
		serverchange.put("serverid", serverid);
		return serverchange;
	}
}
