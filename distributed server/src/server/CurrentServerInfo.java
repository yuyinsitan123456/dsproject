package server;

public class CurrentServerInfo {
	private String serverid;
	private String serverAddress;
	private int clientsPort = 4444;
	private int coordinationPort = 5555;
	private boolean work=true;
<<<<<<< HEAD
	private int number = 0;

	public CurrentServerInfo(String serverid, String serverAddress, int clientsPort, int coordinationPort ,int number) {
=======

	public CurrentServerInfo(String serverid, String serverAddress, int clientsPort, int coordinationPort) {
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
		super();
		this.serverid = serverid;
		this.serverAddress = serverAddress;
		this.clientsPort = clientsPort;
		this.coordinationPort = coordinationPort;
		this.work=true;
<<<<<<< HEAD
		this.number=number;
=======
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
	}

	public String getServerid() {
		return serverid;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public int getClientsPort() {
		return clientsPort;
	}

<<<<<<< HEAD
	public int getNumber() {
		return number;
=======
	public int getCoordinationPort() {
		return coordinationPort;
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
	}
	
	public boolean getWork() {
		return work;
	}
<<<<<<< HEAD
	public int getCoordinationPort() {
		return coordinationPort;
	}
=======
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c

	public void setWork(boolean work) {
		this.work=work;
	}
<<<<<<< HEAD
	public void addNumber() {
		this.number=this.number+1;
	}
	public void deleteNumber() {
		this.number=this.number-1;
	}
=======
>>>>>>> 1fda8c464f619bf4e55479fa196a32b9e809799c
}
