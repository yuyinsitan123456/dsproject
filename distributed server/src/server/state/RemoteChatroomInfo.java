package server.state;

public class RemoteChatroomInfo extends ChatroomInfo{
	private ServerInfo managingServer;

	public RemoteChatroomInfo(String chatroomId,ServerInfo managingServer) {
		super(chatroomId);
		this.managingServer = managingServer;
	}

	public ServerInfo getManagingServer() {
		return managingServer;
	}

}
