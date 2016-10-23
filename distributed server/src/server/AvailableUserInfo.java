package server;

public class AvailableUserInfo {
	private String name;
	private String password;
<<<<<<< HEAD
	private String server;
=======
	private boolean userState;
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
	

	public AvailableUserInfo(String name, String password) {
		super();
		this.name = name;
		this.password = password;
<<<<<<< HEAD
		this.server="";
=======
		this.userState = true;
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
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
	
<<<<<<< HEAD
	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server=server;
=======
	public boolean getUserState() {
		return userState;
	}

	public void setUserState(boolean state) {
		this.userState=state;
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
	}
}
