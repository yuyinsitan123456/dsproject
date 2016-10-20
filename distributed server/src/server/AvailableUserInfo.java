package server;

public class AvailableUserInfo {
	private String name;
	private String password;
	private boolean userState;
	

	public AvailableUserInfo(String name, String password) {
		super();
		this.name = name;
		this.password = password;
		this.userState = true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name=name;
	}

	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password=password;
	}
	
	public boolean getUserState() {
		return userState;
	}

	public void setUserState(boolean state) {
		this.userState=state;
	}
}
