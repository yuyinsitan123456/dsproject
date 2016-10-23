package server.state;

import java.util.LinkedList;
import java.util.List;

public class LocalChatroomInfo extends ChatroomInfo{
	private String ownerIdentity;
	private List<String> members;

	public LocalChatroomInfo(String chatroomId) {
		super(chatroomId);
		this.members =new LinkedList<String>();
	}
	
	public LocalChatroomInfo(String chatroomId,String ownerIdentity) {
		super(chatroomId);
		this.ownerIdentity = ownerIdentity;
		this.members =new LinkedList<String>();
	}

	public String getOwnerIdentity() {
		return ownerIdentity;
	}

	public List<String> getMembers() {
		return members;
	}
	
	public LocalChatroomInfo addMember(String identity) {
		this.members.add(identity);
		return this;
	}
	
	public LocalChatroomInfo deleteMember(String identity) {
		this.members.remove(identity);
		return this;
	}
}
