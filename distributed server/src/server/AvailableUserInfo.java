package server;

public class AvailableUserInfo {
	private String name;
	private String password;
	private String server;
	

	public AvailableUserInfo(String name, String password) {
		super();
		this.name = name;
		this.password = password;
		this.server="";
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
	
	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server=server;
	}
}